
package ilyriadz.games.mawdja.fsm;

import org.jbox2d.common.Vec2;
import static ilyriadz.games.mawdja.gutil.Gutil.*;
import ilyriadz.games.mawdja.gutil.PerlinNoiseGenerator;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

/**
 *
 * @author ilyes
 */
public class FlowField 
{
    private final Vec2[][] field;
    private final int cols, rows;
    private final int resolution;
    private final Circle circles[][];
    private final Line lines[][];
    
    public FlowField( final int rslt, int width, int height, PerlinNoiseGenerator perlin,
        Group group)
    {
        resolution = rslt;
        cols = width / resolution;
        rows = height / resolution;
        field = new Vec2[ rows ][ cols ];
        circles = new Circle[ rows ][ cols ];
        lines = new Line[ rows ][ cols ];
        
        float xoff = 10;
        
        for ( int i = 0; i < field.length; i++ )
        {
            float yoff = 0;
            for ( int j = 0; j < field[ i ].length; j++ )
            {
                
                double angle = map( perlin.noise2( xoff, yoff ), 0, 1, 0, (float)( Math.PI * 2 ) );
                field[ i ][ j ] = new Vec2( (float)Math.cos( angle ), (float)Math.sin( angle ) );
                angle = Math.toDegrees( angle );
                circles[ i ][ j ] = new Circle( resolution / 2 );
                lines[ i ][ j ] = new Line();
                Circle c1 = circles[ i ][ j ];
                c1.setFill( null );
                c1.setStroke( Color.BLACK );
                c1.setStrokeWidth( 0.5 );
                Line l1 = lines[ i ][ j ];
                
                l1.startXProperty().bind( c1.centerXProperty() );
                l1.startYProperty().bind( c1.centerYProperty() );
                l1.endXProperty().bind( c1.centerXProperty().add( 9 ) );
                l1.endYProperty().bind( c1.centerYProperty() );
                
                Group gr = new Group();
                gr.getChildren().addAll( c1, l1 );
                gr.setRotate( angle );
                gr.setTranslateX( j * resolution  );
                gr.setTranslateY( i * resolution );
                group.getChildren().add( gr );
                yoff += 1;
            } // end for
            xoff += 0.1;
        } // end outer for
            
    } // end FlowField default constructor
    
    public Vec2 lookup( Vec2 v2 )
    {
        int x = (int) v2.x;
        int y = (int) v2.y;
        
        int row = constrain(  y / resolution, 0, rows - 1 );
        int col = constrain(  x / resolution, 0, cols - 1 );
        
        return field[ row ][ col ].clone();
    } // end method lookup
} // end class FlowField
