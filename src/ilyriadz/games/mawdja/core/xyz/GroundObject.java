/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ilyriadz.games.mawdja.core.xyz;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import ilyriadz.games.mawdja.core.GameManager;
import static ilyriadz.games.mawdja.gutil.Gutil.meters;
import java.util.HashSet;
import java.util.Set;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javax.vecmath.Vector3f;

/**
 *
 * @author kiradja
 */
public class GroundObject extends BoxObject
{
    private Set<GameObject3D> gameObjects = new HashSet<>();
    private boolean isOnGround = false;

    public GroundObject(GameManager3D gameManager, Point3D position,
            double width, double height, double depth) {
        super(gameManager, position, width, height, depth);
        setMass(0.0);
        setAlwaysManaged(true);
    }
    
    public GroundObject(GameManager3D gameManager, double posX, double posY, 
        double posZ, double width, double height, double depth) 
    {
        this(gameManager, new Point3D(posX, posY, posZ), width, height, depth);
    }
    
    public void addGameObject(GameObject3D gameObject)
    {
        if (!gameObjects.contains(gameObject))
            gameObjects.add(gameObject);
    }

    /*@Override
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
    }*/

    @Override
    public void update(float delta) {
        if (rigidBody() == null)
            return;
        
        if (!rigidBody.isStaticObject())
            super.update(delta);
        else
            contactObjects().values().forEach(contact -> contact.intersect(this));
        
        gameObjects.forEach(g ->
        {
            if (node().getBoundsInParent().intersects(g.node().getBoundsInParent())
                && node().getTranslateZ() > g.node().getTranslateZ())
            {
                if (!isOnGround)
                {
                    g.addGroundToCount();
                    isOnGround = true;
                }
            }
            else
            {
                if (isOnGround)
                {
                    g.removeGroundFromCount();
                    isOnGround = false;
                }
            }
        });
    }
    
    
    
}
