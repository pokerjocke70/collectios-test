import groovy.util.logging.Slf4j

import java.util.function.Supplier

/**
 *
 * @author Joakim Sundqvist
 * @since 29/05/14
 */

@Slf4j
class GroovyListMaker implements FCollection {


    @Override
    Collection<Integer> generateList(int nbrOfStrings, Supplier<Integer> supplier) {
        def list = []
        (0..nbrOfStrings).each {
            list << supplier.get()
        }
        list.asImmutable()
    }

}




