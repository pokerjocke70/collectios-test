import java.util.Collection;
import java.util.function.Supplier;

/**
 * @author Joakim Sundqvist
 * @since 29/05/14
 */
public interface FCollection {

    Collection<Integer> generateList(int nbrOfStrings, Supplier<Integer> supplier);
}
