/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ilyriadz.games.mawdja.fsm.states;

import ilyriadz.games.mawdja.core.GameObject;
import ilyriadz.games.mawdja.fsm.State;

/**
 *
 * @author kiradja
 */
public class Seek extends State
{
    
    private Seek() {
    }
    
    public static Seek getInstance() {
        return SeekHolder.INSTANCE;
    }

    @Override
    public void enter(GameObject gameObject) 
    {
    }

    @Override
    public void execute(GameObject gameObject) 
    {
        gameObject.seek();
    }

    @Override
    public void exit(GameObject gameObject) {
    }
    
    private static class SeekHolder {

        private static final Seek INSTANCE = new Seek();
    }
}
