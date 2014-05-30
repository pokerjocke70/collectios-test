import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * @author Joakim Sundqvist
 * @since 29/05/14
 */
public class IntegerSupplier implements Supplier<Integer> {

    private AtomicInteger start = new AtomicInteger();

    @Override
    public Integer get() {
        final int i = start.getAndIncrement();
        return i * i;
    }
}
