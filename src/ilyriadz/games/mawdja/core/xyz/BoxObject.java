/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ilyriadz.games.mawdja.core.xyz;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import ilyriadz.games.mawdja.core.GameManager;
import static ilyriadz.games.mawdja.gutil.Gutil.meters;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.shape.Box;
import javax.vecmath.Vector3f;

/**
 *
 * @author kiradja
 */
public class BoxObject extends GameObject3D
{

    public BoxObject(GameManager3D gameManager, Point3D position,
            double width, double height, double depth) {
        super(gameManager, position, 
            new Box(width, height, depth));
    }
    
    public BoxObject(GameManager3D gameManager, double posX, double posY, 
        double posZ, double width, double height, double depth) 
    {
        super(gameManager, new Point3D(posX, posY, posZ), 
            new Box(width, height, depth));
    }

    @Override
    public Box node() {
        return (Box) super.node();
    }
      
    @Override
    protected CollisionShape createShape() 
    {
        var box = node();
        CollisionShape collisionShape = new BoxShape(
            new Vector3f(
                meters(box.widthProperty().floatValue() / 2),
                meters(box.heightProperty().floatValue() / 2),
                meters(box.depthProperty().floatValue() / 2)
            ));
        
        return collisionShape;
    }
    
}
