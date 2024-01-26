/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ilyriadz.games.mawdja.core;

import static ilyriadz.games.mawdja.gutil.Gutil.meters;
import javafx.geometry.Point2D;
import javafx.scene.shape.Box;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.World;

/**
 *
 * @author kiradja
 */
public class CircleObject extends GameObject
{

    public CircleObject(GameManager gameManager, double radius, double posX, double posY) 
    {
        super(gameManager, new Point2D(posX, posY), new Box(radius, radius, 0));
    }
  
    @Override
    public final Box node() {
        return (Box) super.node();
    }

    @Override
    protected Shape createShape() 
    {
        CircleShape circle = new CircleShape();
        circle.setRadius(meters(node().getWidth() / 2));
        return circle;
    }
    
    
}
