import com.google.common.collect.ImmutableList;
import com.gs.collections.impl.list.mutable.FastList;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

/**
 * @author Joakim Sundqvist
 * @since 29/05/14
 */
public class GuavaListMaker implements FCollection {

    public Collection<Integer> generateList(final int nbrOfStrings, Supplier<Integer> supplier) {
        final ImmutableList.Builder<Integer> builder = ImmutableList.builder();
        for (int i = 0; i < nbrOfStrings; i++) {
            builder.add(supplier.get());
        }
        return builder.build();
    }

}
