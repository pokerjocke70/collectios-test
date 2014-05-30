import com.gs.collections.impl.list.mutable.FastList;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

/**
 * @author Joakim Sundqvist
 * @since 29/05/14
 */
public class FastListMaker implements FCollection {

    public Collection<Integer> generateList(final int nbrOfStrings, Supplier<Integer> supplier) {
        return Collections.unmodifiableCollection(FastList.newWithNValues(nbrOfStrings, supplier::get));
    }

}
