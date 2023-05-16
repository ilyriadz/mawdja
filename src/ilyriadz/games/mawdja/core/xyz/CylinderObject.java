/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ilyriadz.games.mawdja.core.xyz;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CylinderShape;
import static ilyriadz.games.mawdja.gutil.Gutil.meters;
import javafx.geometry.Point3D;
import javafx.scene.shape.Cylinder;
import javax.vecmath.Vector3f;

/**
 *
 * @author kiradja
 */
public class CylinderObject extends GameObject3D
{

    public CylinderObject(GameManager3D gameManager, Point3D position,
            double radius, double height) {
        super(gameManager, position, 
            new Cylinder(radius, height));
    }
    
    public CylinderObject(GameManager3D gameManager, double posX, double posY, 
        double posZ, double radius, double height) 
    {
        super(gameManager, new Point3D(posX, posY, posZ), 
            new Cylinder(radius, height));
    }

    @Override
    public Cylinder node() {
        return (Cylinder) super.node();
    }
      
    @Override
    protected CollisionShape createShape() 
    {
        var cylinder = node();
        var radius = meters(cylinder.radiusProperty().floatValue());
        CollisionShape collisionShape = 
            new CylinderShape(new Vector3f(radius,
                meters(cylinder.heightProperty().floatValue() / 2f), radius));
        
        return collisionShape;
    }
    
}
