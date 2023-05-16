/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ilyriadz.games.mawdja.core;

import static ilyriadz.games.mawdja.gutil.Gutil.meters;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;

/**
 *
 * @author kiradja
 */
public class RegionRectObject extends RectObject
{    
    private double physicWith;
    private double physicHeight;
    
    public RegionRectObject(GameManager gameManager, double posX, double posY,  double width, double height,
            double physicWith, double physicHeight) 
    {
        super(gameManager, posX, posY, width, height);
        this.physicWith = physicWith;
        this.physicHeight = physicHeight;
    }
    
    @Override
    public Shape createShape() 
    {
        var node = node();
        var rect = new PolygonShape();
        rect.setAsBox(meters(physicWith() / 2), meters(physicHeight() / 2));
        return rect;
    }

    public void setPhysicWith(double physicWith) {
        this.physicWith = physicWith;
    }

    public void setPhysicHeight(double physicHeight) {
        this.physicHeight = physicHeight;
    }

    public double physicWith() {
        return physicWith;
    }

    public double physicHeight() {
        return physicHeight;
    }
    
    
    
}
