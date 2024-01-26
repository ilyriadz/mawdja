/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ilyriadz.games.mawdja.core.media;

import ilyriadz.games.mawdja.core.GameManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javafx.scene.Node;

/**
 *
 * @author kiradja
 */
public final class SoundManager
{
    private final Map<String, SoundObject> sounds = new HashMap<>();
    
    private GameManager gameManager;
    
    private Node target = null;

    public SoundManager(Object gameManager) 
    {
        if (gameManager instanceof GameManager gm)
        {
            this.gameManager = gm;
            this.gameManager.setSoundManager(this);
        }
        else
            throw new IllegalArgumentException("no GameManager argument");   
    }
    
    public void addSound(String name, SoundObject soundObject)
    {
        Objects.nonNull(name);
        Objects.nonNull(soundObject);
        
        if (sounds.containsKey(name))
            throw new IllegalStateException("key '" + name + "' exists");
        
        sounds.put(name, soundObject);
    }
    
    public void addSoundIfNotExist(String name, SoundObject soundObject)
    {
        if (sounds.containsKey(name))
            return;
        addSound(name, soundObject);
    }
    
    public SoundObject removeSound(String name)
    {
        Objects.nonNull(name);
        
        return sounds.remove(name);
    }
    
    public SoundObject getSound(String name)
    {
        Objects.nonNull(name);
        
        return sounds.get(name);
    }
    
    public final boolean hasSound(String name)
    {
        return sounds.containsKey(name);
    }

    public void setTarget(Node target) 
    {
        this.target = target;
    }
    
    
    
    public void play(String name)
    {
        Objects.nonNull(name);
        
        if (!sounds.containsKey(name))
            throw new IllegalArgumentException("sound object '" + name + "' not exist");
        
        sounds.get(name).play();
    }
    
    public void playAll()
    {
        sounds.values().parallelStream().forEach(SoundObject::play);
    }
    
    public void pause(String name)
    {
        Objects.nonNull(name);
        
        if (!sounds.containsKey(name))
            throw new IllegalArgumentException("sound object '" + name + "' not exist");
        
        sounds.get(name).pause();
    }
    
    public void pauseAll()
    {
        sounds.values().parallelStream().forEach(SoundObject::pause);
    }
    
    public void stop(String name)
    {
        Objects.nonNull(name);
        
        if (!sounds.containsKey(name))
            throw new IllegalArgumentException("sound object '" + name + "' not exist");
        
        sounds.get(name).stop();
    }
    
    public void stopAll()
    {
        sounds.values().parallelStream().forEach(SoundObject::stop);
    }
    
    public void update()
    {
        if (target != null)
        {
            sounds.values().parallelStream()
                    .filter(SoundObject::isAutoManaged)
                    .forEach(so -> so.update(target));
        } // end if
    }
}
