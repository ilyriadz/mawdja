/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ilyriadz.games.mawdja.core;

import javafx.geometry.Point2D;
import javafx.scene.PointLight;
import javafx.scene.paint.Color;
import org.jbox2d.collision.shapes.Shape;

/**
 *
 * @author kiradja
 */
public class LightObject extends GameObject
{
    public LightObject(double x, double y, Color lightColor) 
    {
        super(null, new Point2D(x, y), new PointLight(lightColor));
    }
    
    @Override
    protected Shape createShape()
    {
        return null;
    }

    @Override
    public void create() 
    {
    }  

    @Override
    public PointLight node() 
    {
        return (PointLight) super.node();
    }
} // end class
