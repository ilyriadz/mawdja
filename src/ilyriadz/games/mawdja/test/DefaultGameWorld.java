/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ilyriadz.games.mawdja.test;

import ilyriadz.games.mawdja.core.GameManager;
import ilyriadz.games.mawdja.core.GameWorld;
import ilyriadz.games.mawdja.core.GameZone;
import ilyriadz.games.mawdja.core.RectObject;
import javafx.scene.text.Text;

/**
 *
 * @author kiradja
 */
public class DefaultGameWorld extends GameWorld
{
    public final static int DEFAULT_GAME_WORLD_ID = Integer.MIN_VALUE;
    
    public DefaultGameWorld(GameManager gameManager) 
    {
        super(gameManager, DEFAULT_GAME_WORLD_ID, 1024, 600);
    }

    @Override
    public final void load() 
    {
        addGameObject("default", "rect1", new RectObject(
                gameManager(), 150, 250, 50, 25));
        addGameObject("default", "rect2", new RectObject(
                gameManager(), 350, 480, 100, 25));
        
        //getGameZone("default").getGameObject("rect1").create();
        //getGameZone("default").getGameObject("rect1")
                //.fsm().changeState(new CreateState(getGameZone("default")));
        
        addAction("action1", () ->
                {
                    var defaultGameZone = getGameZone("default");
                    var box = gameManager().player().gameObject().node();
                    if (box.getTranslateX() < 200 && !defaultGameZone.isLoaded())
                    {
                        getGameZone("default").load();
                    }
                });
        
        addAction("action2",  () ->
                {
                    var defaultGameZone = getGameZone("default");
                    var box = gameManager().player().gameObject().node();
        
        
                    if (box.getTranslateX() > 650 && defaultGameZone.isLoaded() && !defaultGameZone.isCreated())
                    {
                        getGameZone("default").creates();
                        changePlayerPosition(400, 400);
                    }
                });
        
        addAction("action3", () ->
        {
            var defaultGameZone = getGameZone("default");
            var box = gameManager().player().gameObject().node();
            
            if (box.getTranslateY() > 550 && box.getTranslateX() > 800
                    && defaultGameZone.isCreated())
            {
                defaultGameZone.destroy();
                removeAction("action3");
            } // end
        });
        
        Text t = (Text) gameManager().scene().lookup("#text");
        var defaultGameZone = getGameZone("default");
        addAction("action4", () ->
        {
            t.setText("loaded: " + defaultGameZone.isLoaded() + ", created: " +
                    defaultGameZone.isCreated());
        });
    }   
}
