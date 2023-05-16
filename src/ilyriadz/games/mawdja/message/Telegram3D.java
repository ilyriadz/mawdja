
package ilyriadz.games.mawdja.message;

import ilyriadz.games.mawdja.core.GameObject;
import ilyriadz.games.mawdja.core.xyz.GameObject3D;

/**
 *
 * @author kiradja
 */
public class Telegram3D
{
    public final GameObject3D sender;
    public final GameObject3D receiver;
    public final Message3D message;
    public final Object userData;

    public Telegram3D(GameObject3D sender, GameObject3D receiver, Message3D message,
        Object userData) 
    {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.userData = userData;
    }  
    
    @Override
    public String toString()
    {
        return String.format("Telegram: '%s' sender:%s, reciver:%s", 
                message, sender, receiver);
    }
    
    
}
