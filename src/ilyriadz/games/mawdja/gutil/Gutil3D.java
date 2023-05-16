/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ilyriadz.games.mawdja.gutil;

import com.bulletphysics.linearmath.Transform;
import ilyriadz.games.mawdja.core.xyz.BoxObject;
import ilyriadz.games.mawdja.core.xyz.CylinderObject;
import ilyriadz.games.mawdja.core.xyz.GameObject3D;
import ilyriadz.games.mawdja.core.xyz.GameWorld3D;
import ilyriadz.games.mawdja.core.xyz.SphereObject;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.geometry.Point3D;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 *
 * @author kiradja
 */
public final class Gutil3D 
{
    private Gutil3D()
    {
        throw new Error("use static methods you don't need an instance");
    }
    
    public static Vector3f quatToEuler(Quat4f q)
    {
        double sqw = q.w * q.w;
        double sqx = q.x * q.x;
        double sqy = q.y * q.y;
        double sqz = q.z * q.z;
        double unit = sqx + sqy + sqz + sqw; // if normalised is one, otherwise is correction factor
        double test = q.x * q.y + q.z * q.w;
        
        Vector3f euler = new Vector3f();
        
        if (test > 0.499 * unit) 
        { // singularity at north pole
            euler.y = (float) Math.toDegrees((2 * Math.atan2(q.x, q.w)));
            euler.z = (float) Math.toDegrees((Math.PI / 2));
            euler.x = 0;
            return euler;
        }
        if (test < -0.499 * unit) 
        { // singularity at south pole
            euler.y = (float) Math.toDegrees((-2 * Math.atan2(q.x, q.w)));
            euler.z = (float) Math.toDegrees((-Math.PI / 2));
            euler.x = 0;
            return euler;
        }
        
        euler.y = (float) Math.toDegrees(Math.atan2(2 * q.y * q.w - 2 * q.x * q.z, sqx - sqy - sqz + sqw));
        euler.z = (float) Math.toDegrees(Math.asin(2 * test / unit));
        euler.x = (float) Math.toDegrees(Math.atan2(2 * q.x * q.w - 2 * q.y * q.z, -sqx + sqy - sqz + sqw));
        
        return euler;
}

    
    public static Vector3f getDegreeAngles(Transform transform)
    {
        var basis = transform.basis;
        
        var xr = Math.atan2(basis.m12, basis.m22);
        
        var c2 = Math.sqrt(basis.m00 * basis.m00 + basis.m01 * basis.m01);
        var yr = Math.atan2(-basis.m02, c2);
        
        var s1 = Math.sin(xr);
        var c1 = Math.cos(xr);
        var zr = Math.atan2(s1 * basis.m20 - c1 * basis.m10, 
            c1 * basis.m11 - s1 * basis.m21);//*/

        return new Vector3f((float)Math.toDegrees(xr), (float)Math.toDegrees(yr), (float)Math.toDegrees(zr));
                //quatToEuler(transform.getRotation(new Quat4f()));
    }
    
    public static double getLinearSpeed(GameObject3D gameObject)
    {
        Objects.requireNonNull(gameObject);
        
        var body = gameObject.rigidBody();
        
        if (body != null)
        {
            Vector3f velocity = new Vector3f();
            body.getLinearVelocity(velocity);
            
            return velocity.length();
        }
        
        return 0.0;
    }
    
    public static void updateAngles(GameObject3D gameObject, Transform tr)
    {
        var angles = Gutil3D.getDegreeAngles(tr);
        
        gameObject.rotX().setAngle(-angles.x);
        gameObject.rotY().setAngle(-angles.y);
        gameObject.rotZ().setAngle(-angles.z);
    }
    
    public static void initBoxFields(GameWorld3D gameWorld, double xpos, double ypos, 
        double posz, double width, double depth, int row, int col,
        Image img, String name, String zoneName, Consumer<BoxObject> consumer)
    {
        BoxObject field[][] = new BoxObject[row][col];
        
        int a = 0;
        double wdiv2 = width / 2;
        
        for (int i = 0; i < field.length; i++) 
        {
            for (int j = 0; j < field[i].length; j++) 
            {
                field[i][j] = new BoxObject(gameWorld.gameManager(), 
                    new Point3D(
                        xpos + wdiv2 + j * width, 
                        ypos + wdiv2 + i * width, 
                        posz), width, width, depth);
                field[i][j].setMass(0);
                field[i][j].material().setDiffuseMap(img);
                //field[i][j].material().setSpecularPower(0);
                //field[i][j].material().setSpecularMap(null);
                field[i][j].material().setSelfIlluminationMap(img);
                
                if (consumer != null)
                    consumer.accept(field[i][j]);
                
                var objName = name + (a++);
                gameWorld.addGameObject(zoneName, objName, field[i][j]);

                gameWorld.getGameZone(zoneName).load(objName);
            } // end for
        } // end for
    }
    
    public static void initSphereFields(GameWorld3D gameWorld, double xpos, double ypos, 
        double posz, double radius, int row, int col,
        Image img, String name, String zoneName, Consumer<SphereObject> consumer)
    {
        SphereObject field[][] = new SphereObject[row][col];
        
        int a = 0;
        double radiusMul2 = radius * 2;
        
        for (int i = 0; i < field.length; i++) 
        {
            for (int j = 0; j < field[i].length; j++) 
            {
                field[i][j] = new SphereObject(gameWorld.gameManager(), 
                    new Point3D(
                        xpos + radius + j * radiusMul2, 
                        ypos + radius + i * radiusMul2,
                        posz), radius);
                field[i][j].setMass(0);
                field[i][j].material().setDiffuseMap(img);
                //field[i][j].material().setSpecularPower(0);
                //field[i][j].material().setSpecularMap(img);
                field[i][j].material().setSelfIlluminationMap(img);
                
                if (consumer != null)
                    consumer.accept(field[i][j]);
                
                var objName = name + (a++);
                gameWorld.addGameObject(zoneName, objName, field[i][j]);

                gameWorld.getGameZone(zoneName).load(objName);
            } // end for
        } // end for
    }
    
    public static void initCylinderFields(GameWorld3D gameWorld, double xpos, double ypos, 
        double posz, double radius, double height, int row, int col,
        Image img, String name, String zoneName, Consumer<CylinderObject> consumer)
    {
        CylinderObject field[][] = new CylinderObject[row][col];
        
        int a = 0;
        double radiusMul2 = radius * 2;
        
        for (int i = 0; i < field.length; i++) 
        {
            for (int j = 0; j < field[i].length; j++) 
            {
                field[i][j] = new CylinderObject(gameWorld.gameManager(), 
                    new Point3D(
                        xpos + radius + j * radiusMul2, 
                        ypos + radius + i * radiusMul2, 
                        posz), radius, height);
                field[i][j].setMass(0);
                field[i][j].material().setDiffuseMap(img);
                //field[i][j].material().setSpecularPower(0);
                //field[i][j].material().setSpecularMap(img);
                field[i][j].material().setSelfIlluminationMap(img);
                
                if (consumer != null)
                    consumer.accept(field[i][j]);
                
                var objName = name + (a++);
                gameWorld.addGameObject(zoneName, objName, field[i][j]);

                gameWorld.getGameZone(zoneName).load(objName);
            } // end for
        } // end for
    }

} // end class