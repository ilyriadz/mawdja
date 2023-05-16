/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ilyriadz.games.mawdja.core.xyz;

import java.util.Objects;

/**
 *
 * @author kiradja
 */
public class ToSetOnGroundTarget 
{
    private final GameObject3D gameObject;
    private boolean isOnGround;

    public ToSetOnGroundTarget(GameObject3D gameObject) 
    {
        if (Objects.isNull(gameObject))
            throw new IllegalArgumentException(
                "pass a null object to constructor");
        
        this.gameObject = gameObject;
    }

    public GameObject3D gameObject() 
    {
        return gameObject;
    }

    public boolean isOnGround() 
    {
        return isOnGround;
    }

    public void setOnGround(boolean isOnGround)
    {
        this.isOnGround = isOnGround;
    }
    
    
    
}
