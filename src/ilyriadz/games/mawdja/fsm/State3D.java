package ilyriadz.games.mawdja.fsm;

import com.bulletphysics.collision.narrowphase.PersistentManifold;
import ilyriadz.games.mawdja.contact.ContactReaction;
import ilyriadz.games.mawdja.contact.ContactUnreaction;
import ilyriadz.games.mawdja.core.GameObject;
import ilyriadz.games.mawdja.core.xyz.GameObject3D;
import ilyriadz.games.mawdja.core.xyz.contact.ContactReaction3D;
import ilyriadz.games.mawdja.core.xyz.contact.ContactUnreaction3D;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jbox2d.dynamics.contacts.Contact;

/**
 *
 * @author ilyes
 * @param <T>
 */
public abstract class State3D implements Serializable,
    ContactReaction3D, ContactUnreaction3D
{
    public abstract void enter(GameObject3D gameObject);

    public abstract void execute(GameObject3D gameObject);

    public abstract void exit(GameObject3D gameObject);

    @Override
    public void reaction(GameObject3D gameObject, PersistentManifold cont)
    {
    }

    @Override
    public void unreaction(GameObject3D gameObject, PersistentManifold cont)
    {
        
    }
    
    
} // end class State
