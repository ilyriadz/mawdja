/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ilyriadz.games.mawdja.core.xyz;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.CompoundShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.linearmath.Transform;
import ilyriadz.games.mawdja.gutil.Gutil;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javax.vecmath.Vector3f;

/**
 *
 * @author kiradja
 */
public class CompoundObject extends GameObject3D
{
    private List<CollisionShape> collisionShapesList = new ArrayList<>();
    
    public CompoundObject(GameManager3D gameManager, Point3D position)
    {
        super(gameManager, position, new Group());
        setFixedRotation(false);
    }

    @Override
    public Group node()
    {
        return (Group) super.node();
    }

    @Override
    protected CollisionShape createShape() 
    {
        /*Box box = new Box(25, 25, 25);
        Sphere sp = new Sphere(25);
        sp.setTranslateZ(-25);//*/
        
        CompoundShape compoundShape = new CompoundShape();
        
        compoundShape.addChildShape(new Transform(), new BoxShape(new Vector3f(
                Gutil.meters(25 / 2), Gutil.meters(25 / 2), Gutil.meters(25 / 2))));
        
        /*Transform t = new Transform();
        t.origin.z = Gutil.meters(25);
        //compoundShape.addChildShape(t, new SphereShape(Gutil.meters(25)));
        
        node().getChildren().addAll(box);//*/
       
        return compoundShape;
    }
    
    
}
