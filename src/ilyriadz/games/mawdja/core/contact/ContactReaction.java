
package ilyriadz.games.mawdja.contact;

import ilyriadz.games.mawdja.core.GameObject;
import org.jbox2d.dynamics.contacts.Contact;

/**
 *
 * @author ilyes
 *
 */
public interface ContactReaction
{
   void reaction(GameObject entity, Contact cont);
} // end interface PlayerContactReaction
