package ilyriadz.games.mawdja.fsm;

import ilyriadz.games.mawdja.contact.ContactReaction;
import ilyriadz.games.mawdja.contact.ContactUnreaction;
import ilyriadz.games.mawdja.core.GameObject;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jbox2d.dynamics.contacts.Contact;

/**
 *
 * @author ilyes
 * @param <T>
 */
public abstract class State implements Serializable,
    ContactReaction, ContactUnreaction
{
    public abstract void enter(GameObject gameObject);

    public abstract void execute(GameObject gameObject);

    public abstract void exit(GameObject gameObject);

    @Override
    public void reaction(GameObject gameObject, Contact cont)
    {
        System.out.println("cntact:" + gameObject + " A:" + cont.m_fixtureA.m_body.getUserData() +
                " B:" + cont.m_fixtureB.m_body.getUserData());
    }

    @Override
    public void unreaction(GameObject gameObject, Contact cont)
    {
        System.out.println("uncontact:" + gameObject + " A:" + cont.m_fixtureA.m_body.getUserData() +
                " B:" + cont.m_fixtureB.m_body.getUserData());
    }
    
    
} // end class State
