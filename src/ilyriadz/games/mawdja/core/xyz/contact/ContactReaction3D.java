
package ilyriadz.games.mawdja.core.xyz.contact;

import com.bulletphysics.collision.narrowphase.PersistentManifold;
import ilyriadz.games.mawdja.core.GameObject;
import ilyriadz.games.mawdja.core.xyz.GameObject3D;
import org.jbox2d.dynamics.contacts.Contact;

/**
 *
 * @author ilyes
 *
 */
public interface ContactReaction3D
{
   void reaction(GameObject3D entity, PersistentManifold cont);
} // end interface PlayerContactReaction
