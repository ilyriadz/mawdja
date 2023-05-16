/**
 * this is private
 * owner: ilyes sadaoui (ilyriadz)
 */
package ilyriadz.games.mawdja.fsm;

import ilyriadz.games.mawdja.gutil.GraphPath;
import ilyriadz.games.mawdja.gutil.Gutil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.jbox2d.common.Vec2;

/**
 *
 * @author ilyria
 */
public class FlowGraphPath 
{
    private final List<Integer> stack = new ArrayList<>();
    private final GraphPath graph;
    private Vec2 nextPoint;
    private Runnable onFinish;
    
    public FlowGraphPath(GraphPath graph, int start, int end)
    {
        this.graph = graph;
        
        var set = graph.pathsTo(start, end);
        
        Comparator<List<Integer>> c = 
             (List<Integer> o1, List<Integer> o2) -> Integer.compare(o1.size(), o2.size());
        
        List<List<Integer>> list = set.stream()
                .collect(Collectors.toList());
        
        var minIndex = 0;
        var minSize = list.get(0).size();
        for (int i = 0; i < list.size(); i++) {
            if (minSize > list.get(i).size())
            {
                minIndex = i;
                minSize = list.get(i).size();
            }
        }
        
        //Collections.reverse(list.get(minIndex));
        stack.addAll(list.get(minIndex));
        //System.out.println(list.get(minIndex));
    }
    
    public final Vec2 nextPoint()
    {
        if (stack.isEmpty())
            return nextPoint;
        
        
        if (nextPoint == null)
        {
            var index = stack.get(0);
            var pos = graph.getPosition(index);
            nextPoint = new Vec2(
                Gutil.meters(pos.getX()), Gutil.meters(pos.getY()));
            //System.out.println(graph.pixelsVecOf(stack.get(0)));
            stack.remove(0);
        }
        
        return nextPoint;
    } // end method
    
    public void clearCurrentPoint()
    {
        if (stack.isEmpty())
        {
            if (onFinish != null)
                onFinish.run();
            return;
        } // end if
        nextPoint = null;
    }
    
    public void setOnFinish(Runnable run)
    {
        onFinish = run;
    } // end method
} // end class FlowGraphPath
