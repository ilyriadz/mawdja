/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ilyriadz.games.mawdja.core;

import ilyriadz.games.mawdja.fsm.FlowField;
import ilyriadz.games.mawdja.fsm.FlowGraphPath;
import ilyriadz.games.mawdja.fsm.FlowPath;
import ilyriadz.games.mawdja.fsm.FlowPaths;
import ilyriadz.games.mawdja.fsm.StateMachine;
import static ilyriadz.games.mawdja.gutil.Gutil.distance;
import static ilyriadz.games.mawdja.gutil.Gutil.meters;
import static ilyriadz.games.mawdja.gutil.Gutil.normalPoint;
import static ilyriadz.games.mawdja.gutil.Gutil.pixels;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Shape3D;
import static org.jbox2d.common.MathUtils.map;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

/**
 *
 * @author kiradja
 */
public abstract class GameObject implements Combineable
{
    private final double x;
    private final double y;
    private String name;
    private Body body;
    private Runnable action = null;
    private final Node node;
    protected boolean intersectOffset = false;
    private final StateMachine fsm;
    private final GameManager gameManager;
    private Sprite sprite;
    private Runnable onDestroyedAction = null;
    protected Vec2 faceDirection = new Vec2();
    
    //steering
    protected float maxSpeed = 50f;
    protected float maxForce = 50f;

    private Vec2 target = null;

    private float desiredSeparation = 20;
    private float cohortDistance = 100;

    private float seekRatio = 1f;
    private float separateRatio = 1;
    private float alignRatio = 1;
    private float cohesionRatio = 1;
    
    private final Vec2 desired = new Vec2();
    
    private double normalSeekDistance = 0.5;
    
    private CombineValidator combineValidator;
    
    boolean toDestroyed;

    public GameObject(GameManager gameManager, Point2D position, Node node) 
    {        
        x = position.getX();
        y = position.getY();
        
        this.node = node; 
        this.node.setTranslateX(x);
        this.node.setTranslateY(y); 
        
        if (!(this instanceof LightObject))
        {
            this.node.translateZProperty().addListener(l ->
            {
                this.node.setViewOrder(this.node.getTranslateZ());
            });
        }

        if (this.node instanceof Shape3D)
        {
            var node3d = (Shape3D)this.node;
            node3d.setMaterial(new PhongMaterial(Color.WHITE));
            //node3d.setCullFace(CullFace.BACK);
        }
        
        node.setUserData(this);
        
        fsm = new StateMachine(this);
        this.gameManager = gameManager;
        name = getClass().getSimpleName();
    }
    
    
    public void update(float delta)
    {
        if (body != null && body.getType() != BodyType.STATIC)
        {
            var pos = body.getPosition();
            var node0 = node();
            node0.setTranslateX(pixels(pos.x));
            node0.setTranslateY(pixels(pos.y));
            node0.setRotate(Math.toDegrees(body.getAngle()));
        } // end if
        
        fsm.update();
        
        if (sprite != null)
            sprite.step(delta);
    }
    
    protected abstract org.jbox2d.collision.shapes.Shape createShape();
    
    public void create()
    {
        if (body != null)
            return;
        
        BodyDef bd = new BodyDef();
        bd.position.x = meters(x);
        bd.position.y = meters(y);
        
        FixtureDef fd = new FixtureDef();
        fd.density = 1;
        fd.shape = createShape();

        body = gameManager.world().createBody(bd);
        body.createFixture(fd);
        body.setUserData(this);
    }
    
    public final void destroyBody()
    {
        if (body != null)
        {
            gameManager.world().destroyBody(body);
            body = null;
        } // end if
    }

    public Body body() {
        return body;
    }
    
    public Node node() 
    {
        return node;
    }

    public Runnable action()
    {
        return action;
    }
    
    public void setAction(Runnable action)
    {
        this.action = action;
    }

    public GameManager gameManager()
    {
        return gameManager;
    }

    public StateMachine fsm() 
    {
        return fsm;
    }

    public PhongMaterial material() 
    {
        if (node instanceof Shape3D)
            return (PhongMaterial)((Shape3D)node).getMaterial();
        
        return null;
    }

    public Sprite sprite() 
    {
        return sprite;
    }

    public void setFaceDirection(double x, double y) {
        this.faceDirection.x = (float)x;
        this.faceDirection.y = (float)y;
    }

    public Vec2 faceDirection() {
        return faceDirection;
    }
       
    
    public void setSprite(Sprite sprite)
    {
        this.sprite = sprite;
    }

    public float maxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public float maxForce() {
        return maxForce;
    }

    public void setMaxForce(float maxForce) {
        this.maxForce = maxForce;
    }

    public Vec2 seekTarget() 
    {
        return target;
    }

    public void setTarget(Vec2 target)
    {
        this.target = target;
    }
    
    public void setTargetObject(GameObject target)
    {
        if (target.body() != null)
            this.target = target.body().getPosition();
        else
            Logger.getLogger(GameObject.class.getName()).log(Level.WARNING, 
                "GameObject target has a null body");
    }
    
    public void setTargetCloned(GameObject target)
    {
        if (target.body() != null)
            this.target = target.body().getPosition().clone();
        else
            Logger.getLogger(GameObject.class.getName()).log(Level.WARNING, 
                "GameObject target has a null body");
    }

    public float desiredSeparation() {
        return desiredSeparation;
    }

    public void setDesiredSeparation(float desiredSeparation) {
        this.desiredSeparation = desiredSeparation;
    }

    public float cohortDistance() {
        return cohortDistance;
    }

    public void setCohortDistance(float neighbordist) {
        this.cohortDistance = neighbordist;
    }

    public float seekRatio() {
        return seekRatio;
    }

    public void setSeekRatio(float seekRatio) {
        this.seekRatio = seekRatio;
    }

    public float separateRatio() {
        return separateRatio;
    }

    public void setSeparateRatio(float separateRatio) {
        this.separateRatio = separateRatio;
    }

    public float alignRatio() {
        return alignRatio;
    }

    public void setAlignRatio(float alignRatio) {
        this.alignRatio = alignRatio;
    }

    public float cohesionRatio() {
        return cohesionRatio;
    }

    public void setCohesionRatio(float cohesionRatio) {
        this.cohesionRatio = cohesionRatio;
    }

    public double normalSeekDistance() {
        return normalSeekDistance;
    }

    public void setNormalSeekDistance(double normalSeekDistance) {
        this.normalSeekDistance = normalSeekDistance;
    }
    
    protected Runnable onDestroyedAction()
    {
        return onDestroyedAction;
    }
    
    public void setOnDestroyedAction(Runnable run)
    {
        onDestroyedAction = run;
    }
    
    public void seek()
    {
        if (target == null || body == null)
            return;

        desired.set(target);
        desired.subLocal(body.getPosition());
        
        float d = desired.length();
        desired.normalize();

        float m = map(d, 0, maxSpeed, 0, maxSpeed);
        
        desired.mulLocal(m);
        
        desired.subLocal(body.getLinearVelocity());
        desired.normalize();
        desired.mulLocal(maxForce * seekRatio);

        body.applyForceToCenter(desired);

    } // end method seek
    
    public void normalSeek()
    {
        if (target == null || body == null)
            return;

        desired.set(target);
        desired.subLocal(body.getPosition());
        
        float d = desired.length();
        desired.normalize();

        if (d < normalSeekDistance)
        {
            float m = (float) map(d, 0, maxSpeed, 0, maxSpeed);
            desired.mulLocal(m);
        } // end if
        else
            desired.mulLocal(maxSpeed);

        desired.subLocal(body.getLinearVelocity());
        desired.normalize();
        desired.mulLocal(maxForce * seekRatio);

        body.applyForceToCenter(desired);
    } // end method seek
    
    public void seek(final Vec2 target, float ratio)
    {
        if (target == null || body == null)
            return;

        ratio = ratio > 0 ? ratio : 1;

        desired.set(target);
        desired.subLocal(body.getPosition());
        
        float d = desired.length();
        desired.normalize();

        if (d < normalSeekDistance)
        {
            float m = (float) map(d, 0, maxSpeed, 0, maxSpeed);
            //System.err.println( "m: " + m + ", distence: " + d );
            desired.mulLocal(m);
        } // end if
        else
        {
            desired.mulLocal(maxSpeed);
        }

        desired.subLocal(body.getLinearVelocity());
        desired.normalize();
        desired.mulLocal(maxForce * ratio);

        body.applyForceToCenter(desired);

    } // end method seek
    
    public void seeking()
    {
        if (target == null || body == null)
            return;

        desired.set(target);
        desired.subLocal(body.getPosition());
        //float d = desired.length();
        desired.normalize();

        desired.mulLocal(maxSpeed);

        desired.subLocal(body.getLinearVelocity());
        desired.normalize();
        desired.mulLocal(maxForce * seekRatio);

        body.applyForceToCenter(desired);

    } // end method
    
    public void pursuit()
    {
        if (target == null || body == null)
            return;

        desired.set(target);
        float x = (body.getPosition().x + body.getLinearVelocity().x);
        float y = (body.getPosition().y + body.getLinearVelocity().y);
        desired.set(desired.x - x, desired.y - y);
        desired.normalize();
        desired.mulLocal(maxSpeed);

        desired.subLocal(body.getLinearVelocity());
        desired.normalize();
        desired.mulLocal(maxForce);

        body.applyForceToCenter(desired);
    } // end method porsuit

    public void flee()
    {
        if (target == null || body == null)
            return;
        desired.set(target);
        desired.subLocal(body.getPosition());
        //Vec2 desired = target.sub(body.getPosition());
        desired.normalize();
        desired.mulLocal(maxSpeed);

        desired.subLocal(body.getLinearVelocity());
        desired.normalize();
        desired.mulLocal(maxForce);

        body.applyForceToCenter(desired.negate());

    } // end method flee

    public void followField(FlowField flow)
    {
        if (body == null)
            return;
        
        desired.set(flow.lookup(body.getPosition()));
        desired.mulLocal(maxSpeed);

        desired.subLocal(body.getLinearVelocity());
        desired.normalize();
        desired.mulLocal(maxForce);

        body.applyForceToCenter(desired);
    } // end method followField

    public void followPath(FlowPath path)
    {
        if (body == null)
            return;
        
        Vec2 predict = body.getLinearVelocity().clone();
        predict.normalize();
        predict.mulLocal(25);
        Vec2 predictLoc = body.getPosition().add(predict);

        Vec2 a = path.getStart();
        Vec2 b = path.getEnd();
        Vec2 normalPoint = normalPoint(predictLoc, a, b);

        Vec2 dir = b.sub(a);
        dir.normalize();
        dir.mulLocal(5);
        target = normalPoint.add(dir);

        double distence = distance(normalPoint, predictLoc);

        if (distence > path.getRadius())
        {
            seeking();
        }

    } // end method followPath

    public void flowPaths(FlowPaths paths)
    {
        if (body == null)
            return;
        
        setTarget(paths.getPoints().get(paths.getNextPoint()));
        seek();
        
        // this is floawPath not FlowGraphPath
        //System.out.println("?");

        if (distance(body.getPosition().clone(), paths.getPoints().get(paths.getNextPoint())) <= 10)
        {
            paths.nextPoint();
        }

    } // end method flowPaths
    
    public void FlowGraphPath(FlowGraphPath fgp, double distance)
    {
        if (body == null)
            return;
        
        target = fgp.nextPoint();
        
        if (target == null)
            return;
        
        setTarget(target);
        seek();
        
        var d = distance(body.getPosition(), target);

        if ((distance - d) <= 1)
            fgp.clearCurrentPoint();
    } // end method

    public void separate(ArrayList<GameObject> gameObjects)
    {
        if (body == null || body.getType() == BodyType.STATIC)
            return;

        Vec2 sum = new Vec2();
        int count = 0;

        for (var other : gameObjects)
        {
            if (other != this)
            {
                float d = (float)distance(body.getPosition(), other.body().getPosition());

                if (d < desiredSeparation)
                {
                    Vec2 diff = body.getPosition().sub(other.body().getPosition());
                    diff.normalize();
                    diff.set(diff.x / d, diff.y / d);
                    sum.addLocal(diff);
                    ++count;
                } // end if
            } // end if
        } // end for

        if (count > 0)
        {
            sum.set(sum.x / count, sum.y / count);
            sum.normalize();
            sum.mulLocal(maxSpeed);

            // steer
            sum.subLocal(body.getLinearVelocity());
            sum.normalize();
            sum.mulLocal(maxForce * separateRatio);

            body.applyForceToCenter(sum);
        } // end if

    } // end method separate

    /*public void flock( ArrayList< Boid > BOIDS )
    {
        
    } // end method flock*/
    public void align(ArrayList<GameObject> gameObjects)
    {
        if (target == null || body == null)
            return;
        
        Vec2 sum = new Vec2();
        int count = 0;

        for (var other : gameObjects)
        {
            if (other != this)
            {
                float d = (float)distance(body.getPosition(), other.body().getPosition());

                if (d < cohortDistance)
                {
                    sum.addLocal(other.body().getLinearVelocity());
                    ++count;
                } // end if             
            } // end if           
        } // end for

        if (count > 0)
        {
            sum.set(sum.x / gameObjects.size(), sum.y / gameObjects.size());

            sum.normalize();
            sum.mulLocal(maxSpeed);

            sum.subLocal(body.getLinearVelocity());
            sum.normalize();
            sum.mulLocal(maxForce * alignRatio);

            body.applyForceToCenter(sum);
        } // end if

    } // end method align

    public void cohesion(ArrayList<GameObject> gameObjects)
    {
        if (body == null)
            return;
        
        Vec2 sum = new Vec2();
        int count = 0;

        for (var other : gameObjects)
        {
            if (this != other)
            {
                float d = (float)distance(body.getPosition(), other.body().getPosition());
                if (d < cohortDistance)
                {
                    sum.addLocal(other.body().getPosition());
                    ++count;
                } // end if
            } // end if
        } // end for

        if (count > 0)
        {
            sum.set(sum.x / count, sum.y / count);
            seek(sum, cohesionRatio);
        } // end if
    } // end method cohesion

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(this.body);
        hash = 31 * hash + Objects.hashCode(this.node);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GameObject other = (GameObject) obj;
        if (!Objects.equals(this.body, other.body)) {
            return false;
        }
        return Objects.equals(this.node, other.node);
    }

    public void setObjectName(String name) {
        this.name = name;
    }

    public String objectName() {
        return name;
    }

    @Override
    public void setCombineValidator(CombineValidator combineValidator) 
    {
        this.combineValidator = combineValidator;
    }

    @Override
    public CombineValidator combineValidator()
    {
        return combineValidator;
    }
    
    

    @Override
    public String toString() {
        return name + '@' + hashCode();
    }
    
} // end class
