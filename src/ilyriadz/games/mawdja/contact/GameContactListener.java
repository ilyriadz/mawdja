
package ilyriadz.games.mawdja.contact;

import java.util.Queue;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

/**
 *
 * @author ilyes
 */
public class GameContactListener implements ContactListener
{
    private final Queue<Contact> contactQueue;
    private final Queue<Contact> uncontactQueue;
    
    public GameContactListener(Queue<Contact> contactQueue, Queue<Contact> uncontactQueue)
    {
        this.contactQueue = contactQueue;
        this.uncontactQueue = uncontactQueue;
    }
    @Override
    public void beginContact(Contact contact)
    {
        contactQueue.add(contact);
    } // end method

    @Override
    public void endContact(Contact contact)
    {
        uncontactQueue.add(contact);
    }

    @Override
    public void preSolve( Contact contact, Manifold oldManifold )
    {
    }

    @Override
    public void postSolve( Contact contact, ContactImpulse impulse )
    {
    }
} // end class PlayerContactListener
