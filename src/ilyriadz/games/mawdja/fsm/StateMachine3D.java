package ilyriadz.games.mawdja.fsm;

import com.bulletphysics.collision.narrowphase.PersistentManifold;
import ilyriadz.games.mawdja.core.GameObject;
import ilyriadz.games.mawdja.core.xyz.GameObject3D;
import ilyriadz.games.mawdja.core.xyz.contact.ContactReaction3D;
import ilyriadz.games.mawdja.core.xyz.contact.ContactUnreaction3D;
import java.io.Serializable;
import java.util.Objects;
import org.jbox2d.dynamics.contacts.Contact;

/**
 *
 * @author ilyes
 * @param <T>
 */
public class StateMachine3D implements Serializable, ContactReaction3D, ContactUnreaction3D
{

    private final GameObject3D owner;
    private State3D currentState = null;
    private State3D previousState = null;
    private State3D globalState = null;

    public StateMachine3D(GameObject3D owner)
    {
        this.owner = owner;
    } // end one-argument StateMachine constructor

    public State3D currentState()
    {
        return currentState;
    }

    public State3D previousState()
    {
        return previousState;
    }

    public State3D globalState()
    {
        return globalState;
    }

    public void changeGlobalState(State3D globalState)
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
    public void changeState(State3D newState) throws RuntimeException
    {
        Objects.nonNull(newState);

        previousState = currentState;
        if (currentState != null)
            currentState.exit(owner);

        currentState = newState;
        currentState.enter(owner);
    } // end method changeState

    @Override
    public void reaction(GameObject3D gameObject, PersistentManifold cont)
    {
        if (currentState != null)
            currentState.reaction(gameObject, cont);
    }

    @Override
    public void unreaction(GameObject3D gameObject, PersistentManifold cont) {
        if (currentState != null)
            currentState.unreaction(gameObject, cont);
    }
    
    
} // end class StateMachine
