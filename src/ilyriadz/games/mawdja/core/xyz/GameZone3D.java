/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ilyriadz.games.mawdja.core.xyz;

import ilyriadz.games.mawdja.core.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kiradja
 */
public class GameZone3D
{
    private final GameManager3D gameManager;
    private final String gameZoneName;
    private final Map<String, GameObject3D> gameObjectsMap = new HashMap<>();

    public GameZone3D(GameManager3D gameManager, String zoneName) 
    {
        Objects.nonNull(zoneName);
        Objects.nonNull(gameManager);
        this.gameZoneName = zoneName;
        this.gameManager = gameManager;
    }

    public String gameZoneName() 
    {
        return gameZoneName;
    }
    
    public void putGameObject(String name, GameObject3D gameObject)
    {
        if (gameObjectsMap.keySet().contains(name))
            throw new IllegalStateException(
                "GameObject '" + name + "' is already exist in " + this);
        
        if (gameObjectsMap.values().contains(gameObject))
            throw new IllegalStateException(
                "GameObject " + gameObject + " is duplicated in " + this);
        gameObjectsMap.put(name, gameObject);
    }
    
    public GameObject3D getGameObject(String name)
    {
        return gameObjectsMap.get(name);
    }
    
    public final void load()
    {
        gameObjectsMap.values().forEach(gameManager::add);
        
        if (GameManager3D.debug)
            Logger.getLogger(this.toString()).log(
                Level.INFO, "load {0}", this);
    }
    
    public final void load(String gameObjectString)
    {
        if (!gameObjectsMap.containsKey(gameObjectString))
            throw new IllegalArgumentException(
                "can't load the GameObject named '" + gameObjectString + 
                    "' because is not exists");
        
        gameManager.add(gameObjectsMap.get(gameObjectString));
    }
    
    public final void create(String gameObjectString)
    {
        if (!gameObjectsMap.containsKey(gameObjectString))
            throw new IllegalArgumentException(
                "can't create the GameObject named '" + gameObjectString + 
                    "' because is not exists");
        
        gameObjectsMap.get(gameObjectString).create();
    }
    
    public final void loadAndCreate(String gameObjectString)
    {
        if (!gameObjectsMap.containsKey(gameObjectString))
            throw new IllegalArgumentException(
                "can't load/create the GameObject named '" + gameObjectString + 
                    "' because is not exists");
        var gameObject = gameObjectsMap.get(gameObjectString);
        gameManager.add(gameObject);
        gameObject.create();
        
    }
    
     public final void creates()
    {
        gameObjectsMap.values().stream()
                .forEach(GameObject3D::create);
        
        if (GameManager3D.debug)
            Logger.getLogger(this.toString()).log(
                Level.INFO, "creates {0}", this);
    }
    
    public final void loadAndCreate()
    {
        load();
        creates();
    }
    
    public final boolean isLoaded()
    {
        return gameObjectsMap.values().stream()
                .filter(e -> !gameManager.gameObjectExist(e))
                .findAny().isPresent();
    }
    
    public final boolean isCreated()
    {
        return gameObjectsMap.values().stream()
                .filter(e -> !gameManager.gameObjectExist(e) || e.rigidBody() == null)
                .findAny().isPresent();
    }
    
    
    public final void destroyBodies()
    {
        gameObjectsMap.values().forEach(GameObject3D::destroyRigidBody);
        if (GameManager3D.debug)
            Logger.getLogger(this.toString()).log(
                Level.INFO, "destroys bodies of {0}", this);
    } // end
    
    public final void destroy()
    {
        gameObjectsMap.values().forEach(gameObject ->
        {
            //gameObject.destroyBody();
            gameManager.destroy(gameObject);
        });
        
        if (GameManager3D.debug)
            Logger.getLogger(this.toString()).log(
                Level.INFO, "destroys {0}", this);
    }
    
    protected final void clear()
    {
        gameObjectsMap.clear();
    } // end method

    @Override
    public String toString() {
        return getClass().getSimpleName() + " '" + gameZoneName + "'"; 
    }
    
    
}
