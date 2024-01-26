/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ilyriadz.games.mawdja.core.media;

import ilyriadz.games.mawdja.core.GameManager;
import ilyriadz.games.mawdja.gutil.Gutil;
import java.util.Objects;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.media.AudioClip;
import javafx.scene.media.MediaPlayer;

/**
 *
 * @author ilyriadz
 */
public sealed abstract class SoundObject permits AudioObject, MediaObject
{
    private double volume = 1.0;
    private double balance = 0.0;
    private double rate = 1.0;
    private int priority = 5;
    private double maxDistance = 100;
    private double delay = 0, delayCount = 0;
    private double repeatTimes = 0;
    
    private boolean autoManaged = false;
    
    private DoubleProperty translateX = 
        new SimpleDoubleProperty(this, "translateX", 0.0);
    private DoubleProperty translateY = 
        new SimpleDoubleProperty(this, "translateY", 0.0);
    private DoubleProperty translateZ = 
        new SimpleDoubleProperty(this, "translateZ", 0.0);
    
    private SoundFadeAxis soundFadeAxis = SoundFadeAxis.XYZ_AXIS;
    
    protected Runnable onMediaFinished = null;
    
    public static enum SoundFadeAxis 
    {
        X_AXIS, Y_AXIS, Z_AXIS, XY_AXIS, XYZ_AXIS;
    }

    public SoundObject() 
    {
    }
    
    protected abstract Object soundObject();
    
    public void play()
    {
        delayCount -= GameManager.STEP_TIME;
        
        if (delayCount > 0.0)
        {
            return;
        }
        else
        {
            if (repeatTimes > 0)
            {
                delayCount = delay;
                repeatTimes--;
            }
                
        }
        
        if (soundObject() instanceof AudioClip au)
            au.play(volume, balance, rate, balance, priority);
        else if (soundObject() instanceof MediaPlayer me)
        {
            me.setVolume(volume);
            me.setBalance(balance);
            me.setRate(rate);
            
            //if (me.getStatus() != MediaPlayer.Status.PLAYING)
            me.play();
        }
        
        delayCount = delay;
    }
    
    public void pause()
    {
        if (soundObject() instanceof MediaPlayer me)
        {
            if (me.getStatus() != MediaPlayer.Status.PAUSED)
                me.pause();
        }
    }
    
    public void stop()
    {
        if (soundObject() instanceof AudioClip au)
            au.stop();
        else if (soundObject() instanceof MediaPlayer me)
            me.stop();
    }
    
    public double volume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double balance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double rate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int priority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public double maxDistance()
    {
        return maxDistance;
    }
    
    public void setMaxDistance(double distance)
    {
        this.maxDistance = distance < 0 ? 0 : distance;
    }
    
    public void setAutoManaged(boolean managed)
    {
        autoManaged = managed;
    }

    public boolean isAutoManaged() 
    {
        return autoManaged;
    }
    
    public DoubleProperty translateXProperty()
    {
        return translateX;
    }
    
    public DoubleProperty translateYProperty()
    {
        return translateY;
    }
    
    public DoubleProperty translateZProperty()
    {
        return translateZ;
    }
    
    public double getTranslateX()
    {
        return translateX.doubleValue();
    }
    
    public double getTranslateY()
    {
        return translateY.doubleValue();
    }
    
    public double getTranslateZ()
    {
        return translateZ.doubleValue();
    }
    
    public void setTranslateX(double x)
    {
        translateX.set(x);
    }
    
    public void setTranslateY(double y)
    {
        translateY.set(y);
    }
    
    public void setTranslateZ(double z)
    {
        translateZ.set(z);
    }
    
    public SoundFadeAxis soundFadeAxis()
    {
        return soundFadeAxis;
    }
    
    public void setSoundFadeAxis(SoundFadeAxis axis)
    {
        Objects.nonNull(axis);
        soundFadeAxis = axis;
    }

    public void setOnMediaFinished(Runnable onMediaFinished) 
    {
        this.onMediaFinished = onMediaFinished;
    }

    public Runnable onMediaFinished() 
    {
        return onMediaFinished;
    }

    public void setDelay(double delay) {
        this.delay = delay;
        delayCount = delay;
    }

    public void setRepeatTimes(double repeatTimes) {
        this.repeatTimes = repeatTimes;
    }
    
    

    public double getDelay() {
        return delay;
    }

    public double getRepeatTimes() {
        return repeatTimes;
    }
    
    

    public void bind(Node node)
    {
        translateX.bind(node.translateXProperty());
        translateY.bind(node.translateYProperty());
        translateZ.bind(node.translateZProperty());
    }
    
    public void update(Node node)
    {
        double distance;
        
        double x = node.getTranslateX();
        double y = node.getTranslateY();
        double z = node.getTranslateZ();
        
        double tx = translateX.doubleValue();
        double ty = translateY.doubleValue();
        double tz = translateZ.doubleValue();
        
        double xx = x - tx;
        double yy = y - ty;
        double zz = z - tz;
        
        if (soundFadeAxis == SoundFadeAxis.X_AXIS)
            distance = StrictMath.sqrt(xx * xx);
        else if (soundFadeAxis == SoundFadeAxis.Y_AXIS)
            distance = StrictMath.sqrt(yy * yy);
        else if (soundFadeAxis == SoundFadeAxis.Z_AXIS)
            distance = StrictMath.sqrt(zz * zz);
        else if (soundFadeAxis == SoundFadeAxis.XY_AXIS)
            distance = StrictMath.sqrt(xx * xx + yy * yy);
        else 
            distance = StrictMath.sqrt(xx * xx + yy * yy + zz * zz);

        double relativeAngle = Gutil.relativeAngle(x, y, tx, ty);
        
        float volume0 = (float)Gutil.map(distance, 1, this.maxDistance, 1, 0);
        
        //volume0 = ((int)(volume0 * 100)) / 100f;
        
        //System.out.println(volume0);
        
        setBalance(StrictMath.cos(StrictMath.toRadians(relativeAngle))
            > 0 ? 0.25 : -0.25);
        setVolume(distance > this.maxDistance ? 0.0 : volume0);
        
        if (soundObject() instanceof MediaPlayer me)
        {
            me.setVolume(volume());
            me.setBalance(balance);
        }
        
    }
}
