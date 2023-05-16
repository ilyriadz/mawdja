
package ilyriadz.games.mawdja.message;

import ilyriadz.games.mawdja.core.xyz.GameObject3D;

/**
 *
 * @author kiradja
 */
@FunctionalInterface
public interface Message3D
{
    public void handleMessage(GameObject3D receiver, Object userData);
}
