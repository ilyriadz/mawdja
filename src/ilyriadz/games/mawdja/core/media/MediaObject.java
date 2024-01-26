
package ilyriadz.games.mawdja.core.media;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 *
 * @author ilyriadz
 */
public final class MediaObject extends SoundObject
{
    private MediaPlayer soundObject;
    
    public MediaObject(String file) 
    {
        soundObject = new MediaPlayer(new Media(file));
        soundObject.setOnEndOfMedia( () -> 
        {
            soundObject.stop();
            if (onMediaFinished != null)
                onMediaFinished.run();
        });
    }

    @Override
    public MediaPlayer soundObject() 
    {
        return soundObject;
    }
} // end class
