/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ilyriadz.games.mawdja.core.xyz.contact;

import ilyriadz.games.mawdja.core.xyz.GameObject3D;
import java.util.Objects;

/**
 *
 * @author kiradja
 */
public final class ContactObject3D
{
    private final GameObject3D gameObject;
    private boolean contactBegin;

    public ContactObject3D(GameObject3D gameObject) 
    {
        Objects.nonNull(gameObject);
        this.gameObject = gameObject;
    }
    
    public final void intersect(GameObject3D other)
    {       
        if (gameObject.node().getBoundsInParent().intersects(other.node().getBoundsInParent()))
        {
            if (!contactBegin)
            {
                contactBegin = true;
                
                other.onContactBegin().contact(gameObject);
            } // end if
        }
        else
        {
            if (contactBegin)
            {
                contactBegin = false;
                other.onContactEnd().contact(gameObject);
            } // end if
        }
    }
    
}
