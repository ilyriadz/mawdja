
package ilyriadz.games.mawdja.gutil;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import static ilyriadz.games.mawdja.gutil.Gutil.meters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author ilyria
 */
public class PathUtil 
{
    /**
     * 
     * @param group
     * @param world
     * @param path javafx Path node
     * @param translateX
     * @param translateY
     * @return the physical body of created Path
     */
    public static Body createPathPhysic(World world, Path path, 
        double translateX, double translateY)
    {
        path.setTranslateX(translateX);
        path.setTranslateY(translateY);
        
        BodyDef bd = new BodyDef();
        bd.position = new Vec2(meters(translateX), meters(translateY));
        bd.type = BodyType.STATIC;
        
        Body pathBody = world.createBody(bd);
        
        ChainShape chs = new ChainShape();
        
        Vec2[] vertices = new Vec2[path.getElements()
            .stream()
            .filter(e -> !(e instanceof ClosePath))
            .collect(Collectors.toList()).size()];
        for (int i = 0; i < vertices.length; i++)
        {
            PathElement pel =  path.getElements().get(i);
            
            if (pel instanceof MoveTo)
            {
                MoveTo e = (MoveTo)pel;
                vertices[i] = new Vec2(meters(e.getX()), meters(e.getY()));  
            } // end if
            else if (pel instanceof LineTo)
            {
                LineTo e = (LineTo)pel;
                vertices[i] = new Vec2(meters(e.getX()), meters(e.getY()));
            } // end else
        }
        
        chs.createLoop(vertices, vertices.length);
        pathBody.createFixture(chs, 0.9f);
        
        return pathBody;
    } // end method
    
    public static Body createOpenPathPhysic(World world, Path path, 
        double translateX, double translateY)
    {
        path.setTranslateX(translateX);
        path.setTranslateY(translateY);
        
        BodyDef bd = new BodyDef();
        bd.position = new Vec2(meters(translateX), meters(translateY));
        bd.type = BodyType.STATIC;
        
        Body pathBody = world.createBody(bd);
        
        ChainShape chs = new ChainShape();
        
        Vec2[] vertices = new Vec2[path.getElements()
            .stream()
            .filter(e -> !(e instanceof ClosePath))
            .collect(Collectors.toList()).size()];
        for (int i = 0; i < vertices.length; i++)
        {
            PathElement pel =  path.getElements().get(i);
            
            if (pel instanceof MoveTo)
            {
                MoveTo e = (MoveTo)pel;
                vertices[i] = new Vec2(meters(e.getX()), meters(e.getY()));  
            } // end if
            else if (pel instanceof LineTo)
            {
                LineTo e = (LineTo)pel;
                vertices[i] = new Vec2(meters(e.getX()), meters(e.getY()));
            } // end else
        }
        
        chs.createChain(vertices, vertices.length);
        pathBody.createFixture(chs, 0.9f);
        
        return pathBody;
    } // end method
    
    public static ChainShape createShape(Path path)
    {
        ChainShape chs = new ChainShape();
        
        Vec2[] vertices = new Vec2[path.getElements()
            .stream()
            .filter(e -> !(e instanceof ClosePath))
            .collect(Collectors.toList()).size()];
        for (int i = 0; i < vertices.length; i++)
        {
            PathElement pel =  path.getElements().get(i);
            
            if (pel instanceof MoveTo)
            {
                MoveTo e = (MoveTo)pel;
                vertices[i] = new Vec2(meters(e.getX()), meters(e.getY()));  
            } // end if
            else if (pel instanceof LineTo)
            {
                LineTo e = (LineTo)pel;
                vertices[i] = new Vec2(meters(e.getX()), meters(e.getY()));
            } // end else
        }
        
        chs.createLoop(vertices, vertices.length);
        
        return chs;
    } // end method
    
    public static Body createPathPhysic(World world, Path path, 
        double translateX, double translateY, BodyType type)
    {
        path.setTranslateX(translateX);
        path.setTranslateY(translateY);
        
        BodyDef bd = new BodyDef();
        bd.position = new Vec2(meters(translateX), meters(translateY));
        bd.type = type;
        
        Body pathBody = world.createBody(bd);
        
        ChainShape chs = new ChainShape();
        
        Vec2[] vertices = new Vec2[path.getElements()
            .stream()
            .filter(e -> !(e instanceof ClosePath))
            .collect(Collectors.toList()).size()];
        for (int i = 0; i < vertices.length; i++)
        {
            PathElement pel =  path.getElements().get(i);
            
            if (pel instanceof MoveTo)
            {
                MoveTo e = (MoveTo)pel;
                vertices[i] = new Vec2(meters(e.getX()), meters(e.getY()));  
            } // end if
            else if (pel instanceof LineTo)
            {
                LineTo e = (LineTo)pel;
                vertices[i] = new Vec2(meters(e.getX()), meters(e.getY()));
            } // end else
        }
        
        chs.createLoop(vertices, vertices.length);
        pathBody.createFixture(chs, 0.9f);
        
        return pathBody;
    } // end method
    
    public static FixtureDef fixtureDef(Path path)
    {
        ChainShape chs = new ChainShape();
        
        Vec2[] vertices = new Vec2[path.getElements()
            .stream()
            .filter(e -> !(e instanceof ClosePath))
            .collect(Collectors.toList()).size()];
        for (int i = 0; i < vertices.length; i++)
        {
            PathElement pel =  path.getElements().get(i);
            
            if (pel instanceof MoveTo)
            {
                MoveTo e = (MoveTo)pel;
                vertices[i] = new Vec2(meters(e.getX()), meters(e.getY()));  
            } // end if
            else if (pel instanceof LineTo)
            {
                LineTo e = (LineTo)pel;
                vertices[i] = new Vec2(meters(e.getX()), meters(e.getY()));
            } // end else
        }
        
        chs.createLoop(vertices, vertices.length);
        FixtureDef fdf = new FixtureDef();
        fdf.density = 0.9f;
        fdf.shape = chs;
        
        return fdf;
    } // end method
    
    public static List<String> listOfStringLines(InputStream in)
    {
        Scanner scanner = new Scanner(in);
        List<String> listOfStringLines = new ArrayList<>();
        while (scanner.hasNextLine())
            listOfStringLines.add(scanner.nextLine().toLowerCase().strip());
        
        return listOfStringLines;
    } // end method
    
    public static Indexes pathIndexesOf(int begin, List<String> losl)
    {
        int[] indexes = {0, 0};
        final var SIZE = losl.size();
        
        for (int i = begin; i < SIZE; i++)
        {
            var currentLine = losl.get(i);
            
            if (currentLine.contains("d"))
            {
                indexes[0] = i;
                continue;
            } // end if
            if (currentLine.contains("z"))
            {
                indexes[1] = i;
                break;
            } // end if
        } // end for
        
        return new Indexes(indexes[0], indexes[1]);
    } // end method
    
    public static MoveTo moveToOf(Indexes indexes, List<String> losl) 
    {
        for (int i = indexes.begin; i <= indexes.end; i++)
        {
            var currentLineStr = losl.get(i);
            
            if (currentLineStr.contains("d=\"m"))
            {
                currentLineStr = currentLineStr.replace("d=\"m", "").strip();
                String[] coordStr = currentLineStr.split(",");
                MoveTo moveTo = new MoveTo();
                moveTo.setX(Double.parseDouble(coordStr[0]));
                moveTo.setY(Double.parseDouble(coordStr[1]));
                return moveTo;
            } // end if
        } // end for
        
        throw new IllegalArgumentException("no MoveTo found!");
    } // end method
    
    public static List<LineTo> LineToOf(Indexes indexes, List<String> losl) 
    {
        final var lineToList = new ArrayList<LineTo>();
        
        for (int i = indexes.begin + 2; i <= indexes.end; i++)
        {
            var currentLineStr = losl.get(i);
            
            if (currentLineStr.contains("c"))
            {
                currentLineStr = currentLineStr.replace("c", "").strip();
            } // end if
            
            if (currentLineStr.contains("z"))
            {
                currentLineStr = currentLineStr.replace("z", "").strip();
            } // end if
            
            String[] coordStr = currentLineStr.split(" ");
            String[] xy = coordStr[0].split(",");
                
            LineTo lineTo = new LineTo();
            lineTo.setX(Double.parseDouble(xy[0]));
            lineTo.setY(Double.parseDouble(xy[1]));
                
            lineToList.add(lineTo);
                
            i++;
        } // end for
        
        if (lineToList.isEmpty())
            throw new IllegalArgumentException("no LineTo found!");
        
        return lineToList;
    } // end method
    
    public static Path fromSvg(InputStream svgInputFile)
    {
        var list = listOfStringLines(svgInputFile);
        //list.forEach(System.out::println);
        var indexes = pathIndexesOf(0, list);
        Path p = new Path();
        p.getElements().add(moveToOf(indexes, list));
        LineToOf(indexes, list).stream()
            .forEach(p.getElements()::add);
        p.getElements().add(new ClosePath());
        
        return p;
        
    } // end method
    
    public static Path fromVertices(Vec2... vertices)
    {
        Path p = new Path();
        p.getElements().add(new MoveTo(vertices[0].x, vertices[0].y));
        for (int i = 1; i < vertices.length; i++)
        {
            p.getElements().add(new LineTo(vertices[i].x, vertices[i].y));
        } // end for
        p.getElements().add(new ClosePath());
        
        return p;       
    } // end method
    
    
} // end class PathUtil
