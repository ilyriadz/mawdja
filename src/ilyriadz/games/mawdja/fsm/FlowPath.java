
package ilyriadz.games.mawdja.fsm;

import org.jbox2d.common.Vec2;
import javafx.scene.Group;
import javafx.scene.shape.Line;
/**
 *
 * @author ilyes
 */
public class FlowPath 
{
    private Vec2 start;
    private Vec2 end;
    private float radius;
    
    public FlowPath(int width, int height, Group group)
    {
        radius = 20;
        start = new Vec2( 0, height / 3 );
        end = new Vec2( width, 2 * width / 3 );
        
        if (group != null)
        {
            Line line = new Line();
            line.setStartX( start.x );
            line.setStartY( start.y );
            line.setEndX( end.x );
            line.setEndY( end.y );
        
            group.getChildren().add( line );
        } // end if
    } // end Path default constructor

    public Vec2 getStart() {
        return start;
    }

    public void setStart(Vec2 start) {
        this.start = start;
    }

    public Vec2 getEnd() {
        return end;
    }

    public void setEnd(Vec2 end) {
        this.end = end;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
    
    
} // end class FlowPath
