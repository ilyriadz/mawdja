/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ilyriadz.games.mawdja.core.xyz;

import ilyriadz.games.mawdja.gutil.Gutil;
import static ilyriadz.games.mawdja.gutil.Gutil.meters;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Box;
import javax.vecmath.Vector3f;

/**
 *
 * @author kiradja
 */
public class DefaultGamePlayer3D extends GamePlayer3D
{
    private final Vector3f move = new Vector3f();
    private final Vector3f linearVelocity = new Vector3f();

    public DefaultGamePlayer3D() {
    }

    public DefaultGamePlayer3D(GameObject3D gameObject) 
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
        
        if (kp.getCode() == KeyCode.NUMPAD0 && gameObject().isOnGround())
            jump();
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
            case NUMPAD0:
                notJump();
        }
        
    }
    
    @Override
    public void update(float delta)
    {
        var body = gameObject().rigidBody();
        
        if (right)
            move.x = 1;
        else if (left)
            move.x = -1;   
        else if (up)
            move.y = -1;
        else if (down)
            move.y = 1;
        
        /*if (isJumping())
        {
            move.z = -1;
            notJump();
        }//*/
        
        if (move.length() != 0)
        {            
            move.normalize();
            move.x *= speed;
            move.y *= speed;
            
            body.applyCentralForce(move);
            
            move.x = 0;
            move.y = 0;
            //move.z = 0;
        }
        
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
