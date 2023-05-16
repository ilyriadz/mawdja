/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ilyriadz.games.mawdja.test;

import ilyriadz.games.mawdja.core.GameObject;
import ilyriadz.games.mawdja.core.GameZone;
import ilyriadz.games.mawdja.fsm.State;
import org.jbox2d.dynamics.contacts.Contact;

/**
 *
 * @author kiradja
 */
public class CreateState extends State
{
    private GameZone gameZone;
    public CreateState(GameZone gameZone) 
    {
        this.gameZone = gameZone;
    }
    
    
    @Override
    public void enter(GameObject gameObject) {
        
    }

    @Override
    public void execute(GameObject gameObject) {
        
    }

    @Override
    public void exit(GameObject gameObject) {
    }

    @Override
    public void reaction(GameObject gameObject, Contact cont) {
        gameZone.destroyBodies();
    }
    
    
    
}
