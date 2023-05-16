/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ilyriadz.games.mawdja.core.xyz;

import com.bulletphysics.collision.shapes.CollisionShape;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.PointLight;
import javafx.scene.paint.Color;
import org.jbox2d.collision.shapes.Shape;

/**
 *
 * @author kiradja
 */
public class LightObject3D extends GameObject3D
{
    public LightObject3D(double x, double y, double z, Color lightColor) 
    {
        super(null, new Point3D(x, y, z), new PointLight(lightColor));
    }
    
    @Override
    protected CollisionShape createShape()
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
}
