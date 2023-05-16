/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ilyriadz.games.mawdja.core.xyz;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.broadphase.Dispatcher;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.narrowphase.PersistentManifold;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import ilyriadz.games.mawdja.core.media.SoundManager;
import ilyriadz.games.mawdja.menu.GameStartInto;
import ilyriadz.games.mawdja.message.Telegram3D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.animation.AnimationTimer;
import javafx.beans.property.DoubleProperty;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.AmbientLight;
import javafx.scene.Camera;
import javafx.scene.DepthTest;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javax.vecmath.Vector3f;

/**
 *
 * @author kiradja
 */
public class GameManager3D extends AnimationTimer
{
    public final static float STEP_TIME = 1f / 60f;
    public static boolean debug = true;
    private final StackPane globalRoot = new StackPane();
    private final StackPane uiRoot = new StackPane();
    private final Group gameRoot = new Group();
    private final Scene scene;
    private final SubScene gameScene;
    private final Camera camera = new PerspectiveCamera(true);
    private DynamicsWorld dynamicsWorld;
    private final List<GameObject3D> gameObjects = new ArrayList<>();
    private final Queue<PersistentManifold> contactQueue = new ArrayDeque<>();
    private final Queue<PersistentManifold> uncontactQueue = new ArrayDeque<>();
    private final Queue<Telegram3D> telegramDispatcher = new ArrayDeque<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    protected final Queue<GameObject3D> ADDED_GAME_OBJECTS = 
        new ArrayDeque<>();
    protected final Queue<GameObject3D> DESTROYED_GAME_OBJECTS = 
        new ArrayDeque<>();

    private GamePlayer3D player;
    
    private GameWorld3D currentGameWorld = null, nextWorld = null;
    boolean worldDestoroyed = false;
    
    private final AmbientLight ambientLight = new AmbientLight(Color.WHITE);
    
    private long lastTime = Long.MIN_VALUE;
    private float delta;
    
    //private PersistentManifold contact, uncontact;
    private Telegram3D telegram;
    
    public enum GameState {RUNNING, STOPPED, PAUSED}
    
    private GameState gameState = GameState.STOPPED;

    private boolean destroyCurrentWorld = false;
    
    private GameStartInto gameStartIntro;
    
    private SoundManager soundManager;
    
    private static record Force(GameObject3D gameObject, Vector3f force,
        Vector3f point) {}    
    private Set<Force> forces = new HashSet<>();

    public GameManager3D(double SceneWidth, double sceneHeight) 
    {
        gameRoot.setDepthTest(DepthTest.ENABLE);
        gameScene = new SubScene(gameRoot, SceneWidth, sceneHeight, 
            true, SceneAntialiasing.BALANCED);
        gameScene.setFill(Color.BLACK);
        gameScene.setDepthTest(DepthTest.ENABLE);
        globalRoot.getChildren().addAll(gameScene, uiRoot);
        
        scene = new Scene(globalRoot, SceneWidth, sceneHeight, true, 
            SceneAntialiasing.BALANCED);
        
        gameScene.widthProperty().bind(scene.widthProperty());
        gameScene.heightProperty().bind(scene.heightProperty());
        gameScene.setCamera(camera);
        gameScene.setManaged(false);
        
        camera.setTranslateZ(-500);
        camera.setNearClip(0.01);
        camera.setFarClip(100000);
        
        //world.setContactListener(
            //new GameContactListener(contactQueue, uncontactQueue));
        dynamicsWorld = createDynamicsWorld();
        //dynamicsWorld.setInternalTickCallback(new DefaultInternalTickCallback(this), null);
        
        gameRoot.getChildren().add(ambientLight);
        
    }
    
    private static DynamicsWorld createDynamicsWorld()
    {
        CollisionConfiguration collisionConfiguration = 
            new DefaultCollisionConfiguration();
        Dispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);
        
        BroadphaseInterface broadphaseInterface = new DbvtBroadphase();
        
        ConstraintSolver constraintSolver =
            new SequentialImpulseConstraintSolver();
        
        return new DiscreteDynamicsWorld(dispatcher, 
            broadphaseInterface, constraintSolver, collisionConfiguration);
    }
    
    protected void addObject(GameObject3D gameObject) 
    {
        gameRoot().getChildren().add(gameObject.node());
        gameObjects.add(gameObject);
        gameObject.node().setUserData(gameObject);
    }

    protected void destroyObject(GameObject3D gameObject) 
    {
        // before destroying
        if (gameObject.onDestroyedAction() != null)
            gameObject.onDestroyedAction().run();
        gameRoot().getChildren().remove(gameObject.node());
        if (gameObject.rigidBody() != null)
            dynamicsWorld.removeCollisionObject(gameObject.rigidBody());
        
        gameObjects().remove(gameObject);
    }
    
    public final void add(GameObject3D gameObject)
    {
        if (gameObjectExist(gameObject))
            return;
        ADDED_GAME_OBJECTS.add(gameObject);  
    } // end method
    
    public final void destroy(GameObject3D gameObject)
    {
        //gameObject.destroyRigidBody();
        //gameRoot.getChildren().remove(gameObject.node());
        DESTROYED_GAME_OBJECTS.add(gameObject);
    } // end method
    
    public final boolean gameObjectExist(GameObject3D gameObject)
    {
        return gameObjects.contains(gameObject);
    }
    
    @Override
    public void handle(long now) 
    {
            delta = (now - lastTime) / 1_000_000_000f;

            lastTime = now;
            
            if (player != null)
                player.update(delta);
            
            dynamicsWorld.stepSimulation(delta, 10);
            
            //contact = contactQueue.poll();
            //uncontact = uncontactQueue.poll();
            telegram = telegramDispatcher.poll();

            gameObjects().stream()
                    .filter(this::filtre)
                    .filter(go -> !go.toDestroyed)
                    .forEach(gameObject -> {
                        
                        // update
                        gameObject.update(delta);
                        // action
                        var action = gameObject.action();
                        if (action != null)
                            action.run();

                        var body = gameObject.rigidBody();
                        
                        // telegram
                        if (telegram != null && gameObject == telegram.receiver)
                            telegram.message.handleMessage(
                                gameObject, telegram.userData);
                        else if (telegram != null && telegram.receiver == null && 
                            gameObject != telegram.sender)
                        {
                            telegram.message.handleMessage(
                                gameObject, telegram.userData);
                        } // end 
                    });

            if (currentGameWorld != null)
                currentGameWorld.update();
            
            if (soundManager != null)
                soundManager.update();


            if (!ADDED_GAME_OBJECTS.isEmpty())
            {
                this.addObject(ADDED_GAME_OBJECTS.poll());
                //ADDED_GAME_OBJECTS.forEach(this::addObject);
                //ADDED_GAME_OBJECTS.clear();
            } // end if

            if (!DESTROYED_GAME_OBJECTS.isEmpty())
            {
                /*var it = DESTROYED_GAME_OBJECTS.iterator();
                while (it.hasNext())
                {
                    var gameObject = it.next();
                    destroyObject(gameObject);
                    it.remove();
                } // end while//*/
                destroyObject(DESTROYED_GAME_OBJECTS.poll());
                
            } // end if
            
            if (destroyCurrentWorld)
            {
                if (currentGameWorld == null)
                {
                    currentGameWorld = nextWorld;
                    nextWorld = null;
                    currentGameWorld.load();
                    currentGameWorld.toStartPosition();
                } // end if
                else
                {
                    gameObjects.stream()
                            .filter(e -> e != null)
                            .filter(e -> e != player.gameObject())
                            .forEach(e -> this.destroyObject(e));
                    
                    currentGameWorld.destroy();
                    currentGameWorld = nextWorld;
                    nextWorld = null;
                    currentGameWorld.load();
                    currentGameWorld.toStartPosition();
                } // end else
                
                destroyCurrentWorld = false;
            }
    } // end method*/
    
    public void addForce(GameObject3D gameObject, Vector3f force,
            Vector3f point)
    {
        forces.add(new Force(gameObject, force, point));
    }

    public void playGame()
    {
        if (lastTime == Long.MIN_VALUE)
            lastTime = System.nanoTime();
        start();
        gameState = GameManager3D.GameState.RUNNING;
    }
    
    public void pauseGame()
    {
        stop();
        gameState = GameManager3D.GameState.PAUSED;
    }

    public StackPane globalRoot() {
        return globalRoot;
    }

    public StackPane uiRoot() {
        return uiRoot;
    }

    public Group gameRoot() {
        return gameRoot;
    }

    public Scene scene() {
        return scene;
    }

    public SubScene gameScene() {
        return gameScene;
    }

    public Camera camera() {
        return camera;
    }

    protected final void setDynamicWorld(DynamicsWorld world)
    {
        this.dynamicsWorld = world;
    }
    
    public DynamicsWorld dynamicsWorld()
    {
        return dynamicsWorld;
    }

    public List<GameObject3D> gameObjects() 
    {
        return gameObjects;
    }

    public GamePlayer3D player() 
    {
        return player;
    }

    public void setPlayer(GamePlayer3D player) 
    {
        this.player = player;
    }

    public void send(Telegram3D telegram) 
    {
        telegramDispatcher.add(telegram);
    }
    
    public void send(Telegram3D telegram, final long time) 
    {
        if (time <= 0)
            telegramDispatcher.add(telegram);
        else
        {
            Task<Void> dispatcher = new Task<>() {
                @Override
                protected Void call() throws Exception 
                {
                    Thread.sleep(time);
                    telegramDispatcher.add(telegram);
                    return null;
                }
            };
            executor.submit(dispatcher);
        } // end if
    }

    public void bindCamera(Node node)
    {
        camera.translateXProperty().unbind();
        camera.translateYProperty().unbind();
        camera.translateZProperty().unbind();
        
        camera.translateXProperty().bind(node.translateXProperty());
        camera.translateYProperty().bind(node.translateYProperty());
        camera.translateZProperty().bind(node.translateZProperty().subtract(500));
    }
    
    public void bindCamera(Node node, double zFar)
    {
        camera.translateXProperty().unbind();
        camera.translateYProperty().unbind();
        camera.translateZProperty().unbind();
        
        camera.translateXProperty().bind(node.translateXProperty());
        camera.translateYProperty().bind(node.translateYProperty());
        camera.translateZProperty().bind(node.translateZProperty().subtract(zFar));
    }
    
    public void bindCamera(Node node, DoubleProperty width, DoubleProperty height)
    {
        camera.translateXProperty().unbind();
        camera.translateYProperty().unbind();
        camera.translateZProperty().unbind();
        
        camera.translateXProperty().bind(node.translateXProperty().add(width.divide(2)));
        camera.translateYProperty().bind(node.translateYProperty().add(height.divide(2)));
        camera.translateZProperty().bind(node.translateZProperty().subtract(500));
    }
    
    public void bindCamera(Node node, DoubleProperty width, DoubleProperty height, double zFar)
    {
        camera.translateXProperty().unbind();
        camera.translateYProperty().unbind();
        camera.translateZProperty().unbind();
        
        camera.translateXProperty().bind(node.translateXProperty().add(width.divide(2)));
        camera.translateYProperty().bind(node.translateYProperty().add(height.divide(2)));
        camera.translateZProperty().bind(node.translateZProperty().subtract(zFar));
    }
    
    public final void unbindCamera()
    {
        camera.translateXProperty().unbind();
        camera.translateYProperty().unbind();
        camera.translateZProperty().unbind();
    } // end method
    
    public final void unbindCamera(double x, double y, double z)
    {
        camera.translateXProperty().unbind();
        camera.translateYProperty().unbind();
        camera.translateZProperty().unbind();
        
        camera.setTranslateX(x);
        camera.setTranslateY(y);
        camera.setTranslateZ(z);
    } // end method
    
    public final void bindGameObject(GameObject3D gameObject1, 
        GameObject3D gameObject2, double xOffset,double yOffset, double zOffset)
    {
        var node1 = gameObject1.node();
        var node2 = gameObject2.node();
        
        unbindGameObject(gameObject1);
        
        node1.translateXProperty().bind(node2.translateXProperty().add(xOffset));
        node1.translateYProperty().bind(node2.translateYProperty().add(yOffset));
        node1.translateZProperty().bind(node2.translateZProperty().add(zOffset));
    }
    
    public final void unbindGameObject(GameObject3D gameObject1)
    {
        var node1 = gameObject1.node();
        
        node1.translateXProperty().unbind();
        node1.translateYProperty().unbind();
        node1.translateZProperty().unbind();
    }
    
    public final void bindNodes(Node node1, 
        Node node2, double xOffset,double yOffset, double zOffset)
    {
        unbindNode(node1);
        
        node1.translateXProperty().bind(node2.translateXProperty().add(xOffset));
        node1.translateYProperty().bind(node2.translateYProperty().add(yOffset));
        node1.translateZProperty().bind(node2.translateZProperty().add(zOffset));
    }
    
    public final void unbindNode(Node node)
    {
        node.translateXProperty().unbind();
        node.translateYProperty().unbind();
        node.translateZProperty().unbind();
    }
    
    private boolean filtre(GameObject3D gameObject)
    {
        //Objects.nonNull(gameObject);
        var fsm = gameObject.fsm();
        var body = gameObject.rigidBody();
        return gameObject.isAlwaysManaged() || (body != null && !body.isStaticObject()) || fsm.currentState() != null ||
                fsm.globalState() != null;
    }
    
    public final void loadGameWorld(GameWorld3D gameWorld)
    {
        /*if (currentGameWorld != null)
            currentGameWorld.destroy();
        currentGameWorld = gameWorld;
        gameWorld.load();
        gameWorld.toStartPosition();*/
        destroyCurrentWorld = true;
        nextWorld = gameWorld;
    }
    
    public final void addDebugText()
    {
        Text t = new Text();
        t.setId("debugText");
        t.setFill(Color.BLACK);
        t.setStroke(Color.ORANGE);
        t.setStrokeWidth(1);
        t.setFont(Font.font("mono", 24));
        uiRoot().getChildren().add(t);
        StackPane.setAlignment(t, Pos.TOP_LEFT);
    }
    
    public final AmbientLight getAmbientLight()
    {
        return ambientLight;
    } //
    
    public GameManager3D.GameState gameState()
    {
        return gameState;
    }
    
    public final boolean hasPlayer()
    {
        return player != null;
    }

    public SoundManager soundManager()
    {
        return soundManager;
    }
    
    public void setSoundManager(SoundManager soundManager)
    {
        this.soundManager = soundManager;
    }
    
    public final void setGameStartIntro(GameStartInto gameStartInto)
    {
        this.gameStartIntro = gameStartInto;
    }

    public GameStartInto gameStartIntro() 
    {
        return gameStartIntro;
    }
    
    
}
