
package ilyriadz.games.mawdja;

/**
 *
 * @author kiradja
 */
public class Testing
{
    public static void main(String[] args)
    {
        double a = 4000;
        for (int i = 0; a >= 125; i++) 
        {
            System.out.printf("%.2f\t%f%n", a, zDivFactor(4000, 125, a));
            a -= 25;
        }
        
        //System.out.println(zDivFactor(4000, 125, 750));
    }
    
    public static double zDivFactor(double max, double min, double n)
    {
        if (n > max || n < min)
            throw new IllegalArgumentException(String.format(
                "n(%.2f) must be in the range[%.2f, %.2f]", n, min, max));
        return max / n;
    }
}
