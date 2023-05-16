/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ilyriadz.games.mawdja.core.xyz;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import static ilyriadz.games.mawdja.gutil.Gutil.meters;
import javafx.geometry.Point3D;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javax.vecmath.Vector3f;

/**
 *
 * @author kiradja
 */
public class SphereObject extends GameObject3D
{

    public SphereObject(GameManager3D gameManager, Point3D position,
            double radius) {
        super(gameManager, position, 
            new Sphere(radius));
    }
    
    public SphereObject(GameManager3D gameManager, double posX, double posY, 
        double posZ, double radius, int divisions) 
    {
        super(gameManager, new Point3D(posX, posY, posZ), 
            new Sphere(radius, divisions));
    }

    @Override
    public Sphere node() {
        return (Sphere) super.node();
    }
      
    @Override
    protected CollisionShape createShape() 
    {
        var box = node();
        CollisionShape collisionShape = 
            new SphereShape(meters(node().getRadius()));
        
        return collisionShape;
    }
    
}
