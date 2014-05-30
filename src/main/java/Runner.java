import com.google.common.base.Strings;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author Joakim Sundqvist
 * @since 29/05/14
 */
public class Runner {


    static final int MAX = 10_000_000;

    private final List<FCollection> list = Arrays.asList(new StaticGroovyListMaker(), new ArrayListMaker(), new GroovyListMaker(), new FastListMaker(), new MutableFastListMaker(), new GuavaListMaker());


    void run() {
        list.stream().forEach((this::runEach));
    }


    void runEach(FCollection collection) {
        System.gc();
        long start = System.currentTimeMillis();

        final Optional<Integer> max = collection.generateList(MAX, new IntegerSupplier()).parallelStream().max(Integer::compareTo);
        long stop = System.currentTimeMillis();
        System.out.printf("%s : %s - Max = %s -------- Duration %s ms\n", pad(35, Thread.currentThread().getName()), pad(25, collection.getClass().getName()), max.get(), stop - start);
    }


    private static String pad(int length, String original){
        return Strings.padEnd(original, length, ' ');
    }


    public static void main(String[] args) {

        final Runner runner = new Runner();

        System.out.println(Strings.padEnd("Starting..     ", 150, '-'));

        for (int i = 0; i < 10; i++) {
            runner.run();
            System.out.println(Strings.padEnd(Thread.activeCount() + " threads.    ", 150, '-'));
        }
    }
}
