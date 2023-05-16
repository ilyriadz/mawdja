package ilyriadz.games.mawdja.fsm;

import ilyriadz.games.mawdja.contact.ContactReaction;
import ilyriadz.games.mawdja.contact.ContactUnreaction;
import ilyriadz.games.mawdja.core.GameObject;
import java.io.Serializable;
import org.jbox2d.dynamics.contacts.Contact;

/**
 *
 * @author ilyes
 * @param <T>
 */
public class StateMachine implements Serializable, ContactReaction, ContactUnreaction
{

    private final GameObject owner;
    private State currentState = null;
    private State previousState = null;
    private State globalState = null;

    public StateMachine(GameObject owner)
    {
        this.owner = owner;
    } // end one-argument StateMachine constructor

    public State currentState()
    {
        return currentState;
    }

    public State previousState()
    {
        return previousState;
    }

    public State globalState()
    {
        return globalState;
    }

    public void changeGlobalState(State globalState)
    {
        if (this.globalState != null)
            this.globalState.exit(owner);
        
        this.globalState = globalState;
        if (globalState != null)
            globalState.enter(owner);
    }

    public void update()
    {
        if (globalState != null)
        {
            globalState.execute(owner);
        }

        if (currentState != null)
        {
            currentState.execute(owner);
        }
    } // end method update

    /**
     *
     * @param newState
     * @throws RuntimeException if newState is null
     */
    public void changeState(State newState) throws RuntimeException
    {
        if (newState == null)
        {
            throw new RuntimeException("newState is null!!!");
        }

        previousState = currentState;
        if (currentState != null)
            currentState.exit(owner);

        currentState = newState;
        currentState.enter(owner);
    } // end method changeState

    @Override
    public void reaction(GameObject gameObject, Contact cont)
    {
        if (currentState != null)
            currentState.reaction(gameObject, cont);
    }

    @Override
    public void unreaction(GameObject gameObject, Contact cont) {
        if (currentState != null)
            currentState.unreaction(gameObject, cont);
    }
    
    
} // end class StateMachine
