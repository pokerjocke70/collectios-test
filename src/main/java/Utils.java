/**
 * @author Joakim Sundqvist
 * @since 29/05/14
 */
public final class Utils {


    private Utils(){}


    public static boolean isPrime(int n) {
        for(int i=2;i<n;i++) {
            if(n%i==0)
                return false;
        }
        return true;
    }
}
