/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ilyriadz.games.mawdja.core;

import ilyriadz.games.mawdja.gutil.PathUtil;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.World;

/**
 *
 * @author kiradja
 */
public class SvgObject extends GameObject
{ 
    private final String svgFileStr;
    private final Class<?> clazz;
    
    public SvgObject(GameManager gameManager, Image image, String svg, Class<?> clazz, double posx, double posy) 
    {
        super(gameManager, new Point2D(posx, posy), 
            new Box(image.getWidth(), image.getHeight(), 0));
        
        this.svgFileStr = svg;
        this.clazz = clazz;

        PhongMaterial pm = new PhongMaterial(Color.WHITE);
        pm.setDiffuseMap(image);
        pm.setSpecularMap(image);
        var box = node();
        box.setTranslateX(box.getTranslateX() + box.getWidth() / 2);
        box.setTranslateY(box.getTranslateY() + box.getHeight() / 2);
        box.setMaterial(pm);
    }
    
    @Override
    protected Shape createShape() 
    {
        return PathUtil.createShape(PathUtil.fromSvg(
            clazz.getResourceAsStream(svgFileStr)));
    }

    @Override
    public Box node() {
        return (Box) super.node();
    }
    
    
    
}
