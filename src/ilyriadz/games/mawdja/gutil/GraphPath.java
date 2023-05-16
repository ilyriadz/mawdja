
package ilyriadz.games.mawdja.gutil;

import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.Traverser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import org.jbox2d.common.Vec2;

/**
 *
 * @author ilyria
 */
public class GraphPath 
{
    private final MutableGraph<Integer> graph;
    private final Map<Integer, Point3D> positions = new HashMap<>();
    private final Map<Point2D, Set<List<Integer>>> paths = new HashMap<>();

    public GraphPath()
    {
        graph = GraphBuilder.undirected()
                .build();
    }
    
    public final void addNode(int node)
    {
        graph.addNode(node);
        positions.put(node, Point3D.ZERO);
    }
    
    public final void addNode(int node, double x, double y, double z)
    {
        graph.addNode(node);
        positions.put(node, new Point3D(x, y, z));
    }
    
    public final Point3D getPosition(int node)
    {
        return positions.get(node);
    }
    
    public int nodesCount() {return graph.nodes().size();}
    public int edgesCount() {return graph.edges().size();}
    
    public void setPosition(int v, double posx, double posy, double posz)
    {
        if (graph.nodes().contains(v))
        {
            positions.put(v, new Point3D(posx, posy, posz));
        } // end if
    } // end 
    
    public void addEdge(int v, int w)
    {
        graph.putEdge(v, w);
    }
    
    public void removeNode(int node)
    {
        graph.removeNode(node);
        paths.clear();
    }
    
    public void removeEdge(int node1, int node2)
    {
        boolean modified = graph.removeEdge(node1, node2);
        if (modified)
            paths.clear();
    }
    
    private static List<List<Integer>> allmostPossiblePaths(Graph<Integer> g, Iterable<Integer> it)
    {
        List<Integer> l = new ArrayList<>();
        
        for (var v : it)
            l.add(v);
        
        List<List<Integer>> list = new ArrayList<>();
        list.add(new ArrayList<>());
        var current = list.get(0);
        for (int i = 0; i < l.size() - 1; i++) {
            var v1 = l.get(i);
            var v2 = l.get(i + 1);
            
            if (g.hasEdgeConnecting(v1, v2))
            {
                if (!current.contains(v1))
                    current.add(v1);
                current.add(v2);
            } // end if
            else
            {
                current = new ArrayList<>();
                current.add(v2);
                list.add(current);
            }           
        }
        
        var ll = list.get(0);
        var list2 = new ArrayList<List<Integer>>();
        for (int i = 1; i < list.size(); i++) {
            var cu = list.get(i);
            for (int j = 0; j < ll.size(); j++) {
                var last = ll.size() - (j + 1);
                for (int k = 0; k < cu.size(); k++) {
                    if (g.hasEdgeConnecting(ll.get(last), cu.get(k)))
                    {
                        List<Integer> l0 = new ArrayList<>();
                        l0.addAll(ll.subList(0, ll.size() - j));
                        l0.addAll(cu.subList(k, cu.size()));
                        list2.add(l0);
                    }
                }
                
                        
            }
        }
        
        List<List<Integer>> lastList = new ArrayList<>();
        lastList.add(list.get(0));
        lastList.addAll(list2);
        
        return lastList;
    }
    
    public List<List<Integer>> allPossiblePaths(int node)
    {
        Traverser<Integer> tr = Traverser.forGraph(this.graph);
        var firstList = allmostPossiblePaths(this.graph, tr.breadthFirst(node));
        var secondList = allmostPossiblePaths(this.graph, tr.depthFirstPreOrder(node));
        var lastList = allmostPossiblePaths(this.graph, tr.depthFirstPostOrder(node));
        
        List<List<Integer>> finalList = new ArrayList<>();
        finalList.addAll(firstList);
        finalList.addAll(secondList);
        for (var l : lastList)
            if (l.contains(node))
            {
                Collections.reverse(l);
                finalList.add(l);
            } // end if
        
        return finalList;
    }
    
    public Set<List<Integer>> pathsTo(int source, int destination)
    {
        var key = new Point2D(source, destination);
        
        var setInMap = paths.get(key);
        
        if (setInMap != null)
            return setInMap;
        
        Set<List<Integer>> set = new HashSet<>();
        for (var v : graph.nodes()) 
        {
            var all = allPossiblePaths(v);
            set.addAll(all);
        }
        
        Set<List<Integer>> secondSet = new HashSet<>();
                
                
        secondSet.addAll(set.stream()
            .filter(e -> !e.isEmpty())
            .filter(e -> e.contains(source) && e.contains(destination))
            .collect(Collectors.toList()));
        
        Set<List<Integer>> finalSet = new HashSet<>();
        
        var it = secondSet.iterator();
        
        while (it.hasNext())
        {
            var list = it.next();
            
            int sourceIndex = list.indexOf(source);
            int destinationIndex = list.indexOf(destination);
            
                        
            var subList = list.subList(
                Math.min(sourceIndex, destinationIndex),
                Math.max(sourceIndex, destinationIndex) + 1);
            
            if (subList.isEmpty())
                continue;
            
            if (sourceIndex > destinationIndex)
                Collections.reverse(subList);
            
            finalSet.add(subList);
            
            it.remove();
        } // end for
        
        if (finalSet.isEmpty())
            return null;
        
        
        if (!paths.containsKey(new Point2D(source, destination)))
            paths.put(key, finalSet);
        
        
        return finalSet; 
    }
    
    @Override
    public String toString()
    {
        return graph.toString();
    } // end method 
    
    public static void main(String[] args)
    {
        GraphPath g = new GraphPath();
        
        for (int i = 0; i < 11; i++) {
            g.addNode(i);
        }
        g.addNode(1);
        g.addNode(2);
        g.addNode(3);
        g.addNode(4);
        g.addNode(5);
        g.addNode(6);
        g.addNode(7);
        g.addNode(8);
        g.addNode(9);
        g.addNode(10);
        g.addNode(11);
        
        g.addEdge(1, 3);
        g.addEdge(1, 2);

        g.addEdge(2, 8);
        
        g.addEdge(3, 4);
        
        g.addEdge(4, 2);
        g.addEdge(4, 5);     
        g.addEdge(4, 7);
        
        g.addEdge(5, 6);
        
        g.addEdge(6, 7);
        g.addEdge(6, 11);
        
        g.addEdge(7, 8);
        g.addEdge(7, 10);
        
        g.addEdge(8, 9);
        
        g.addEdge(9, 10);
        
        g.addEdge(10, 11);
        
        g.removeEdge(4, 7);
        
        System.out.println(g.pathsTo(8, 11));
    }
} // end class Graph
