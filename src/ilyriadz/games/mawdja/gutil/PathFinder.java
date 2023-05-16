
package ilyriadz.games.mawdja.gutil;

import ilyriadz.games.mawdja.core.GameObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import org.jbox2d.common.Vec2;

/**
 *
 * @author ilyria
 */
public class PathFinder 
{
    private boolean[] marked;
    private int[] edgeTo;
    private final int source;

    public PathFinder(Graph g, int source)
    {
        this.source = source;
        
        var verticesNumber = g.verticesNumber();
        marked = new boolean[verticesNumber];
        edgeTo = new int[verticesNumber];
        find(g, source);
    } // end constructor
    
    public PathFinder(Graph g, GameObject b)
    {
        List<Vec2> list = new ArrayList<>();
        HashMap<Vec2, Integer> map = new HashMap<>();
        for (int i = 0; i < g.verticesNumber(); i++)
        {
            var vec = g.pixelsVecOf(i);
            map.put(vec, i);
            list.add(vec);
        } // end for
        
        var position = new Vec2((float)b.node().getTranslateX(), 
            (float)b.node().getTranslateY());
        var minVec = list.get(0);
        
        for (int i = 1; i < list.size(); i++)
        {
            var vec = list.get(i);
            var d1 = Gutil.distance(vec, position);
            var d2 = Gutil.distance(minVec, position);
            
            if (d1 < d2)
                minVec = vec;
        }
        
        this.source = map.get(minVec);
        
        var verticesNumber = g.verticesNumber();
        marked = new boolean[verticesNumber];
        edgeTo = new int[verticesNumber];
        find(g, source);
    } // end constructor
    
    public int source()
    {
        return source;
    } // end method
    
    private void find(Graph g, int v)
    {
        marked[v] = true;
        for (int w : g.adjacency(v))
        {
            if (!marked[w])
            {
                edgeTo[w] = v;
                find(g, w);
            } // end if
        } //end for
    } // end method
    
    public boolean hasPathTo(int v)
    {
        return marked[v];
    } // end method
    
    public Iterable<Integer> pathTo(int v)
    {
        if (!hasPathTo(v)) 
            return null;
        
        var stackPath = new Stack<Integer>();
        for (int i = v; i != source; i = edgeTo[i])
            stackPath.push(i);
        stackPath.push(source);
        return stackPath;
    } // end method
    
    public static void main(String[] args)
    {
        Graph g = new Graph(6);
        g.addEdge(0, 1);
        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(3, 4);
        g.addEdge(3, 5);
        
        PathFinder pf = new PathFinder(g, 5);
        
        System.err.println(pf.pathTo(4));
    }
} // end class PathFinder
