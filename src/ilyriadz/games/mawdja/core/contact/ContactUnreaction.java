
package ilyriadz.games.mawdja.contact;

import ilyriadz.games.mawdja.core.GameObject;
import org.jbox2d.dynamics.contacts.Contact;

/**
 *
 * @author ilyes
 *
 */
public interface ContactUnreaction
{
   void unreaction(GameObject entity, Contact cont);
} // end interface PlayerContactReaction
