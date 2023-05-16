/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ilyriadz.games.mawdja.core.xyz;

import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.InternalTickCallback;
import com.bulletphysics.dynamics.RigidBody;

/**
 *
 * @author kiradja
 */
public class DefaultInternalTickCallback extends InternalTickCallback
{
    private GameManager3D gameManager;
    public DefaultInternalTickCallback(GameManager3D gameManager)
    {
        this.gameManager = gameManager;
    }
    
    
    @Override
    public void internalTick(DynamicsWorld world, float timeStep) 
    {
        var dispatcher = world.getDispatcher();
        dispatcher.getInternalManifoldPointer()
                .forEach(pm ->
                {
                    var body1 = (RigidBody) pm.getBody0();
                    var body2 = (RigidBody) pm.getBody1();
                    
                    System.out.print(body1.getUserPointer());
                    System.out.println("\t" + body2.getUserPointer());
                });
    }
    
}
