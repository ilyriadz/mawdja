/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ilyriadz.games.mawdja.gutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import org.jbox2d.common.Vec2;

/**
 *
 * @author kiradja
 */
public final class Gutil 
{
    public final static float SCALE = 100;
    
    public static float meters(double pixels)
    {
        return (float) (pixels / SCALE);
    } // end
    
    public static float meters(double pixels, double scale)
    {
        return (float) (pixels / (scale < 1 ? 1 : scale));
    } // end
    
    public static double pixels(float meters)
    {
        return meters * SCALE;
    } // end
    
    public static double pixels(float meters, double scale)
    {
        return meters * scale;
    } // end
    
    public static double distance(Vec2 v1, Vec2 v2)
    {
        Point2D p1 = new Point2D(v1.x, v1.y);
        Point2D p2 = new Point2D(v2.x, v2.y);

        return p1.distance(p2);
    } // end method getDistance
    
    public static double distance(double x1, double y1, double x2, double y2)
    {
        Point2D p1 = new Point2D(x1, y1);
        Point2D p2 = new Point2D(x2, y2);

        return p1.distance(p2);
    } // end method getDistance

    
    public static double distance(Node n1, Node n2)
    {
        return Gutil.distance(n1.getTranslateX(), n1.getTranslateY(), 
            n2.getTranslateX(), n2.getTranslateY());
    } // end method
    
    public static float maxSpeed(double distence, double desired, double maxSpeed)
    {
        return (float)(maxSpeed * distence / desired);
    } // end method getMaxSpeed
    
    static public final double map(double value,
        double start1, double stop1,
        double start2, double stop2)
    {
        return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1));
    } // end method map
    
    static public final int constrain(int amt, int low, int high)
    {
        return (amt < low) ? low : ((amt > high) ? high : amt);
    } // end method constrain
    
    public static Vec2 randomVec2()
    {
        double angle = Math.toRadians(
            ThreadLocalRandom.current().nextGaussian() * -30);
        return new Vec2((float)Math.cos(angle), (float)Math.sin(angle));
    } // end method randomVec2
    
    public static double angleBetween(Vec2 v1, Vec2 v2)
    {
        return Math.toDegrees(Math.acos(Vec2.dot(v1, v2) / (v1.length() * v2.length())));
    } // end method getAngleBetween
    
    public static double relativeAngle(double x1, double y1, 
        double x2, double y2)
    {
        return relativeAngle(new Vec2((float)x1, (float)y1), 
            new Vec2((float)x2, (float)y2));
    } // end method
    
    public static double relativeAngle(Vec2 r, Vec2 t)
    {
        var subVec = r.sub(t).mul(-1);       
        var angle = Math.toDegrees(Math.atan2(subVec.y, subVec.x));
        
        return angle < 0 ? angle * -1 : 360 - angle;
    } // end method
    
    public static double relativeAngle(Node r, Node t)
    {
        return relativeAngle(r.getTranslateX(), r.getTranslateY(), 
            t.getTranslateX(), t.getTranslateY());
    } // end method
    
    public static Vec2 relativeVector(Vec2 r, Vec2 t)
    {
        return r.sub(t).mul(-1);
    } // end method
    
    public static Vec2 relativeVector(Node r, Node t)
    {
        return new Vec2(
            r.translateXProperty().floatValue(),
            r.translateYProperty().floatValue()).sub(
            new Vec2(t.translateXProperty().floatValue(),
                t.translateYProperty().floatValue())).mul(-1);
    } // end method
    
    public static Vec2 relativeVectorNormalized(Vec2 r, Vec2 t)
    {
        var vec = r.sub(t).mul(-1);
        vec.normalize();
        return vec;
    } // end method

    public static Vec2 normalPoint(Vec2 p, Vec2 a, Vec2 b)
    {
        Vec2 ap = p.sub(a);
        Vec2 ab = b.sub(a);

        ab.normalize();
        ab.mulLocal(Vec2.dot(ap, ab));

        return a.add(ab);
    } // end method getNormalPoint  
    
    public static Path svgToPath(String svg)
    {
        String[] lines = svg.split("\n");
        List<PathElement> list = new ArrayList<>();
        
        for (String line : lines)
        {
            String str = line.trim();
            char c = '\0';
            String[] points;
            if (str.startsWith("M"))
            {
                points = splitPoints(line, "M");
                c = 'M';
            } // end if
            else if (str.startsWith("C"))
            {
                points = splitPoints(line, "C");
                c = 'M';
            } // end if
            else
            {
                points = splitPoints(line, "");
            } // end if    
        
            double[] xy;
            for (String point : points)
            {
                switch (c)
                {
                    case 'M':
                        MoveTo mt = new MoveTo();
                        xy = splitPointToDouble(point);
                        mt.setX(xy[0]);
                        mt.setY(xy[1]);
                        list.add(mt);
                        break;
                    case 'C':
                        LineTo lt = new LineTo();
                        xy = splitPointToDouble(point);
                        lt.setX(xy[0]);
                        lt.setY(xy[1]);
                        list.add(lt);
                        break;
                    default:
                        LineTo lt2 = new LineTo();
                        xy = splitPointToDouble(point);
                        lt2.setX(xy[0]);
                        lt2.setY(xy[1]);
                        list.add(lt2);
                        break; // end else
                } // end switch
                System.out.println(Arrays.toString(xy));
            } // end inner enhanced for      
        } // end enhanced for
        
        Path path = new Path(list);
        path.getElements().add(new ClosePath());
        path.setTranslateZ(-0.01);
        path.setStroke(Color.RED);
        path.setStrokeWidth(1.5);
        return path;
    } // end method
    
    private static String[] splitPoints(String points, String replace)
    {
        String str = points.replace(replace, "").trim();
        
        return str.split(" ");
    } // end method
    
    private static double[] splitPointToDouble(String point)
    {
        String[] str = point.split(",");
        
        return new double[]{Double.parseDouble(str[0]), Double.parseDouble(str[1])};
    } // end method
    
    public static double thirtyBase(double first, double firstEquivalent, 
            double second)
    {
        return Math.abs(second * firstEquivalent / first);
    }
} // end class