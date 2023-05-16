/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ilyriadz.games.mawdja.core.media;

import javafx.scene.media.AudioClip;

/**
 *
 * @author ilyriadz
 */
public final class AudioObject extends SoundObject
{
    private final AudioClip soundObject;
    
    public AudioObject(String file) 
    {
        soundObject = new AudioClip(file);
    }

    @Override
    public AudioClip soundObject() 
    {
        return soundObject;
    }
    
    
}
