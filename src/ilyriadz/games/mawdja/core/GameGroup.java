/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ilyriadz.games.mawdja.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kiradja
 */
public class GameGroup
{
    private final GameManager gameManager;
    private final String gameGroupName;
    private final Map<String, GameObject> gameObjectsMap = new HashMap<>();

    public GameGroup(GameManager gameManager, String zoneName) 
    {
        Objects.nonNull(zoneName);
        Objects.nonNull(gameManager);
        this.gameGroupName = zoneName;
        this.gameManager = gameManager;
    }

    public String gameGroupName() 
    {
        return gameGroupName;
    }
    
    public void putGameObject(String name, GameObject gameObject)
    {
        if (gameObjectsMap.keySet().contains(name))
            throw new IllegalStateException(
                "GameObject '" + name + "' is already exist in " + this);
        
        if (gameObjectsMap.values().contains(gameObject))
            throw new IllegalStateException(
                "GameObject " + gameObject + " is duplicated in " + this);
        gameObjectsMap.put(name, gameObject);
    }
    
    public GameObject getGameObject(String name)
    {
        if (!gameObjectsMap.containsKey(name))
            throw new IllegalStateException(
                "GameObject '" + name + "' not exist in " + this);
        return gameObjectsMap.get(name);
    }
    
    public final void load()
    {
        gameObjectsMap.values().forEach(gameManager::add);
    }
    
     public final void creates()
    {
        gameObjectsMap.values().stream()
                .forEach(GameObject::create);
        if (!isLoaded())
            Logger.getLogger(this.toString()).log(
                Level.WARNING, "creates no loaded {0} has no effect", this);
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
                .filter(e -> !gameManager.gameObjectExist(e) || e.body() == null)
                .findAny().isPresent();
    }
    
    
    public final void destroyBodies()
    {
        gameObjectsMap.values().forEach(GameObject::destroyBody);
    } // end
    
    public final void destroy()
    {
        gameObjectsMap.values().forEach(gameObject ->
        {
            //gameObject.destroyBody();
            gameManager.destroy(gameObject);
        });
        
        clear();
    }
    
    protected final void clear()
    {
        gameObjectsMap.clear();
    } // end method

    @Override
    public String toString() {
        return getClass().getSimpleName() + " '" + gameGroupName + "'"; 
    }
    
    
}
