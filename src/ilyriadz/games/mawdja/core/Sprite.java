/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ilyriadz.games.mawdja.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javafx.scene.image.Image;

/**
 *
 * @author kiradja
 */
public class Sprite
{
    private final GameObject gameObject;
    private final Map<String, List<Image>> assets = new HashMap<>();
    private final Map<String, Double> rates = new HashMap<>();
    private List<Image> currentList;
    private int currentImageIndex = 0;
    private double currentRate;
    private String currentName;
    private boolean loop = true;
    private boolean stop = false;
    
    public Sprite(GameObject gameObject)
    {
        Objects.nonNull(gameObject);
        this.gameObject = gameObject;
        gameObject.setSprite(this);
    } // end constructor
    
    public void addAsset(String name, List<Image> images, double rate)
    {
        Objects.nonNull(name);
        Objects.nonNull(images);
        if (images.isEmpty())
            throw new IllegalStateException("images list is empty!");
        
        if (rate < 0)
            rate = 0;
        
        assets.put(name, images);
        rates.put(name, rate);
        
        if (assets.values().size() == 1)
            setAsset(name);
    }
    
    public void setAsset(String name)
    {
        if (currentName != null && currentName.equals(name))
        {
            stop = false;
            return;
        } // end if
        
        currentList = assets.get(name);
        currentRate = 0;
        currentImageIndex = 0;
        currentName = name;      
    } // end
    
    public void step(float delta)
    {
        if (currentList == null)
            return;
        
        if (!stop)
        {
            if (currentRate <= 0)
            {
                gameObject.material().setDiffuseMap(
                  currentList.get(currentImageIndex));
                currentImageIndex = (currentImageIndex + 1) % currentList.size();
                currentRate = rates.get(currentName) * GameManager.STEP_TIME;
                if (currentImageIndex == 0 && !loop)
                    stop = true;
            } // end if
            currentRate -= delta;
        } // end if
    } // end method
    
    public void play()
    {
        loop = true;
        stop = false;
    }
    
    public void playOneTime()
    {
        loop = false;
        stop = false;
    }
    
    public void stop()
    {
        loop = false;
    }
   
    public boolean isLooping()
    {
        return loop;
    }
    
    public boolean isStoped()
    {
        return !loop;
    }
} // end class
