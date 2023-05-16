/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ilyriadz.games.mawdja.menu;

import ilyriadz.games.mawdja.core.xyz.GameManager3D;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 *
 * @author kiradja
 */
public class GameStartInto 
{
    private final StackPane stackPane;
    
    private final List<ImageView> developersLogosList = new ArrayList<>();
    private final List<MediaPlayer> developersMedias = new ArrayList<>();
    private final List<Duration> devDuration = new ArrayList<>();
    
    private final List<ImageView> gameToolsLogosList = new ArrayList<>();
    private final List<MediaPlayer> gameToolsMedias = new ArrayList<>();
    private final List<Duration> gameToolDuration = new ArrayList<>();
    
    private final List<ImageView> splashScreenImagesList = new ArrayList<>();
    private final List<MediaPlayer> splashScreenMedias = new ArrayList<>();
    private final List<Duration> splashDuration = new ArrayList<>();
    
    private final SequentialTransition developerSequentialTransition = 
        new SequentialTransition();
    private SequentialTransition gameToolsSequentialTransition =
        new SequentialTransition();
    private final SequentialTransition splashScreenSequentialTransition =
        new SequentialTransition();
    
    /*private double developersDefaultDuration   = 3;
    private double gameToolsDefaultDuration    = 3;
    private double splashScreenDefaultDuration = 5;*/
    
    /*private double developersDefaultDelay = 0;
    private double gameToolsDefaultDelay = 0;
    private double splashScreenDefaultDelay = 0;//*/
    
    //private double sequentialDelay = 0;
    
    private Runnable onFinish = null;
    

    public GameStartInto(StackPane stackPane) 
    {
        this.stackPane = stackPane;
    }
    
    private void add(List<ImageView> imgLst, List<MediaPlayer> mpLst,
        List<Duration> drLst, Image image, Media media, double duration)
    {
        var imgv = new ImageView(image);
        imgv.setPreserveRatio(true);
        imgv.fitWidthProperty().bind(stackPane.getScene().widthProperty().divide(2));
        imgv.fitHeightProperty().bind(stackPane.getScene().heightProperty().divide(2));
        imgLst.add(imgv);
        if (media != null)
            mpLst.add(new MediaPlayer(media));
        else
            mpLst.add(null);
        
        drLst.add(Duration.seconds(duration));
    }
    
    public final void addDeveloperLogo(Image image, Media media, double duration)
    {
        add(developersLogosList, developersMedias, devDuration, image, media, 
            duration);
    }
    
    public final void addToolLogo(Image image, Media media, double duration)
    {
        add(gameToolsLogosList, gameToolsMedias, gameToolDuration, image, media, 
            duration);
    }
    
    public final void addSplashScreenImage(Image image, Media media, double duration)
    {
        add(splashScreenImagesList, splashScreenMedias, splashDuration, 
             image, media, duration);
    }
    
    public final void play()
    {
        
        for (int i = 0; i < developersLogosList.size(); i++) 
        {
            var dls = developersLogosList.get(i);
            dls.setOpacity(0);
            stackPane.getChildren().add(dls);
            FadeTransition fadeTransition = new FadeTransition();
            fadeTransition.setCycleCount(2);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);
            fadeTransition.setAutoReverse(true);
            fadeTransition.setNode(dls);
            
            //final var mediaplayer = developersMedias.get(i);
            fadeTransition.setDuration(/*mediaplayer == null ?*/
                devDuration.get(i)); //: 
                //mediaplayer.getMedia().getDuration());
            
            /*if (fadeTransition.getDuration().toSeconds() == 0)
                fadeTransition.setDuration(Duration.seconds(
                    developersDefaultDuration));//*/
            
            final int ii = i;
            if (i < developersLogosList.size() - 1)
                fadeTransition.setOnFinished(e ->
                {
                    var mp = developersMedias.get(ii + 1);
                    if (mp != null)
                        mp.play();
                });
            
            developerSequentialTransition.getChildren().add(fadeTransition);
        } // end for dev
        
        //developerSequentialTransition.setDelay(Duration.seconds(developersDefaultDelay));
        developerSequentialTransition.setOnFinished(e ->
        {
            var mp = gameToolsMedias.get(0);
            if (mp != null)
                mp.play();
            gameToolsSequentialTransition.play();
        });
        
        for (int i = 0; i < gameToolsLogosList.size(); i++) 
        {
            var dls = gameToolsLogosList.get(i);
            dls.setOpacity(0);
            stackPane.getChildren().add(dls);
            FadeTransition fadeTransition = new FadeTransition();
            fadeTransition.setCycleCount(2);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);
            fadeTransition.setAutoReverse(true);
            fadeTransition.setNode(dls);
            
            //final var mediaplayer = gameToolsMedias.get(i);
            fadeTransition.setDuration(//mediaplayer == null ? //
                gameToolDuration.get(i)); // : 
                //mediaplayer.getMedia().getDuration());
            
            final int ii = i;
            if (i < gameToolsLogosList.size() - 1)
                fadeTransition.setOnFinished(e ->
                {
                    var mp = gameToolsMedias.get(ii + 1);
                    if (mp != null)
                        mp.play();
                });
            
            gameToolsSequentialTransition.getChildren().add(fadeTransition);
        } // end for game
        
        //gameToolsSequentialTransition.setDelay(Duration.seconds(gameToolsDefaultDelay));
        gameToolsSequentialTransition.setOnFinished(e ->
        {          
            var mp = splashScreenMedias.get(0);
            if (mp != null)
                mp.play();
            splashScreenSequentialTransition.play();
        });
        
        for (int i = 0; i < splashScreenImagesList.size(); i++) 
        {
            var dls = splashScreenImagesList.get(i);
            dls.setOpacity(0);
            stackPane.getChildren().add(dls);
            FadeTransition fadeTransition = new FadeTransition();
            fadeTransition.setCycleCount(2);
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);
            fadeTransition.setAutoReverse(true);
            fadeTransition.setNode(dls);
            
            //final var mediaplayer = splashScreenMedias.get(i);
            fadeTransition.setDuration(//mediaplayer == null ? 
                splashDuration.get(i)); // : 
                //mediaplayer.getMedia().getDuration());
            
            final int ii = i;
            if (i < splashScreenImagesList.size() - 1)
                fadeTransition.setOnFinished(e ->
                {
                    var mp = splashScreenMedias.get(ii + 1);
                    if (mp != null)
                        mp.play();
                });
            
            splashScreenSequentialTransition.getChildren().add(fadeTransition);
        } // end for splash
        
        //splashScreenSequentialTransition.setDelay(Duration.seconds(splashScreenDefaultDelay));
        splashScreenSequentialTransition.setOnFinished(e ->
        {
            if (onFinish != null)
                onFinish.run();
        });
        
        var mp = developersMedias.get(0);
        
        if (mp != null)
            mp.play();
        
        developerSequentialTransition.play();
        
        
    }

    /*public double developersDefaultDuration() {
        return developersDefaultDuration;
    }

    public void setDevelopersDefaultDuration(double developersDefaultDuration) {
        this.developersDefaultDuration = developersDefaultDuration;
    }

    public double gameToolsDefaultDuration() {
        return gameToolsDefaultDuration;
    }

    public void setGameToolsDefaultDuration(double gameToolsDefaultDuration) {
        this.gameToolsDefaultDuration = gameToolsDefaultDuration;
    }

    public double splashScreenDefaultDuration() {
        return splashScreenDefaultDuration;
    }

    public void setSplashScreenDefaultDuration(double splashScreenDefaultDuration) {
        this.splashScreenDefaultDuration = splashScreenDefaultDuration;
    }//*/

    public void setOnFinish(Runnable onFinish) {
        this.onFinish = onFinish;
    }

    public Runnable onFinish() {
        return onFinish;
    }

    /*public double developersDefaultDelay() {
        return developersDefaultDelay;
    }

    public void setDevelopersDefaultDelay(double developersDefaultDelay) {
        this.developersDefaultDelay = developersDefaultDelay;
    }

    public double gameToolsDefaultDelay() {
        return gameToolsDefaultDelay;
    }

    public void setGameToolsDefaultDelay(double gameToolsDefaultDelay) {
        this.gameToolsDefaultDelay = gameToolsDefaultDelay;
    }

    public double splashScreenDefaultDelay() {
        return splashScreenDefaultDelay;
    }

    public void setSplashScreenDefaultDelay(double splashScreenDefaultDelay) {
        this.splashScreenDefaultDelay = splashScreenDefaultDelay;
    } 

    public double sequentialDelay() {
        return sequentialDelay;
    }

    public void setSequentialDelay(double sequentialDelay) {
        this.sequentialDelay = sequentialDelay;
    }//*/
    
}
