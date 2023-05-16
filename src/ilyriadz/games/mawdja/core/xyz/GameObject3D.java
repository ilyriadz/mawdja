/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ilyriadz.games.mawdja.core.xyz;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.QuaternionUtil;
import com.bulletphysics.linearmath.Transform;
import ilyriadz.games.mawdja.core.CombineValidator;
import ilyriadz.games.mawdja.core.Combineable;
import ilyriadz.games.mawdja.core.GameObject;
import ilyriadz.games.mawdja.core.Sprite;
import ilyriadz.games.mawdja.core.xyz.contact.ContactObject3D;
import ilyriadz.games.mawdja.core.xyz.contact.Contactable;
import ilyriadz.games.mawdja.fsm.StateMachine3D;
import ilyriadz.games.mawdja.gutil.Gutil;
import static ilyriadz.games.mawdja.gutil.Gutil.meters;
import ilyriadz.games.mawdja.gutil.Gutil3D;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Rotate;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 *
 * @author kiradja
 */
public abstract class GameObject3D implements Combineable
{
    private String name;
    protected RigidBody rigidBody;
    private Runnable action = null;
    private final Node node;
    private final StateMachine3D fsm;
    private final GameManager3D gameManager;
    private Sprite sprite;
    private Runnable onDestroyedAction = null;
    protected Vector3f faceDirection = new Vector3f();
    
    //steering
    protected float maxSpeed = 50f;
    protected float maxForce = 50f;

    private Vector3f target = null;

    private float desiredSeparation = 20;
    private float cohortDistance = 100;

    private float seekRatio = 1f;
    private float separateRatio = 1;
    private float alignRatio = 1;
    private float cohesionRatio = 1;
    
    private final Vector3f desired = new Vector3f();
    
    private double normalSeekDistance = 0.5;
    
    private CombineValidator combineValidator;
    
    boolean toDestroyed;
    
    private final Transform transform = new Transform();
    
    private final Rotate rotX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotY = new Rotate(0, Rotate.Y_AXIS);
    private final Rotate rotZ = new Rotate(0, Rotate.Z_AXIS);
    
    private float mass = 1.0f;
    
    private int onGroundCount = 0;
    private boolean alwaysManaged = false;
    private boolean fixedRotation = false;
    
    Contactable onContactBegin = null, onContactEnd = null;
    
    private final Map<String, ContactObject3D> contactedObjects = new HashMap<>();
    
    private final Map<String, ToSetOnGroundTarget> toSetOnGroundObjects = new HashMap<>();
    
    private boolean isJumping = false;

    public GameObject3D(GameManager3D gameManager, Point3D position, Node node) 
    {        
        this.node = node; 
        this.node.setTranslateX(position.getX());
        this.node.setTranslateY(position.getY()); 
        this.node.setTranslateZ(position.getZ());
        
        /*if (!(this instanceof LightObject3D))
        {
            this.node.translateYProperty().addListener(l ->
            {
                this.node.setViewOrder(this.node.getTranslateY());
            });
        }//*/

        if (this.node instanceof Shape3D shape3d)
        {
            shape3d.setMaterial(new PhongMaterial(Color.WHITE));
            shape3d.setCullFace(CullFace.BACK);
            shape3d.getTransforms().addAll(rotX, rotY, rotZ);
        }
        
        fsm = new StateMachine3D(this);
        
        this.gameManager = gameManager;
        
        name = getClass().getSimpleName();
    }
    
    public void create()
    {
        if (rigidBody != null)
            return;
        
        Transform tr = new Transform();
        tr.setIdentity();
        tr.origin.set(
            meters(node.translateXProperty().floatValue()), 
            meters(node.translateYProperty().floatValue()), 
            meters(node.translateZProperty().floatValue()));
        
        Vector3f localInertia = new Vector3f();
        
        var shape = createShape();
        
        shape.calculateLocalInertia(mass, localInertia);

        MotionState motionState = new DefaultMotionState(tr);
        
        rigidBody = new RigidBody(mass, motionState, shape,
            fixedRotation ? new Vector3f() : localInertia);

        gameManager.dynamicsWorld().addRigidBody(rigidBody);
        
        rigidBody.setUserPointer(this);
        
        setRigidBodyRotation(rotX.getAngle(), rotY.getAngle(), rotZ.getAngle());
    }
      
    public void update(float delta)
    {
        rigidBody.activate();
        
        if (!rigidBody.isStaticObject())
        {
            rigidBody.getMotionState().getWorldTransform(transform);
        
            node.setTranslateX(Gutil.pixels(transform.origin.x));
            node.setTranslateY(Gutil.pixels(transform.origin.y));
            node.setTranslateZ(Gutil.pixels(transform.origin.z));
        
            var angles = Gutil3D.getDegreeAngles(transform);
        
            rotX.setAngle(-angles.x);
            rotY.setAngle(-angles.y);
            rotZ.setAngle(-angles.z);
        }
        
        fsm.update();
        
        if (sprite != null)
            sprite.step(delta);
        
        if (!contactedObjects.isEmpty())
        {
            contactedObjects.values().forEach(contact -> contact.intersect(this));
        } // end if
        
        if (!toSetOnGroundObjects.isEmpty())
        {
            toSetOnGroundProgress(toSetOnGroundObjects);
        } // end if
    }
    
    protected abstract CollisionShape createShape();
    
    
    public final void destroyRigidBody()
    {
        if (rigidBody != null)
        {
            gameManager.dynamicsWorld().removeRigidBody(rigidBody);
            rigidBody = null;
        } // end if
    }

    public RigidBody rigidBody() {
        return rigidBody;
    }
    
    public void addToSetOnGround(String name, ToSetOnGroundTarget toSetOnGroundTarget)
    {
        if (Objects.isNull(name))
            throw new IllegalArgumentException("null name value");
        
        if (Objects.isNull(toSetOnGroundTarget))
            throw new IllegalArgumentException("null GemaObject3D");
        
        if (toSetOnGroundObjects.containsKey(name))
            throw new IllegalArgumentException("name " + "'".concat(name).concat(
                    "' already exist as to set on ground"));
        
        if (toSetOnGroundObjects.containsValue(toSetOnGroundTarget))
            throw new IllegalArgumentException("GameObject3D " + toSetOnGroundTarget +
                "' already exist as to set on ground");
        
        toSetOnGroundObjects.put(name, toSetOnGroundTarget);
    }
    
    public void removeFromToSetOnGround(String name)
    {
        if (Objects.isNull(name))
            throw new IllegalArgumentException("null key passed");
        
        var b = toSetOnGroundObjects.remove(name);
        
        if (b == null)
            Logger.getLogger(this.toString()).log(
                Level.WARNING, "no ToSetOnGround mapped with key ''{0}''", name);
    }
    
    private void toSetOnGroundProgress(Map<String, ToSetOnGroundTarget> map)
    {
        map.values().forEach(toSetOnGround ->
        {
            GameObject3D gob = toSetOnGround.gameObject();
            
            double xd = 0;
            double yd = 0;
            double zd = 0;
            
            if (this instanceof BoxObject box)
            {
                var nd = box.node();
                xd = nd.getWidth() / 2;
                yd = nd.getHeight() / 2;
                zd = nd.getDepth() / 2;
            } // end if
            else if (this instanceof SphereObject sp)
            {
                var nd = sp.node();
                xd = nd.getRadius();
                yd = xd;              
                zd = xd;
            } // end else if
            else if (this instanceof CylinderObject cy)
            {
                var nd = cy.node();
                xd = nd.getRadius();
                yd = xd;              
                zd = nd.getHeight() / 2;
            } // end else if
                
            if (node().getBoundsInParent().intersects(gob.node().getBoundsInParent())
                && node().getTranslateZ() > gob.node().getTranslateZ()/* - zd*/
                /*&& node().getTranslateZ() < gob.node().getTranslateY() + yd
                && node().getTranslateY() > gob.node().getTranslateY() - yd
                && node().getTranslateY() < gob.node().getTranslateY() + yd
                && node().getTranslateX() > gob.node().getTranslateX() - xd
                && node().getTranslateX() < gob.node().getTranslateX() + yd*/)
            {
                if (!toSetOnGround.isOnGround())
                {
                    gob.addGroundToCount();
                    toSetOnGround.setOnGround(true);
                }
            }
            else
            {
                if (toSetOnGround.isOnGround())
                {
                    gob.removeGroundFromCount();
                    toSetOnGround.setOnGround(false);
                }
            }
        });
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

    public GameManager3D gameManager3D()
    {
        return gameManager;
    }

    public StateMachine3D fsm() 
    {
        return fsm;
    }

    public PhongMaterial material() 
    {
        if (node instanceof Shape3D shape3D)
            return (PhongMaterial)shape3D.getMaterial();
        
        return null;
    }

    public Sprite sprite() 
    {
        return sprite;
    }

    public void setFaceDirection(double x, double y, double z) {
        this.faceDirection.x = (float)x;
        this.faceDirection.y = (float)y;
        this.faceDirection.z = (float)z;
    }

    public Vector3f faceDirection() {
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

    public Vector3f seekTarget() 
    {
        return target;
    }

    public void setTarget(Vector3f target)
    {
        this.target = target;
    }
    
    public void setTargetObject(GameObject3D targetObject)
    {
        if (targetObject.rigidBody() != null)
        {
            var tr = new Transform();
            targetObject.rigidBody().getMotionState().getWorldTransform(tr);
                        
            target = tr.origin;

        }
        else
            Logger.getLogger(GameObject.class.getName()).log(Level.WARNING, 
                "GameObject target has a null body");
    }
    
    public void setTargetCloned(GameObject3D targetObject)
    {
        if (targetObject == null)
            return;
        
        if (targetObject.rigidBody() != null)
        {
            var tr = new Transform();
            targetObject.rigidBody().getMotionState().getWorldTransform(tr);
            
            if (target == null)
                target = new Vector3f();
            
            target.set(tr.origin);
        }
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
    
    /*public void seek()
    {
        if (target == null || rigidBody == null)
            return;

        desired.set(target);
        desired.subLocal(rigidBody.getPosition());
        
        float d = desired.length();
        desired.normalize();

        float m = map(d, 0, maxSpeed, 0, maxSpeed);
        
        desired.mulLocal(m);
        
        desired.subLocal(rigidBody.getLinearVelocity());
        desired.normalize();
        desired.mulLocal(maxForce * seekRatio);

        rigidBody.applyForceToCenter(desired);

    } // end method seek
    
    public void normalSeek()
    {
        if (target == null || rigidBody == null)
            return;

        desired.set(target);
        desired.subLocal(rigidBody.getPosition());
        
        float d = desired.length();
        desired.normalize();

        if (d < normalSeekDistance)
        {
            float m = (float) map(d, 0, maxSpeed, 0, maxSpeed);
            desired.mulLocal(m);
        } // end if
        else
            desired.mulLocal(maxSpeed);

        desired.subLocal(rigidBody.getLinearVelocity());
        desired.normalize();
        desired.mulLocal(maxForce * seekRatio);

        rigidBody.applyForceToCenter(desired);
    } // end method seek
    
    public void seek(final Vec2 target, float ratio)
    {
        if (target == null || rigidBody == null)
            return;

        ratio = ratio > 0 ? ratio : 1;

        desired.set(target);
        desired.subLocal(rigidBody.getPosition());
        
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

        desired.subLocal(rigidBody.getLinearVelocity());
        desired.normalize();
        desired.mulLocal(maxForce * ratio);

        rigidBody.applyForceToCenter(desired);

    } // end method seek
    
    public void seeking()
    {
        if (target == null || rigidBody == null)
            return;

        desired.set(target);
        desired.subLocal(rigidBody.getPosition());
        //float d = desired.length();
        desired.normalize();

        desired.mulLocal(maxSpeed);

        desired.subLocal(rigidBody.getLinearVelocity());
        desired.normalize();
        desired.mulLocal(maxForce * seekRatio);

        rigidBody.applyForceToCenter(desired);

    } // end method
    
    public void pursuit()
    {
        if (target == null || rigidBody == null)
            return;

        desired.set(target);
        float x = (rigidBody.getPosition().x + rigidBody.getLinearVelocity().x);
        float y = (rigidBody.getPosition().y + rigidBody.getLinearVelocity().y);
        desired.set(desired.x - x, desired.y - y);
        desired.normalize();
        desired.mulLocal(maxSpeed);

        desired.subLocal(rigidBody.getLinearVelocity());
        desired.normalize();
        desired.mulLocal(maxForce);

        rigidBody.applyForceToCenter(desired);
    } // end method porsuit

    public void flee()
    {
        if (target == null || rigidBody == null)
            return;
        desired.set(target);
        desired.subLocal(rigidBody.getPosition());
        //Vec2 desired = target.sub(rigidBody.getPosition());
        desired.normalize();
        desired.mulLocal(maxSpeed);

        desired.subLocal(rigidBody.getLinearVelocity());
        desired.normalize();
        desired.mulLocal(maxForce);

        rigidBody.applyForceToCenter(desired.negate());

    } // end method flee

    public void followField(FlowField flow)
    {
        if (rigidBody == null)
            return;
        
        desired.set(flow.lookup(rigidBody.getPosition()));
        desired.mulLocal(maxSpeed);

        desired.subLocal(rigidBody.getLinearVelocity());
        desired.normalize();
        desired.mulLocal(maxForce);

        rigidBody.applyForceToCenter(desired);
    } // end method followField

    public void followPath(FlowPath path)
    {
        if (rigidBody == null)
            return;
        
        Vec2 predict = rigidBody.getLinearVelocity().clone();
        predict.normalize();
        predict.mulLocal(25);
        Vec2 predictLoc = rigidBody.getPosition().add(predict);

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
        if (rigidBody == null)
            return;
        
        setTarget(paths.getPoints().get(paths.getNextPoint()));
        seek();
        
        // this is floawPath not FlowGraphPath
        //System.out.println("?");

        if (distance(rigidBody.getPosition().clone(), paths.getPoints().get(paths.getNextPoint())) <= 10)
        {
            paths.nextPoint();
        }

    } // end method flowPaths
    
    public void FlowGraphPath(FlowGraphPath fgp, double distance)
    {
        if (rigidBody == null)
            return;
        
        target = fgp.nextPoint();
        
        if (target == null)
            return;
        
        setTarget(target);
        seek();
        
        var d = distance(rigidBody.getPosition(), target);

        if ((distance - d) <= 1)
            fgp.clearCurrentPoint();
    } // end method

    public void separate(ArrayList<GameObject> gameObjects)
    {
        if (rigidBody == null || rigidBody.getType() == BodyType.STATIC)
            return;

        Vec2 sum = new Vec2();
        int count = 0;

        for (var other : gameObjects)
        {
            if (other != this)
            {
                float d = (float)distance(rigidBody.getPosition(), other.rigidBody().getPosition());

                if (d < desiredSeparation)
                {
                    Vec2 diff = rigidBody.getPosition().sub(other.rigidBody().getPosition());
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
            sum.subLocal(rigidBody.getLinearVelocity());
            sum.normalize();
            sum.mulLocal(maxForce * separateRatio);

            rigidBody.applyForceToCenter(sum);
        } // end if

    } // end method separate

    public void flock( ArrayList< Boid > BOIDS )
    {
        
    } // end method flock
    public void align(ArrayList<GameObject> gameObjects)
    {
        if (target == null || rigidBody == null)
            return;
        
        Vec2 sum = new Vec2();
        int count = 0;

        for (var other : gameObjects)
        {
            if (other != this)
            {
                float d = (float)distance(rigidBody.getPosition(), other.rigidBody().getPosition());

                if (d < cohortDistance)
                {
                    sum.addLocal(other.rigidBody().getLinearVelocity());
                    ++count;
                } // end if             
            } // end if           
        } // end for

        if (count > 0)
        {
            sum.set(sum.x / gameObjects.size(), sum.y / gameObjects.size());

            sum.normalize();
            sum.mulLocal(maxSpeed);

            sum.subLocal(rigidBody.getLinearVelocity());
            sum.normalize();
            sum.mulLocal(maxForce * alignRatio);

            rigidBody.applyForceToCenter(sum);
        } // end if

    } // end method align

    public void cohesion(ArrayList<GameObject> gameObjects)
    {
        if (rigidBody == null)
            return;
        
        Vec2 sum = new Vec2();
        int count = 0;

        for (var other : gameObjects)
        {
            if (this != other)
            {
                float d = (float)distance(rigidBody.getPosition(), other.rigidBody().getPosition());
                if (d < cohortDistance)
                {
                    sum.addLocal(other.rigidBody().getPosition());
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
        hash = 31 * hash + Objects.hashCode(this.rigidBody);
        hash = 31 * hash + Objects.hashCode(this.node);
        return hash;
    }//*/

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
        
        final GameObject3D other = (GameObject3D) obj;
        if (!Objects.equals(this.rigidBody, other.rigidBody)) {
            return false;
        }
        return Objects.equals(this.node, other.node);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.name);
        return hash;
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
    
    public void setMass(double mass)
    {
        this.mass = mass <= 0 ? 0.0f : (float)mass;
    }

    public int onGroundCount() {
        return onGroundCount;
    }
    
    public boolean isOnGround()
    {
        return onGroundCount == 1 && !isJumping;
    }
    
    protected void addGroundToCount()
    {
        this.onGroundCount++;
    }
    
    protected void removeGroundFromCount()
    {
        this.onGroundCount--;
    }

    public boolean isAlwaysManaged() {
        return alwaysManaged;
    }
    
    public void setAlwaysManaged(boolean alwaysManaged)
    {
        this.alwaysManaged = alwaysManaged;
    }

    public boolean isFixedRotation() 
    {
        return fixedRotation;
    }

    public void setFixedRotation(boolean fixedRotation) {
        this.fixedRotation = fixedRotation;
    }
    
    public final void addContactObject(String name, GameObject3D gameObject)
    {
        if (Objects.isNull(name) || Objects.isNull(gameObject))
            throw new IllegalArgumentException("null argument(s)");
        
        if (gameObject == this)
            throw new IllegalArgumentException("can not contact to itself");
        
        if (contactedObjects.containsKey(name))
            throw new IllegalArgumentException("ContactObject already exists");
            
        contactedObjects.put(name, new ContactObject3D(gameObject));
    }
    
    public final void removeContactObject(String name)
    {
        contactedObjects.remove(name);
    }
    
    public final ContactObject3D getContactObject3D(String name)
    {
        return contactedObjects.get(name);
    }
    
    public final void setOnContactBegin(Contactable contactable)
    {
        this.onContactBegin = contactable;
    }
    
    public final void setOnContactEnd(Contactable contactable)
    {
        this.onContactEnd = contactable;
    }
    
    public final Map<String, ContactObject3D> contactObjects()
    {
        return Collections.unmodifiableMap(contactedObjects);
    }
    
    public final Contactable onContactBegin() {return onContactBegin;}
    public final Contactable onContactEnd() {return onContactEnd;}

    public Rotate rotX() 
    {
        return rotX;
    }

    public Rotate rotY() {
        return rotY;
    }

    public Rotate rotZ() {
        return rotZ;
    }
    
    public final void setRigidBodyRotationX(double angle)
    {
        if (rigidBody == null)
        {
            Logger.getLogger(this.toString()).log(Level.WARNING, 
                "can't set x rotation to null RigidBody");
            return;
        } // end if
        
        var tr = new Transform();
        rigidBody.getWorldTransform(tr);
        
        Quat4f rot = new Quat4f();
        //tr.getRotation(rot);
        
        /*QuaternionUtil.setRigidBodyRotation(rot, new Vector3f(1, 0, 0), 
            (float)Math.toRadians(angle));//*/
        QuaternionUtil.setEuler(rot, (float)Math.toRadians(rotY.getAngle()), 
            (float)Math.toRadians(rotZ.getAngle()), 
            (float)Math.toRadians(angle));      
        
        tr.setRotation(rot);
        
        rigidBody.setWorldTransform(tr);
        
        Gutil3D.updateAngles(this, tr);
        
    }
    
    public final void setRigidBodyRotationY(double angle)
    {
        if (rigidBody == null)
        {
            Logger.getLogger(this.toString()).log(Level.WARNING, 
                "can't set y rotation to null RigidBody");
            return;
        } // end if
        
        var tr = new Transform();
        rigidBody.getWorldTransform(tr);
        
        Quat4f rot = new Quat4f();
        tr.getRotation(rot);
        
        /*QuaternionUtil.setRigidBodyRotation(rot, new Vector3f(0, 1, 0), 
            (float)Math.toRadians(angle));*/
        
        QuaternionUtil.setEuler(rot, (float)Math.toRadians(angle), 
            (float)Math.toRadians(rotZ.getAngle()), 
            (float)Math.toRadians(rotX.getAngle()));
        
        tr.setRotation(rot);
        
        rigidBody.setWorldTransform(tr);
        
        Gutil3D.updateAngles(this, tr);
    }
    
    public final void setRigidBodyRotationZ(double angle)
    {
        if (rigidBody == null)
        {
            Logger.getLogger(this.toString()).log(Level.WARNING, 
                "can't set z rotation to null RigidBody");
            return;
        } // end if
        
        var tr = new Transform();
        rigidBody.getWorldTransform(tr);
        
        Quat4f rot = new Quat4f();
        tr.getRotation(rot);
        
        //QuaternionUtil.setRigidBodyRotation(rot, new Vector3f(0, 0, 1), 
            //(float)Math.toRadians(angle));
            
        QuaternionUtil.setEuler(rot, (float)Math.toRadians(rotY.getAngle()), 
            (float)Math.toRadians(angle), 
            (float)Math.toRadians(rotX.getAngle()));
        
        tr.setRotation(rot);
        
        rigidBody.setWorldTransform(tr);
        
        Gutil3D.updateAngles(this, tr);
    }
    
    public final void setRigidBodyRotation(double xAngle, double yAngle, double zAngle)
    {
        if (rigidBody == null)
        {
            Logger.getLogger(this.toString()).log(Level.WARNING, 
                "can't set xyz rotations to null RigidBody");
            return;
        } // end if
        
        var tr = new Transform();
        rigidBody.getWorldTransform(tr);
        
        Quat4f rot = new Quat4f();
        tr.getRotation(rot);
        
        //QuaternionUtil.setRigidBodyRotation(rot, new Vector3f(0, 0, 1), 
            //(float)Math.toRadians(angle));
            
        QuaternionUtil.setEuler(rot, (float)Math.toRadians(yAngle), 
            (float)Math.toRadians(zAngle), 
            (float)Math.toRadians(xAngle));
        
        tr.setRotation(rot);
        
        rigidBody.setWorldTransform(tr);
        
        Gutil3D.updateAngles(this, tr);
    }
    
    public final void translateToX(double px)
    {
        var transform = new Transform();
        rigidBody.getMotionState().getWorldTransform(transform);
        transform.origin.x = meters(px);
        
        rigidBody.setWorldTransform(transform);
    }
    
    public final void translateToY(double py)
    {
        var transform = new Transform();
        rigidBody.getMotionState().getWorldTransform(transform);
        transform.origin.y = meters(py);
        
        rigidBody.setWorldTransform(transform);
    }
    
    public final void translateToXY(double px, double py)
    {
        var transform = new Transform();
        rigidBody.getMotionState().getWorldTransform(transform);
        transform.origin.x = meters(px);
        transform.origin.y = meters(py);
        
        rigidBody.setWorldTransform(transform);
    }
    
    public final void translateToZ(double pz)
    {
        var transform = new Transform();
        rigidBody.getMotionState().getWorldTransform(transform);
        transform.origin.z = meters(pz);
        
        rigidBody.setWorldTransform(transform);
    }
    
    public final void translateTo(double px, double py, double pz)
    {
        var transform = new Transform();
        rigidBody.getMotionState().getWorldTransform(transform);
        transform.origin.x = meters(px);
        transform.origin.y = meters(py);
        transform.origin.z = meters(pz);
        
        rigidBody.setWorldTransform(transform);
    }

    public boolean isJumping() {
        return isJumping;
    }

    public void setJumping(boolean isJumping) {
        this.isJumping = isJumping;
    }

    @Override
    public String toString() {
        return name + '@' + hashCode();
    }
}
