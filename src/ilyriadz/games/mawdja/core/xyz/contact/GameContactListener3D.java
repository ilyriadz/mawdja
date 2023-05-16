
package ilyriadz.games.mawdja.core.xyz.contact;

import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.InternalTickCallback;
import com.bulletphysics.dynamics.RigidBody;
import ilyriadz.games.mawdja.contact.*;
import java.util.Queue;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

/**
 *
 * @author ilyes
 */
public class GameContactListener3D extends InternalTickCallback
{
    private final Queue<PersistentManifold> contactQueue;
    private final Queue<PersistentManifold> uncontactQueue;
    
    public GameContactListener3D(Queue<PersistentManifold> contactQueue, 
            Queue<PersistentManifold> uncontactQueue)
    {
        this.contactQueue = contactQueue;
        this.uncontactQueue = uncontactQueue;
    }

    
    @Override
    public void internalTick(DynamicsWorld world, float timeStep)
    {
        world.getDispatcher().getInternalManifoldPointer().parallelStream()
                .forEach(pm ->
                {
                    contactQueue.add(pm);
                });
    }
} // end class PlayerContactListener
