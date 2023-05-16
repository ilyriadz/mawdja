/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ilyriadz.games.mawdja.core;

import static ilyriadz.games.mawdja.gutil.Gutil.meters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.concurrent.Task;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

/**
 *
 * @author kiradja
 */
public abstract class GameWorld
{
    private final GameManager gameManager;
    private final int gameWorldID;
    private final Map<String, GameZone> gameZones = new HashMap<>();
    private final int gameWorldWidth;
    private final int gameWorldHeight;
    private Body gameWorldBoundsBody;
    private Point2D playerStartPosition;
    private boolean isOnDestroying;
    private Map<String, Runnable> actionsMap = new HashMap<>();
    private Map<String, Runnable> addActionsMap = new HashMap<>();
    private final List<String> removeActionsList = new ArrayList<>();
    private final List<String> lights = new ArrayList<>();
    private Box ground;
    private Runnable afterGameWorldDestroyed = () -> {};
    
    private final ExecutorService exec = Executors.newCachedThreadPool();

    public GameWorld(GameManager gameManager, int gameWorldID, int width, int height) 
    {
        this.gameManager = gameManager;
        this.gameWorldID = gameWorldID;
        this.gameWorldWidth = width;
        this.gameWorldHeight = height;
        this.playerStartPosition = new Point2D(width / 2, height / 2);
        isOnDestroying = false;
        
        ground = new Box(gameWorldWidth, gameWorldHeight, 0);
        ground.translateZProperty().addListener(l ->
        {
            ground.setViewOrder(ground.getTranslateZ());
        });
        ground.setTranslateX(gameWorldWidth / 2);
        ground.setTranslateY(gameWorldHeight / 2);
        PhongMaterial material = new PhongMaterial(Color.WHITE);
        material.setSpecularPower(0);
        ground.setMaterial(material);
        //ground.setOpacity(0.1);
        //gameManager.gameRoot().getChildren().add(ground);
        ground.setTranslateZ(1);
        
        //createGameWorldBoundsBody();
        
        gameManager.getAmbientLight().setLightOn(false);
    }

    public void load()
    {
        gameManager().gameRoot().getChildren().add(ground());
        createGameWorldBoundsBody();
    }
    
    protected final void addAction(String name, Runnable run)
    {
        if (actionsMap.containsKey(name))
            throw new IllegalArgumentException("action '" + name + 
                    "' exists in " + this);
        addActionsMap.put(name, run);
    }
    
    protected final void addAction(String name, Runnable run, long millis)
    {
        
        if (actionsMap.containsKey(name))
            throw new IllegalArgumentException("action '" + name + 
                    "' exists in " + this);
        
        Task<Void> dispatcher = new Task<>() {
                @Override
                protected Void call() throws Exception 
                {
                    Thread.sleep(millis);
                    addActionsMap.put(name, run);
                    return null;
                }
            };
        
        exec.submit(dispatcher);
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
        /*if (isOnDestroying)
            return;
        else
        {*/
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
        //} // end else     
    } // end method
    
    protected final void addGameObject(String gameZoneName, String gameObjectName,
        GameObject gameObject)
    {
        if (!gameZones.keySet().contains(gameZoneName))
            gameZones.put(gameZoneName, new GameZone(gameManager, gameZoneName));
        gameZones.get(gameZoneName).putGameObject(gameObjectName, gameObject);
    }
    
    protected final GameZone getGameZone(String gameZoneName)
    {
        if (!gameZones.keySet().contains(gameZoneName))
            throw new IllegalStateException("GameZone '" + gameZoneName +
                    "' not exists in " + this);
        
        return gameZones.get(gameZoneName);
    }
    
    public final void creates()
    {
        gameZones.values().forEach(GameZone::creates);
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
        
        gameManager.gameRoot().getChildren().remove(ground);
        
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
        BodyDef bd = new BodyDef();
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
        gameWorldBoundsBody.createFixture(fd);
            
    }

    public Box ground() {
        return ground;
    }
    
    public final PhongMaterial groundMaterial()
    {
        return (PhongMaterial) ground.getMaterial();
    }

    protected final void setGroundTexture(Image image)
    {
        ((PhongMaterial) ground.getMaterial()).setDiffuseMap(image);
    }
    
    protected final void setPlayerStartPosition(double x, double y)
    {
        playerStartPosition = new Point2D(x, y);
    }
    
    protected final void toStartPosition()
    {
        var body = gameManager.player().body();
        body.setType(BodyType.STATIC);
        body.getPosition().set(
            meters(playerStartPosition.getX()), 
            meters(playerStartPosition.getY()));
        var shape = gameManager.player().gameObject().node();
        shape.setTranslateX(playerStartPosition.getX());
        shape.setTranslateY(playerStartPosition.getY());
        body.setType(BodyType.DYNAMIC);
    }
    
    protected final void changePlayerPosition(double x, double y)
    {
        var body = gameManager.player().body();
        body.setType(BodyType.STATIC);
        body.getPosition().set(
            meters(x), 
            meters(y));
        var shape = gameManager.player().gameObject().node();
        shape.setTranslateX(x);
        shape.setTranslateY(y);
        body.setType(BodyType.DYNAMIC);
    }
    
    protected final void changePlayerPosition(BodyType type, double x, double y)
    {
        var body = gameManager.player().body();
        body.setType(BodyType.STATIC);
        body.getPosition().set(
            meters(x), 
            meters(y));
        var shape = gameManager.player().gameObject().node();
        shape.setTranslateX(x);
        shape.setTranslateY(y);
        
        if (type != BodyType.STATIC)
            body.setType(type);
    }
    
    protected final int gameWorldID()
    {
        return gameWorldID;
    }
    
    protected final GameManager gameManager()
    {
        return gameManager;
    }

    @Override
    public String toString() 
    {
        return "GameWorld '" + getClass().getSimpleName() + '\'';
    } 
} // end class
