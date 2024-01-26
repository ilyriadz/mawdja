
package ilyriadz.games.mawdja.message;

import ilyriadz.games.mawdja.core.GameObject;

/**
 *
 * @author kiradja
 */
@FunctionalInterface
public interface Message
{
    public void handleMessage(GameObject receiver, Object userData);
}
