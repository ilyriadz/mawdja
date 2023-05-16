
package ilyriadz.games.mawdja.core;

import ilyriadz.games.mawdja.contact.GameContactListener;
import ilyriadz.games.mawdja.core.media.SoundManager;
import ilyriadz.games.mawdja.message.Telegram;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
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
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

/**
 *
 * @author kiradja
 */
public class GameManager extends AnimationTimer
{
    public final static float STEP_TIME = 1f / 60f;
    private final StackPane globalRoot = new StackPane();
    private final StackPane uiRoot = new StackPane();
    private final Group gameRoot = new Group();
    private final Scene scene;
    private final SubScene gameScene;
    private final Camera camera = new PerspectiveCamera(true);
    private World world = new World(new Vec2());
    private final List<GameObject> gameObjects = new ArrayList<>();
    private final Queue<Contact> contactQueue = new ArrayDeque<>();
    private final Queue<Contact> uncontactQueue = new ArrayDeque<>();
    private final Queue<Telegram> telegramDispatcher = new ArrayDeque<>();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    protected final Queue<GameObject> ADDED_GAME_OBJECTS = 
        new ArrayDeque<>();
    protected final Queue<GameObject> DESTROYED_GAME_OBJECTS = 
        new ArrayDeque<>();

    private GamePlayer player;
    
    private GameWorld currentGameWorld = null;
    private final AmbientLight ambientLight = new AmbientLight(Color.WHITE);
    
    //private final Timeline timeline = new Timeline();
    //private final KeyFrame keyFrame;
    
    private long lastTime = Long.MIN_VALUE;
    
    private Contact contact, uncontact;
    private Telegram telegram;
    
    public enum GameState {RUNNING, STOPPED, PAUSED}
    
    private GameState gameState = GameState.STOPPED;
    
    boolean worldDestoroyed = false;
    GameWorld nextWorld = null;
    
    private boolean destroyCurrentWorld = false;
    
    private SoundManager soundManager;

    public GameManager(double SceneWidth, double sceneHeight) 
    {
        gameRoot.setDepthTest(DepthTest.ENABLE);
        gameScene = new SubScene(gameRoot, SceneWidth, sceneHeight, 
            true, SceneAntialiasing.BALANCED);
        gameScene.setFill(Color.BLACK);
        gameScene.setDepthTest(DepthTest.ENABLE);
        globalRoot.getChildren().addAll(gameScene, uiRoot);
        
        //uiRoot.setMouseTransparent(true);
        
        scene = new Scene(globalRoot, SceneWidth, sceneHeight, true, 
            SceneAntialiasing.BALANCED);
        
        gameScene.widthProperty().bind(scene.widthProperty());
        gameScene.heightProperty().bind(scene.heightProperty());
        gameScene.setCamera(camera);
        gameScene.setManaged(false);
        
        camera.setTranslateZ(-500);
        camera.setNearClip(0.01);
        camera.setFarClip(100000);
        
        world.setContactListener(
            new GameContactListener(contactQueue, uncontactQueue));
        
        gameRoot.getChildren().add(ambientLight);
        
       /* keyFrame = new KeyFrame(Duration.seconds(STEP_TIME), e ->
        {
            currentTime = System.nanoTime();
            delta = (currentTime - lastTime) / 1_000_000_000.0f;
            lastTime = currentTime;
            
            accumulator += Math.min(delta, 0.25f);
            
            if (accumulator < STEP_TIME)
                return;
            
            accumulator -= STEP_TIME;
            
            //System.out.println(delta);
            
            contact = contactQueue.poll();
            uncontact = uncontactQueue.poll();
            telegram = telegramDispatcher.poll();

            world.step(STEP_TIME, 8, 3);
            gameObjects().stream()
                    .filter(this::filtre)
                    .forEach(gameObject -> {
                        // update
                        gameObject.update(STEP_TIME);
                        // action
                        var action = gameObject.action();
                        if (action != null)
                            action.run();

                        var body = gameObject.body();
                        // contact
                        if (contact != null && (contact.m_fixtureB.m_body == body
                                || contact.m_fixtureA.m_body == body))
                            gameObject.fsm().reaction(gameObject, contact);
                        if (uncontact != null && (uncontact.m_fixtureB.m_body == body
                                || uncontact.m_fixtureA.m_body == body))
                            gameObject.fsm().unreaction(gameObject, uncontact);
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

            if (player != null)
                player.update(STEP_TIME);

            if (currentGameWorld != null)
                currentGameWorld.update();


            if (!ADDED_GAME_OBJECTS.isEmpty())
            {
                ADDED_GAME_OBJECTS.forEach(this::addObject);
                ADDED_GAME_OBJECTS.clear();
            } // end if

            if (!DESTROYED_GAME_OBJECTS.isEmpty())
            {
                var it = DESTROYED_GAME_OBJECTS.iterator();
                while (it.hasNext())
                {
                    var gameObject = it.next();
                    destroyObject(gameObject);
                    it.remove();
                } // end while
            } // end if//
        });
        
        
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Timeline.INDEFINITE);*/
    }

    protected void addObject(GameObject gameObject) 
    {
        gameRoot().getChildren().add(gameObject.node());
        gameObjects().add(gameObject);
        gameObject.node().setUserData(gameObject);
    }

    protected void destroyObject(GameObject gameObject) 
    {
        // before destroying
        if (gameObject.onDestroyedAction() != null)
            gameObject.onDestroyedAction().run();
        gameRoot().getChildren().remove(gameObject.node());
        if (gameObject.body() != null)
            world().destroyBody(gameObject.body());
        gameObjects.remove(gameObject);
    }
    
    public final void add(GameObject gameObject)
    {
        if (gameObjectExist(gameObject))
            return;
        ADDED_GAME_OBJECTS.add(gameObject);  
    } // end method
    
    public final void destroy(GameObject gameObject)
    {
        //gameObject.destroyBody();
        //gameRoot.getChildren().remove(gameObject.node());
        DESTROYED_GAME_OBJECTS.add(gameObject);
    } // end method
    
    public final boolean gameObjectExist(GameObject gameObject)
    {
        return gameObjects.contains(gameObject);
    }
    
    @Override
    public void handle(long now) 
    {

            long elapsedTime = now - lastTime;
            float delta = (float) (elapsedTime / 1_000_000_000.0);
            
            
            world.step(delta, 8, 3);
            
            contact = contactQueue.poll();
            uncontact = uncontactQueue.poll();
            telegram = telegramDispatcher.poll();

            gameObjects().stream().parallel()
                    .filter(this::filtre)
                    .forEach(gameObject -> {
                        
                        if (gameObject.toDestroyed)
                            return;
                        
                        // update
                        gameObject.update(delta);
                        // action
                        var action = gameObject.action();
                        if (action != null)
                            action.run();

                        var body = gameObject.body();
                        // contact
                        if (contact != null && (contact.m_fixtureB.m_body == body
                                || contact.m_fixtureA.m_body == body))
                            gameObject.fsm().reaction(gameObject, contact);
                        if (uncontact != null && (uncontact.m_fixtureB.m_body == body
                                || uncontact.m_fixtureA.m_body == body))
                            gameObject.fsm().unreaction(gameObject, uncontact);
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

            if (player != null)
                player.update(delta);

            if (currentGameWorld != null)
                currentGameWorld.update();
            
            if (soundManager != null)
                soundManager.update();


            if (!ADDED_GAME_OBJECTS.isEmpty())
            {
                //this.addObject(ADDED_GAME_OBJECTS.poll());
                ADDED_GAME_OBJECTS.forEach(this::addObject);
                ADDED_GAME_OBJECTS.clear();
            } // end if

            if (!DESTROYED_GAME_OBJECTS.isEmpty())
            {
                var it = DESTROYED_GAME_OBJECTS.iterator();
                while (it.hasNext())
                {
                    var gameObject = it.next();
                    destroyObject(gameObject);
                    it.remove();
                } // end while//*/
                //destroyObject(DESTROYED_GAME_OBJECTS.poll());
                
            } // end if
            
            if (destroyCurrentWorld)
            {
                if (currentGameWorld == null)
                {
                    currentGameWorld = nextWorld;
                    nextWorld = null;
                    System.err.println("????????????????");
                    System.out.println(nextWorld);
                    currentGameWorld.load();
                    currentGameWorld.toStartPosition();
                } // end if
                else
                {
                    var bd = world.getBodyList();
                    while (bd != null)
                    {
                        if (player == null || player.gameObject() == null
                                || player.gameObject().body() != bd)
                            world.destroyBody(bd);
                        
                        bd = bd.getNext();
                    } // end while 
                    
                    var it = gameObjects.iterator();
                    
                    while (it.hasNext())
                    {
                        var gameObject = it.next();
                        if (gameObject != player.gameObject())
                        {
                            gameRoot.getChildren().remove(gameObject.node());
                            it.remove();
                        } // end if
                    }//*/
                    
                    currentGameWorld.destroy();
                    currentGameWorld = nextWorld;
                    nextWorld = null;
                    currentGameWorld.load();
                    currentGameWorld.toStartPosition();
                    
                    System.out.println(gameObjects);
                } // end else
                
                destroyCurrentWorld = false;
            }
            
            lastTime = now;
            
            
    } // end method*/

    public void playGame()
    {
        if (lastTime == Long.MIN_VALUE)
            lastTime = System.nanoTime();
        start();
        gameState = GameState.RUNNING;
    }
    
    public void pauseGame()
    {
        stop();
        gameState = GameState.PAUSED;
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

    protected final void setWorld(World world)
    {
        this.world = world;
    }
    
    public World world()
    {
        return world;
    }

    public List<GameObject> gameObjects() 
    {
        return gameObjects;
    }

    public GamePlayer player() 
    {
        return player;
    }

    public void setPlayer(GamePlayer player) 
    {
        this.player = player;
    }

    public void send(Telegram telegram) 
    {
        telegramDispatcher.add(telegram);
    }
    
    public void send(Telegram telegram, final long time) 
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
    
    public final void bindGameObject(GameObject gameObject1, 
        GameObject gameObject2, double xOffset,double yOffset, double zOffset)
    {
        var node1 = gameObject1.node();
        var node2 = gameObject2.node();
        
        unbindGameObject(gameObject1);
        
        node1.translateXProperty().bind(node2.translateXProperty().add(xOffset));
        node1.translateYProperty().bind(node2.translateYProperty().add(yOffset));
        node1.translateZProperty().bind(node2.translateZProperty().add(zOffset));
    }
    
    public final void unbindGameObject(GameObject gameObject1)
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
    
    private boolean filtre(GameObject gameObject)
    {
        Objects.nonNull(gameObject);
        var fsm = gameObject.fsm();
        var body = gameObject.body();
        return (body != null && body.getType() != BodyType.STATIC) || fsm.currentState() != null ||
                fsm.globalState() != null;
    }
    
    public final void loadGameWorld(GameWorld gameWorld)
    {
        /*if (currentGameWorld != null)
            currentGameWorld.destroy();
        currentGameWorld = gameWorld;
        gameWorld.load();
        gameWorld.toStartPosition();*/
        nextWorld = gameWorld;
        destroyCurrentWorld = true;
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
    
    public GameState gameState()
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
} // end class
