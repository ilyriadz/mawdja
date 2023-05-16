/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ilyriadz.games.mawdja.core.xyz;

import com.bulletphysics.dynamics.RigidBody;
import ilyriadz.games.mawdja.core.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

/**
 *
 * @author kiradja
 */
public abstract class GamePlayer3D 
{
    protected boolean up, down, right, left;
    protected Vec2 move = new Vec2();
    protected float speed = 1.5f;
    private GameObject3D gameObject;
    protected boolean jump;

    
    private boolean isMoving = true;
    
    protected AssetsData upAssets, downAssets, rightAssets, leftAssets,
            upRightAssets, upLeftAssets, downRightAssets, downLeftAssets,
            restAssets;
    
    public static class AssetsData
    {
        private final String name;
        private final boolean play;

        public AssetsData(String name, boolean play) 
        {
            this.name = name;
            this.play = play;
        }

        public String name() {
            return name;
        }
        
        public boolean isPlay() 
        {
            return play;
        }   
    };

    public GamePlayer3D() {
    }
    
    

    public GamePlayer3D(GameObject3D gameObject)
    {
        this.gameObject = gameObject;
    } // end constructor

    public GameObject3D gameObject() 
    {
        return gameObject;
    }

    public void setGameObject(GameObject3D gameObject) {
        this.gameObject = gameObject;
    }
    
    protected final void changeSpriteAssets(AssetsData assetsData)
    {
        if (assetsData == null)
            return;
        var sprite = gameObject.sprite();
        
        if (sprite != null)
        {
            sprite.setAsset(assetsData.name()); 
            if (assetsData.play)
                sprite.play();
            else
                sprite.stop();
        } // end if*/
    }
    
    public boolean isUp() 
    {
        return up;
    }

    public void up() 
    {
        this.up = true;
        if (isRight())
            changeSpriteAssets(upRightAssets);
        else if (isLeft())
            changeSpriteAssets(upLeftAssets);
        else
            changeSpriteAssets(upAssets);
    }
    
    public void notUp()
    {
        up = false;
    }

    public boolean isDown()
    {
        return down;
    }

    public void down()
    {
        down = true;
        if (isRight())
            changeSpriteAssets(downRightAssets);
        else if (isLeft())
            changeSpriteAssets(downLeftAssets);
        else
            changeSpriteAssets(downAssets);
    }
    
    public void notDown()
    {
        down = false;
    }

    public boolean isRight() 
    {
        return right;
    }

    public void right() 
    {
        this.right = true;
        if (isUp())
            changeSpriteAssets(upRightAssets);
        else if (isDown())
            changeSpriteAssets(downRightAssets);
        else
            changeSpriteAssets(rightAssets);
    }
    
    public void notRight()
    {
        right = false;
    }

    public boolean isLeft() 
    {
        return left;
    }

    public void left() 
    {
        this.left = true;
        if (isUp())
            changeSpriteAssets(upLeftAssets);
        else if (isDown())
            changeSpriteAssets(downLeftAssets);
        else
            changeSpriteAssets(leftAssets);
    }
    
    public void notLeft()
    {
        left = false;
    }
    
    public boolean isUpRight()
    {
        return up && right;
    }
    
    public void upRight()
    {
        up = right = true;
    }
    
    public void notUpRight()
    {
        up = right = false;
    }
    
    public boolean isUpLeft()
    {
        return up && left;
    }
    
    public void upLeft()
    {
        up = left = true;
    }
    
    public void notUpLeft()
    {
        up = false;
        left = false;
    }
    
    public boolean isDownRight()
    {
        return down && right;
    }
    
    public void downRight()
    {
        down = right = true;
    }
    
    public void notDownRight()
    {
        down = right = false;
    }
    
    public boolean isDownLeft()
    {
        return down && left;
    }
    
    public void downLeft()
    {
        down = left = true;
    }
    
    public void notDownLeft()
    {
        down = false;
        left = false;
    }
    
    public void setUpAssets(String assetsName, boolean play)
    {
        upAssets = new AssetsData(assetsName, play);
    }
    
    public void setDownAssets(String assetsName, boolean play)
    {
        downAssets = new AssetsData(assetsName, play);;
    }
    
    public void setRightAssets(String assetsName, boolean play)
    {
        rightAssets = new AssetsData(assetsName, play);
    }
    
    public void setLeftAssets(String assetsName, boolean play)
    {
        leftAssets = new AssetsData(assetsName, play);
    }
    
    public void setUpRightAssets(String assetsName, boolean play)
    {
        upRightAssets = new AssetsData(assetsName, play);
    }
    
    public void setUpLeftAssets(String assetsName, boolean play)
    {
        upLeftAssets = new AssetsData(assetsName, play);
    }
    
    public void setDownRightAssets(String assetsName, boolean play)
    {
        downRightAssets = new AssetsData(assetsName, play);
    }
    
    public void setdownLeftAssets(String assetsName, boolean play)
    {
        downLeftAssets = new AssetsData(assetsName, play);
    }
    
    public void setRestAssets(String assetsName, boolean play)
    {
        restAssets = new AssetsData(assetsName, play);
    }
    
    public float speed() 
    {
        return speed;
    }

    public void setSpeed(double speed)
    {
        this.speed = (float)speed;
    }

    public boolean isJumping() 
    {
        return jump;
    }

    public void jump() 
    {
        this.jump = true;
    }
    
    public void notJump()
    {
        jump = false;
    }

    public RigidBody rigidBody()
    {
        return gameObject.rigidBody();
    }
    
    public abstract void onKeyPressed(KeyEvent kp);    
    public abstract void onKeyReleased(KeyEvent kr);
    public abstract void onMousePressed(MouseEvent mp);
    public abstract void onMouseReleased(MouseEvent mr);
    public abstract void onMouseClicked(MouseEvent mc);
    public abstract void onMouseMoved(MouseEvent mm);
    
    public abstract void update(float delta);
    
    public void notMove() {isMoving = false;}
    public void moving() {isMoving = true;}

    public boolean isMoving() {return isMoving;}
} // end method
