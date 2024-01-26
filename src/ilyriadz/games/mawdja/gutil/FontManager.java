/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ilyriadz.games.mawdja.gutil;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 *
 * @author kiradja
 */
public class FontManager 
{
    private final static Map<String, String> fontMap = new HashMap<>();
    
    public static void addFont(String name, String fontUrl)
    {   
        if (!fontMap.containsKey(name))
            fontMap.put(name, Font.loadFont(fontUrl, 0).getFamily());
    }
    
    public static Font getFont(String name, double size)
    {
        if (!fontMap.containsKey(name))
            throw new IllegalArgumentException("font for name '" + name + "' not exists");
        
        return Font.font(fontMap.get(name), size);
    }
    
    public static Font getFont(String name, FontWeight fontWeight, double size)
    {
        if (!fontMap.containsKey(name))
            throw new IllegalArgumentException("font for name '" + name + "' not exists");
        
        return Font.font(fontMap.get(name), fontWeight, size);
    }
    
    public static Font getFont(String name, FontPosture fontPosture, double size)
    {
        if (!fontMap.containsKey(name))
            throw new IllegalArgumentException("font for name '" + name + "' not exists");
        
        return Font.font(fontMap.get(name), fontPosture, size);
    }
    
    public static Font getFont(String name, FontWeight fontWeight, FontPosture fontPosture, double size)
    {
        if (!fontMap.containsKey(name))
            throw new IllegalArgumentException("font for name '" + name + "' not exists");
        
        return Font.font(
            fontMap.get(name), fontWeight, fontPosture, size);
    }
}
