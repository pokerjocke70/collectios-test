import com.google.common.util.concurrent.SettableFuture;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Request;
import com.ning.http.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

/**
 * CircuitBreaker implementation of AsyncHttpClient
 * <p>
 * See: http://martinfowler.com/bliki/CircuitBreaker.html
 *
 * @author Joakim Sundqvist
 * @since 30/05/14
 */
public class CircuitBreaker {

    public static final int DEFAULT_THRESHOLD = 10;

    public static final int DEFAULT_HALF_OPEN_AFTER = 5000;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AtomicLong failureCount = new AtomicLong();

    private final AtomicLong lastFailure = new AtomicLong();

    private final Time time;

    private final int threshold;

    private final int halfOpenAfter;

    // TODO  - Add option to configure it
    private final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    /**
     * Creates an instance of CircuitBreaker
     *
     * @param time          the time impl to use
     * @param threshold     the numbers of errors before opening the circuit
     * @param halfOpenAfter the duration in milliseconds before trying to close the circuit again
     */
    public CircuitBreaker(@Nonnull Time time, int threshold, int halfOpenAfter) {
        Objects.requireNonNull(time, "Time must not be null");
        this.time = time;
        this.threshold = threshold;
        this.halfOpenAfter = halfOpenAfter;
        logger.info("Configured this instance with threshold {} and halfOpenAfter {} ms.");
    }

    /**
     * Creates a instance with default values
     *
     * @see CircuitBreaker#DEFAULT_THRESHOLD
     * @see CircuitBreaker#DEFAULT_HALF_OPEN_AFTER
     */
    public CircuitBreaker() {
        this(DEFAULT_THRESHOLD, DEFAULT_HALF_OPEN_AFTER);
    }

    /**
     * Creates an instance with a default {@link Time} implementation
     *
     * @param threshold     the numbers of errors before opening the circuit
     * @param halfOpenAfter the duration in milliseconds before trying to close the circuit again
     */
    public CircuitBreaker(int threshold, int halfOpenAfter) {
        this(new SystemTime(), threshold, halfOpenAfter);
    }

    /**
     * Process a http request
     *
     * @param request the request to process
     * @return a {@link Future}
     * @throws IOException
     */
    public Future<Response> process(@Nonnull final Request request) throws IOException {

        Objects.requireNonNull(request, "Request must not be null");

        // Cache the current failure count
        final int currentFailureCount = failureCount.intValue();

        if (isCircuitOpen(currentFailureCount)) {
            // Circuit open - create future that throws CircuitBreakerException
            return createCircuitBreakerFuture(currentFailureCount);
        }

        return executeInternal(request);
    }

    /**
     * Executes a http request
     *
     * @param request the request to process
     * @return a {@link Future}
     * @throws IOException
     */
    private Future<Response> executeInternal(@Nonnull final Request request) throws IOException {

        logger.debug("Circuit closed, continuing normal operation, URL = {}", request.getUrl());

        return asyncHttpClient.prepareConnect(request.getUrl())
                .setBody(request.getStringData())
                .setMethod(request.getMethod())
                .setFollowRedirects(true)
                .setHeaders(request.getHeaders())
                .setBodyEncoding(request.getBodyEncoding())
                .execute(new AsyncCompletionHandler<Response>() {

                             @Override
                             public Response onCompleted(Response response) throws Exception {
                                 logger.debug("Request completed normally, resetting failureCount. URL = {}", request.getUrl());
                                 failureCount.set(0L);
                                 return response;
                             }

                             @Override
                             public void onThrowable(Throwable t) {
                                 final long count = failureCount.incrementAndGet();
                                 lastFailure.set(time.get());
                                 logger.info("Caught Exception, failureCount is now {}. URL = {}", count, request.getUrl());
                                 throw new RuntimeException(t.getMessage());
                             }
                         }
                );
    }

    /**
     * Creates a {@link java.util.concurrent.Future}
     *
     * @param currentFailureCount the current failure count
     * @return a {@link java.util.concurrent.Future} with a CircuitBreakerException
     */
    private Future<Response> createCircuitBreakerFuture(int currentFailureCount) {
        logger.warn("CircuitBreaker open - failureCount = {}, throwing CircuitBreakerException", currentFailureCount);
        final SettableFuture<Response> responseCompletableFuture = SettableFuture.create();
        responseCompletableFuture.setException(new CircuitBreakerException("CircuitBreaker rule applied - currentFailureCount = " + currentFailureCount + ", threshold = " + threshold));
        return responseCompletableFuture;
    }

    /**
     * Checks the circuit
     *
     * @param currentFailureCount current number of failed operations
     * @return true if the circuit is closed (I.e to many errors and halfOPenAfter has not yet occurred)
     */
    private boolean isCircuitOpen(int currentFailureCount) {
        return currentFailureCount >= threshold && (time.get() - lastFailure.intValue()) < halfOpenAfter;
    }

    @Override
    public String toString() {
        return "CircuitBreaker{" +
                "failureCount=" + failureCount.get() +
                ", lastFailure=" + lastFailure.get() +
                ", time=" + time.getClass().getName() +
                ", threshold=" + threshold +
                ", halfOpenAfter=" + halfOpenAfter +
                '}';
    }
}