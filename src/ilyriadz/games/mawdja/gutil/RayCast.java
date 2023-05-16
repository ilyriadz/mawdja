/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ilyriadz.games.mawdja.gutil;

import ilyriadz.games.mawdja.core.GameManager;
import ilyriadz.games.mawdja.core.GameObject;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;

/**
 *
 * @author kiradja
 */
public class RayCast implements RayCastCallback
{
    private Fixture fixture;
    private Vec2 point;
    private Vec2 normal;
    private float friction;
    
    private final GameObject source, target;

    public RayCast(GameObject source, GameObject target) {
        this.source = source;
        this.target = target;
    }
    
    @Override
    public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) 
    {
        this.fixture = fixture;
        this.point = point;
        this.normal = normal;
        this.friction = fraction;
        
        return fraction;
    }
    
    public void rayCast(GameManager gameManager)
    {
        gameManager.world().raycast(this, source.body().getPosition(), target.body().getPosition());
    }
    
    public boolean isRayCastDitectedToFace(GameManager gameManager, Vec2 face)
    {
        rayCast(gameManager);
        var relativeNormal = Gutil.relativeVector(target.body().getPosition(), 
                source.body().getPosition());
        var sourceNormal = source.body().getPosition().clone();
        
        //relativeNormal.normalize();
        sourceNormal.normalize();
        
        var angle = Gutil.relativeAngle(source.body().getPosition(), target.body().getPosition());
        System.out.println(angle);
        
        return isRayCastDitected() && (angle < 90 || angle > 270)
                && (Vec2.dot(face, relativeNormal) < 0);
    }
    
    public boolean isRayCastDitected()
    {
        return fixture.m_body != null && fixture.m_body == target.body();
    }

    public Fixture fixture() {
        return fixture;
    }

    public Vec2 point() {
        return point;
    }

    public Vec2 normal() {
        return normal;
    }

    public float friction() {
        return friction;
    }

    public GameObject source() {
        return source;
    }

    public GameObject target() {
        return target;
    }
    
    

    @Override
    public String toString() {
        return "RayCast{" + "fixture=" + fixture + ", point=" + point + ", normal=" + normal + ", friction=" + friction + '}';
    }
    
    
    
}
