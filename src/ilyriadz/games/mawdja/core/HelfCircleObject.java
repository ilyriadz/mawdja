/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ilyriadz.games.mawdja.core;

import static ilyriadz.games.mawdja.gutil.Gutil.meters;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.World;

/**
 *
 * @author kiradja
 */
public class HelfCircleObject extends CircleObject
{
    private double shrinkValue = 6;
    
    public HelfCircleObject(GameManager gameManager, double radius, double posX, double posY) 
    {
        super(gameManager, radius, posX, posY);
    }
    
    public HelfCircleObject(GameManager gameManager, double radius, double posX, double posY,
            double shrink) 
    {
        super(gameManager, radius, posX, posY);
        this.shrinkValue = shrink < 1 ? 6 : shrink;
    }
    
    @Override
    protected Shape createShape() 
    {
        CircleShape circle = new CircleShape();
        circle.setRadius(meters(node().getWidth() / (float)shrinkValue));
        return circle;
    }

    public void setShrinkValue(double shrink) {
        this.shrinkValue = shrink;
    }

    public double shrinkValue() 
    {
        return shrinkValue;
    }
 
}
