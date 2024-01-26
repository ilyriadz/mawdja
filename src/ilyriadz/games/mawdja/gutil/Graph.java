
package ilyriadz.games.mawdja.gutil;

import java.util.ArrayList;
import java.util.List;
import org.jbox2d.common.Vec2;

/**
 *
 * @author ilyria
 */
public class Graph 
{
    private final int verticesNumber;
    private int edgesNumber;
    private List<Integer>[] adjacency;
    private final Vec2[] positions;

    public Graph(int verticesNumber)
    {
        this.verticesNumber = verticesNumber;
        positions = new Vec2[verticesNumber];
        
        initAdjacency();
    }
    
    @SuppressWarnings({"unchecked"})
    private void initAdjacency()
    {
        adjacency = (ArrayList<Integer>[])new ArrayList[verticesNumber];
        for (int i = 0; i < verticesNumber; i++)
        {
            adjacency[i] = new ArrayList<>();
        } // end for
    } // end method
    
    public int verticesNumber() {return verticesNumber;}
    public int edgesNumber() {return edgesNumber;}
    
    public Vec2 pixelsVecOf(int v)
    {
        return new Vec2(positions[v]);
    } // end method
    
    public Vec2 metersVecOf(int v)
    {
        var vec2 = positions[v];
        return new Vec2(Gutil.meters(vec2.x), Gutil.meters(vec2.y));
    } // end method
    
    public void setPosition(int v, double posx, double posy)
    {
        if (positions[v] != null)
            positions[v].set((float)posx, (float)posy);
        else
            positions[v] = new Vec2((float)posx, (float)posy);
    }
    
    public void addEdge(int v, int w)
    {
        if (adjacency[v].contains(w))
            return;
        adjacency[v].add(w);
        adjacency[w].add(v);
        edgesNumber++;
    }
    
    public Iterable<Integer> adjacency(int i) {return adjacency[i];}

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append("verticesNumber:");
        builder.append(verticesNumber);
        builder.append('\n');
        builder.append("edgesNumber:");
        builder.append(edgesNumber);
        builder.append('\n');
        for (int i = 0; i < adjacency.length; i++)
        {
            var vec = positions[i];
            builder.append(i);
            if (vec != null)
                builder.append(String.format("(%.0f, %.0f)", vec.x, vec.y ));
            builder.append(": ");
            final var adj = adjacency[i];
            for (int j = 0; j < adj.size(); j++)
            {
                builder.append(adj.get(j));
                builder.append(' ');
            } // end for
            builder.append('\n');
        } // end for
        return builder.toString();
    } // end method   
    
    public static void main(String[] args)
    {
        Graph g = new Graph(42);
        
        g.setPosition(0, 1302, 3130);
        g.addEdge(0, 2);
        g.addEdge(0, 1);
        
        g.setPosition(1, 1498, 2912);
        g.addEdge(1, 2);
        
        g.setPosition(2, 1266, 2878);
        g.addEdge(2, 3);
        
        g.setPosition(3, 1244, 2516);
        g.addEdge(3, 4);
        g.addEdge(3, 8);
        g.addEdge(3, 11);
        g.addEdge(3, 12);
        g.addEdge(3, 16);
        
        g.setPosition(4, 954, 2646);
        g.addEdge(4, 5);
        
        g.setPosition(5, 724, 2706);
        g.addEdge(5, 6);
        g.addEdge(5, 13);
        
        g.setPosition(6, 478, 2706);
        g.addEdge(6, 7);
        
        g.setPosition(7, 478, 2890);
        
        
        
        System.err.println(g);
    }
} // end class Graph
