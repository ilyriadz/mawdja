/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ilyriadz.games.mawdja.core;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.jbox2d.common.Vec2;

/**
 *
 * @author kiradja
 */
public class DefaultGamePlayer extends GamePlayer
{
    private final Vec2 move = new Vec2();

    public DefaultGamePlayer() 
    {
    }

    public DefaultGamePlayer(GameObject gameObject) 
    {
        super(gameObject);
    }
 
    @Override
    public void onKeyPressed(KeyEvent kp)
    {
        switch (kp.getCode())
        {
            case W -> up();
            case S -> down();
            case D -> right();
            case A -> left();
        } // end switch
    }
    
    @Override
    public void onKeyReleased(KeyEvent kp)
    {
        switch (kp.getCode())
        {
            case W:
                notUp();
                break;
            case S:
                notDown();
                break;
            case D:
                notRight();
                break;
            case A:
                notLeft();
                break;
        }
    }
    
    @Override
    public void update(float delta)
    {
        if (right)
            move.x = 1;
        else if (left)
            move.x = -1;   
        else if (up)
            move.y = -1;
        else if (down)
            move.y = 1;
        
        if (move.length() != 0)
        {            
            move.normalize();
            move.x *= speed + delta;
            move.y *= speed + delta;
             
            var body = gameObject().body();
            body.applyForceToCenter(move);
            body.getLinearVelocity().setZero();
            move.setZero();
        } // end if
        
        if (!up && !down && !right && !left)
            changeSpriteAssets(restAssets);//*/      
    }
    

    @Override
    public void up() 
    {
        this.up = true;
        gameObject().faceDirection.y = -1;
        changeSpriteAssets(upAssets);
    }
    
    @Override
    public boolean isDown()
    {
        return down;
    }

    @Override
    public void down()
    {
        down = true;
        gameObject().faceDirection.y = 1;
        changeSpriteAssets(downAssets);
    }
    
    @Override
    public void right() 
    {
        this.right = true;
        gameObject().faceDirection.x = 1;
        changeSpriteAssets(rightAssets);
    }

    @Override
    public void left() 
    {
        this.left = true;
        gameObject().faceDirection.x = -1;
        changeSpriteAssets(leftAssets);
    }
    
    @Override
    public void onMousePressed(MouseEvent mp) {
        throw new UnsupportedOperationException("Not supported in " + getClass().getName());
    }

    @Override
    public void onMouseReleased(MouseEvent mr) {
        throw new UnsupportedOperationException("Not supported in " + getClass().getName());
    }

    @Override
    public void onMouseClicked(MouseEvent mc) {
        throw new UnsupportedOperationException("Not supported in " + getClass().getName()); 
    }

    @Override
    public void onMouseMoved(MouseEvent mm) {
        throw new UnsupportedOperationException("Not supported in " + getClass().getName());
    }
}
