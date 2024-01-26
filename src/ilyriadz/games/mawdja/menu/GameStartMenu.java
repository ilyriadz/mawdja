/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ilyriadz.games.mawdja.menu;

import ilyriadz.games.mawdja.gutil.BindingResourceBundle;
import ilyriadz.games.mawdja.gutil.FontManager;
import ilyriadz.games.mawdja.gutil.SaveFile;
import java.util.Objects;
import javafx.scene.layout.StackPane;

/**
 *
 * @author kiradja
 */
public class GameStartMenu 
{
    private BindingResourceBundle bindingBundle;
    private SaveFile saveFile;
    private final StackPane stackPane = new StackPane();

    public GameStartMenu() 
    {
    }
    
    public BindingResourceBundle bindingBundle() 
    {
        return bindingBundle;
    }

    public void setBindingBundle(BindingResourceBundle bindingBundle)
    {
        this.bindingBundle = Objects.requireNonNull(bindingBundle,
            "pass a null BindingBundle as argument");
    }   

    public SaveFile saveFile() 
    {
        return saveFile;
    }

    public void setSaveFile(SaveFile saveFile)
    {
        this.saveFile = Objects.requireNonNull(saveFile,
            "pass a null SaveFile as argument");
    }
 
    public StackPane stackPane() 
    {
        return stackPane;
    }
    
    
}
