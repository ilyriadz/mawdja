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
public class Seeking extends State
{
    
    private Seeking() {
    }
    
    public static Seeking getInstance() {
        return SeekingHolder.INSTANCE;
    }

    @Override
    public void enter(GameObject gameObject) 
    {
    }

    @Override
    public void execute(GameObject gameObject) 
    {
        gameObject.seeking();
    }

    @Override
    public void exit(GameObject gameObject) {
    }
    
    private static class SeekingHolder {

        private static final Seeking INSTANCE = new Seeking();
    }
}
