/**
 * @author Joakim Sundqvist
 * @since 30/05/14
 */
public class SystemTime implements Time {

    @Override
    public long get() {
        return System.currentTimeMillis();
    }
}
