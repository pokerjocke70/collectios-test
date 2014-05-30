import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author Joakim Sundqvist
 * @since 29/05/14
 */
public class ArrayListMaker implements FCollection {


    @Override
    public Collection<Integer> generateList(int nbrOfStrings, Supplier<Integer> supplier) {
        final List<Integer> list = new ArrayList<>(nbrOfStrings);
        for (int i = 0; i < nbrOfStrings; i++) {
            list.add(supplier.get());
        }
        return Collections.unmodifiableCollection(list);
    }


}
