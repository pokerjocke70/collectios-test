/**
 * @author Joakim Sundqvist
 * @since 30/05/14
 */
public class CircuitBreakerException extends RuntimeException {

    public CircuitBreakerException(String message) {
        super(message);
    }
}
