/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ilyriadz.games.mawdja.core;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Box;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.World;

/**
 *
 * @author kiradja
 */
public class GhostObject extends GameObject
{

    public GhostObject(double posX, double posY, double width, 
            double height) 
    {
        super(null, new Point2D(posX, posY), new Box(width, height, 0));
    }
    
    @Override
    protected Shape createShape() 
    {
        return null;
    }

    @Override
    public Box node() {
        return (Box) super.node();
    }
    
    

    @Override
    public void create() {
    }
    
    
    
}
