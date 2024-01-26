
package ilyriadz.games.mawdja.message;

import ilyriadz.games.mawdja.core.GameObject;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author kiradja
 */
public class Telegram
{
    public final GameObject sender;
    public final GameObject receiver;
    public final Message message;
    public final Object userData;

    public Telegram(GameObject sender, GameObject receiver, Message message,
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
