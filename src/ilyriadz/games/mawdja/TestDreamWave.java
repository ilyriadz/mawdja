
package ilyriadz.games.mawdja;

import ilyriadz.games.mawdja.core.GameManager;
import ilyriadz.games.mawdja.core.GamePlayer;
import ilyriadz.games.mawdja.core.RectObject;
import ilyriadz.games.mawdja.core.CircleObject;
import ilyriadz.games.mawdja.core.KeyboardGamePlayer;
import ilyriadz.games.mawdja.core.Sprite;
import ilyriadz.games.mawdja.test.DefaultGameWorld;
import static ilyriadz.games.mawdja.gutil.Gutil.thirtyBase;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.LightBase;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;

/**
 *
 * @author kiradja
 */
public class TestDreamWave extends Application
{
    private final static GameManager gameManager;
    CircleObject player;
    
    StringProperty message = new SimpleStringProperty(this, "message", "message");
   
    static
    {
        Screen s = Screen.getPrimary();
        gameManager = new GameManager(s.getBounds().getWidth(), 
            s.getBounds().getHeight());
    }

    @Override
    public void init() throws Exception 
    {
        gameManager.uiRoot().setMouseTransparent(true);
        Text t = new Text();
        t.setId("text");
        //t.textProperty().bind(message);
        t.setFill(Color.WHITE);
        t.setStrokeWidth(1);
        t.setStroke(Color.BLACK);
        message.set("message");
        t.setFont(Font.font("mono", 24));
        gameManager.uiRoot().getChildren().add(t);
        StackPane.setAlignment(t, Pos.TOP_LEFT);
        
        player = new CircleObject(gameManager, 50, 0, 0);
        player.setObjectName("Doramon");
        player.create();
        //rect.body().getFixtureList().setDensity(0.1f);
        player.body().setType(BodyType.DYNAMIC);
        //player.fsm().changeState(new DefaultState());
        gameManager.add(player);
        player.body().setLinearDamping(20);
        player.body().setFixedRotation(true);
        
        gameManager.setPlayer(new KeyboardGamePlayer(player));
        
        gameManager.bindCamera(player.node());
        
        LightBase light = new PointLight(Color.WHITE);
        gameManager.gameRoot().getChildren().add(light);
        light.setTranslateZ(-80);
        light.translateXProperty().bind(player.node().translateXProperty());
        light.translateYProperty().bind(player.node().translateYProperty());
        
        Sprite sp = new Sprite(player);
        sp.addAsset("right", List.of(new Image("/ilyriadz/games/dreamerwave/core/right.png"),
            new Image("/ilyriadz/games/dreamerwave/core/right2.png")), GameManager.STEP_TIME * 7);
        sp.addAsset("left", List.of(new Image("/ilyriadz/games/dreamerwave/core/left.png"),
            new Image("/ilyriadz/games/dreamerwave/core/left2.png")), GameManager.STEP_TIME * 7);
        sp.addAsset("stay", List.of(new Image("/ilyriadz/games/dreamerwave/core/doramon.png")), 0);
        sp.setAsset("stay");
        player.setSprite(sp);   
        
        var pl = gameManager.player();
        pl.setRightAssets("right", true);
        pl.setLeftAssets("left", true);
        pl.setRestAssets("stay", false);
        
        
        
        /*var path = PathUtil.fromSvg(PathUtil.class.getResourceAsStream("svgtest.svg"));
        var body = PathUtil.createPathPhysic(gameManager(), path, 250, 450);
        var image = new Image(PathUtil.class.getResource("svgtest.png").toExternalForm());
        Box box = new Box(image.getWidth(), image.getHeight(), 0);

        PhongMaterial pm = new PhongMaterial();
        pm.setDiffuseMap(image);
        box.setMaterial(pm);
        box.setTranslateX(250 + box.getWidth() / 2);
        box.setTranslateY(450 + box.getHeight() / 2);
        gameManager.gameRoot().getChildren().add(box);*/
        
        
    }
    
    
    @Override
    public void start(Stage primaryStage) throws Exception 
    {
        gameManager.scene().setOnMouseClicked(mc ->
        {
           var sceneWidthDiv2 = gameManager.scene().getWidth() / 2;
           var sceneHightDiv2 = gameManager.scene().getHeight() / 2;
           var cameraX = gameManager.camera().getTranslateX();
           var cameraY = gameManager.camera().getTranslateY();
           var zFarDiv = thirtyBase(125, 8, gameManager.camera().getTranslateZ());
           
           var mouseScenePosX = mc.getSceneX() - sceneWidthDiv2;
           var mouseScenePosY = mc.getSceneY() - sceneHightDiv2;
           
           message.set(String.format("%.2f, %.2f | %.2f, %.2f | %.2f | %.2f", 
                mouseScenePosX, mouseScenePosY, 
                cameraX, cameraY, gameManager.camera().getTranslateZ(),
                zFarDiv));
           
           RectObject obj = new RectObject(gameManager, 
                   mouseScenePosX  / 5.333333333333333 + cameraX,
                   mouseScenePosY / 5.333333333333333 + cameraY , 50, 50);
           gameManager.add(obj);
        });
        
        gameManager.scene().setOnKeyPressed(kp ->
        {
            gameManager.player().onKeyPressed(kp);
            
        });
        
        gameManager.scene().setOnKeyReleased(kr ->
        {
            gameManager.player().onKeyReleased(kr);
            
            if (kr.getCode() == KeyCode.ESCAPE)
                Platform.exit();

        });
        
        primaryStage.setScene(gameManager.scene());
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitKeyCombination(
            KeyCombination.keyCombination("ctrl+alt+enter"));
        //primaryStage.setFullScreenExitHint(null);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("Test GameManager2D");
        primaryStage.setOnShown(e ->
        {
            gameManager.playGame();
            var gw = new DefaultGameWorld(gameManager);
            gameManager.loadGameWorld(gw);         
        });
        primaryStage.show();
    }
    
    public static GameManager gameManager()
    {
        return gameManager;
    }
    
    public static World world()
    {
        return gameManager.world();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
