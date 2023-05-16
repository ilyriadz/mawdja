
package ilyriadz.games.mawdja.fsm;

import static ilyriadz.games.mawdja.gutil.Gutil.*;
import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import org.jbox2d.common.Vec2;

/**
 *
 * @author ilyes
 */
public class FlowPaths 
{
    private ArrayList<Vec2> points;
    private double radius;
    private Path path;
    private int nextPoint = 0;
    private boolean loop = false;
    
    public FlowPaths(Group group)
    {
        
        points = new ArrayList<>();
        if (group != null)
        {
            path = new Path();
            radius = 20;
            group.getChildren().add(path);
        } // end if
    } // end FlowPaths default constructor
    
    public void addPoint(float x, float y)
    {
        Vec2 p = new Vec2( x, y );
        points.add( p );
        
        if (path != null)
        {
            if (points.size() == 1)
                path.getElements().add(new MoveTo( p.x, p.y ));
            else
                path.getElements().add( new LineTo( p.x, p.y ) );
        } // end if
    } // end method addPoint

    public ArrayList<Vec2> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Vec2> points) {
        this.points = points;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }
            
    public void nextPoint()
    {
        if ( nextPoint < points.size() -1)
            ++nextPoint;
        else if (loop)
        {
            nextPoint = 0;
        } // end if
    } // end method nextPoint
    
    public int getNextPoint()
    {
        return nextPoint;
    } // end method getNextPoint
    
    public void setNextClosestPoint(Vec2 target)
    {
        double min = distance( points.get( 0 ), target );
        int index = 0;
        
        for ( int i = 0; i < points.size(); i++ )
        {
            double min0 = Math.min(distance( points.get( i ), target ), min );
            if ( min0 < min )
            {
                index = i;
                min = min0;
            } // end if
        } // end for
        
        nextPoint = index;
    } // end method setNextClosestPoint

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }
    
    
} // end method FlowPaths
