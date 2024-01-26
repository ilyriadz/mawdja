/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ilyriadz.games.mawdja.core;

import ilyriadz.games.mawdja.gutil.Gutil;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.jbox2d.common.Vec2;

/**
 *
 * @author kiradja
 */
public class MouseGamePlayer extends GamePlayer
{
    private final GameManager gameManager;
    private final Vec2 target = new Vec2();
    private final Text t = new Text();
    public MouseGamePlayer(GameManager gameManager, GameObject gameObject) 
    {
        super(gameObject);
        this.gameManager = gameManager;
        gameObject.setTarget(target);
        
        gameManager.uiRoot().getChildren().add(t);
        StackPane.setAlignment(t, Pos.TOP_LEFT);
        t.setFill(Color.WHITE);
        t.setStrokeWidth(1);
        t.setStroke(Color.BLACK);
        t.setFont(Font.font("mono", 24));
    }
    
    @Override
    public void onKeyPressed(KeyEvent kp) {
        throw new UnsupportedOperationException("Not supported in " + getClass().getName()); 
    }

    @Override
    public void onKeyReleased(KeyEvent kr) {
        throw new UnsupportedOperationException("Not supported in " + getClass().getName());
    }

    @Override
    public void onMousePressed(MouseEvent mp) 
    {
        
    }

    @Override
    public void onMouseReleased(MouseEvent mr) 
    {
        
    }

    @Override
    public void onMouseClicked(MouseEvent mc) 
    {   
        target.x = Gutil.meters(mc.getX());
        target.y = Gutil.meters(mc.getY());   
    }

    @Override
    public void update(float delta) 
    {
    }

    @Override
    public void onMouseMoved(MouseEvent mm) 
    {             
    }
    
}
