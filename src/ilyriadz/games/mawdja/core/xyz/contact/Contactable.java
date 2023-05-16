/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ilyriadz.games.mawdja.core.xyz.contact;

import ilyriadz.games.mawdja.core.xyz.GameObject3D;

/**
 *
 * @author ilyriadz
 */
@FunctionalInterface
public interface Contactable 
{
    void contact(GameObject3D gameObject);
}
