

package ilyriadz.games.mawdja.gutil;

/**
 *
 * @author ilyria
 */
public class Indexes implements Comparable<Indexes>
{
    public final int begin;
    public final int end;

    public Indexes(int begin, int end)
    {
        this.begin = begin;
        this.end = end;
    } // end constructor
    
    @Override
    public int compareTo(Indexes indexes)
    {
        int compare = Integer.compare(begin, indexes.begin);
        
        if ( compare != 0)
            return compare;
            
        return Integer.compare(end, indexes.end);
    } // end method

    @Override
    public String toString()
    {
        return "Indexes{" + begin + ", " + end + '}';
    }

    
} // end class Indexes
