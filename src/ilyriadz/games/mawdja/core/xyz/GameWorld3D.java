/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ilyriadz.games.mawdja.core.xyz;

import com.bulletphysics.linearmath.Transform;
import ilyriadz.games.mawdja.core.*;
import static ilyriadz.games.mawdja.gutil.Gutil.meters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;

/**
 *
 * @author kiradja
 */
public abstract class GameWorld3D
{
    private final GameManager3D gameManager;
    private final int gameWorldID;
    private final Map<String, GameZone3D> gameZones = new HashMap<>();
    private Body gameWorldBoundsBody;
    private Point3D playerStartPosition;
    private final Map<String, Runnable> actionsMap = new HashMap<>();
    private final Map<String, Runnable> addActionsMap = new HashMap<>();
    private final List<String> removeActionsList = new ArrayList<>();
    
    private Runnable afterGameWorldDestroyed = () -> {};

    public GameWorld3D(GameManager3D gameManager, int gameWorldID) 
    {
        this.gameManager = gameManager;
        this.gameWorldID = gameWorldID;
                
        PhongMaterial material = new PhongMaterial(Color.WHITE);
        material.setSpecularPower(0);
        
        gameManager.getAmbientLight().setLightOn(false);
    }

    public abstract void load();
    
    protected final void addAction(String name, Runnable run)
    {
        if (actionsMap.containsKey(name))
            throw new IllegalArgumentException("action '" + name + 
                    "' exists in " + this);
        addActionsMap.put(name, run);
    }
    
    protected final void removeAction(String name)
    {
        if (actionsMap.containsKey(name))
            removeActionsList.add(name);
    }
    
    protected final Runnable getAction(String name)
    {
        if (!actionsMap.containsKey(name))
            throw new IllegalArgumentException("action '" + name + 
                    "' is not exists in " + this);
        return actionsMap.get(name);
    }

    public void setAfterGameWorldDestroyed(Runnable afterGameWorldDestroyed)
    {
        if (afterGameWorldDestroyed != null)
            this.afterGameWorldDestroyed = afterGameWorldDestroyed;
    }

    public Runnable getAfterGameWorldDestroyed() {
        return afterGameWorldDestroyed;
    }
    
    
    
    public final void update()
    {

        if (!addActionsMap.isEmpty())
        {
            actionsMap.putAll(addActionsMap);
            addActionsMap.clear();
        } // end if
            
        if (!removeActionsList.isEmpty())
        {
            removeActionsList.forEach(actionsMap::remove);
            removeActionsList.clear();
        } // end if
            
        actionsMap.values().forEach(Runnable::run);     
    } // end method
    
    public final void addGameObject(String gameZoneName, String gameObjectName,
        GameObject3D gameObject)
    {
        if (!gameZones.keySet().contains(gameZoneName))
            gameZones.put(gameZoneName, new GameZone3D(gameManager, gameZoneName));
        gameZones.get(gameZoneName).putGameObject(gameObjectName, gameObject);
    }
    
    public final GameZone3D getGameZone(String gameZoneName)
    {
        if (!gameZones.keySet().contains(gameZoneName))
            throw new IllegalStateException("GameZone '" + gameZoneName +
                    "' not exists in " + this);
        
        return gameZones.get(gameZoneName);
    }
    
    public final void creates()
    {
        gameZones.values().forEach(GameZone3D::creates);
    }
    
    public final void destroy()
    {
        //isOnDestroying = true;
        
        gameZones.values().forEach(gameZone ->
        {
            gameZone.destroy();
            gameZone.clear();
        });
        actionsMap.clear();
        
        gameWorldBoundsBody.getWorld().destroyBody(gameWorldBoundsBody);
        gameWorldBoundsBody = null;
        
        afterGameWorldDestroyed.run();
    }
    
    public final void destroyBodies(String name)
    {
        var gameZone = gameZones.get(name);
        if (gameZone != null)
            gameZone.destroyBodies();
        else
            throw new IllegalStateException("GameZone '" + name 
                    + "' not exists!");
    }

    protected void createGameWorldBoundsBody() 
    {
        /*BodyDef bd = new BodyDef();
        bd.type = BodyType.STATIC;
        
        FixtureDef fd = new FixtureDef();
        ChainShape ch = new ChainShape();
        
        var w = meters(gameWorldWidth);
        var h = meters(gameWorldHeight);
        
        var vecs = new Vec2[]{new Vec2(), new Vec2(w, 0), new Vec2(w, h),
            new Vec2(0, h)};
        ch.createLoop(vecs, vecs.length);
        fd.shape = ch;

        gameWorldBoundsBody = gameManager.world().createBody(bd);
        gameWorldBoundsBody.createFixture(fd);//*/
            
    }

    protected final void setPlayerStartPosition(double x, double y, double z)
    {
        playerStartPosition = new Point3D(x, y, z);
    }
    
    protected final void toStartPosition()
    {
        if (gameManager.hasPlayer())
        {
            var body = gameManager.player().gameObject().rigidBody;
            Transform transform = new Transform();
            body.getMotionState().getWorldTransform(transform);
            transform.origin.set(
                meters(playerStartPosition.getX()),
                meters(playerStartPosition.getY()),
                meters(playerStartPosition.getZ()));
            body.setWorldTransform(transform);
        }
        
    }
    
    protected final void changePlayerPosition(double x, double y, double z)
    {
        if (gameManager.hasPlayer())
        {
            var body = gameManager.player().gameObject().rigidBody;
            Transform transform = new Transform();
            body.getMotionState().getWorldTransform(transform);
            transform.origin.set(
                meters((float)playerStartPosition.getX()),
                meters((float)playerStartPosition.getY()),
                meters((float)playerStartPosition.getZ()));
        }
    }
    
    public final int gameWorldID()
    {
        return gameWorldID;
    }
    
    public final GameManager3D gameManager()
    {
        return gameManager;
    }

    @Override
    public String toString() 
    {
        return "GameWorld '" + getClass().getSimpleName() + '\'';
    } 
} // end class
