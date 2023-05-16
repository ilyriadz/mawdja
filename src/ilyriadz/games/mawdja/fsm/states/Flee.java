/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ilyriadz.games.mawdja.fsm.states;

import ilyriadz.games.mawdja.core.GameObject;
import ilyriadz.games.mawdja.fsm.State;
import ilyriadz.games.mawdja.gutil.Gutil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author kiradja
 */
public class Flee extends State
{
    private GameObject fleeGameObject;
    private final Map<GameObject, Double> gameObjectsMap = new HashMap<>();
    
    public Flee(GameObject fleeGameObject) 
    {
        Objects.nonNull(fleeGameObject);
        this.fleeGameObject = fleeGameObject;
    }
    
    @Override
    public void enter(GameObject gameObject) 
    {
        
    }

    @Override
    public void execute(GameObject gameObject) 
    {
        gameObjectsMap.keySet().stream()
                .forEach(e ->
                        {
                            if (Gutil.distance(e.node(), fleeGameObject.node())
                                    < gameObjectsMap.get(e))
                            {
                                fleeGameObject.setTarget(e.body().getPosition());
                                fleeGameObject.flee();
                            }
                        });
        fleeGameObject.setTarget(null);
    }

    @Override
    public void exit(GameObject gameObject) {
    }
    
    public void addGameObject(GameObject gameObject, double distance)
    {
        gameObjectsMap.put(gameObject, distance);
    } // end method
}
