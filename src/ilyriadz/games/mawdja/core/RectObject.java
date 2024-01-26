/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ilyriadz.games.mawdja.core;

import static ilyriadz.games.mawdja.gutil.Gutil.meters;
import javafx.geometry.Point2D;
import javafx.scene.shape.Box;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;

/**
 *
 * @author kiradja
 */
public class RectObject extends GameObject
{  
    public RectObject(GameManager gameManager, double posX, double posY, 
            double width, double height) 
    {
        super(gameManager, new Point2D(posX, posY), 
            new Box(width, height, 0));
    }
    
    public RectObject(GameManager gameManager, double posX, double posY, 
            double width, double height, boolean intersectOffset) 
    {
        super(gameManager, new Point2D(posX, posY), 
            new Box(width, height, 0));
        this.intersectOffset = intersectOffset;
    }
    
    
    @Override
    public Box node() {
        return (Box)super.node();
    }

    @Override
    protected Shape createShape() {
        var ps = new PolygonShape();
        var node = node();
        ps.setAsBox(meters(node.getWidth() / 2 - (intersectOffset ? 1 : 0)), 
                meters(node.getHeight() / 2 - (intersectOffset ? 1 : 0)));
        
        return ps;
    }

    

    
}
