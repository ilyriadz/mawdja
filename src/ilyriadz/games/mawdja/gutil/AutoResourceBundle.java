/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ilyriadz.games.mawdja.gutil;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.text.Font;

/**
 *
 * @author kiradja
 */
public class AutoResourceBundle 
{
    private final String bundleName;
    private ResourceBundle bundle;
    private Locale locale;
    private final Map<String, Object> objects = new HashMap<>();

    public AutoResourceBundle(String bundleName, Locale locale) 
    {
        this.bundleName = bundleName;
        Locale.setDefault(locale);
        this.locale = locale;
        bundle = ResourceBundle.getBundle(bundleName, locale);
    }
    
    public AutoResourceBundle(String bundleName, Locale locale, Module module) 
    {
        this.bundleName = bundleName;
        Locale.setDefault(locale);
        this.locale = locale;
        bundle = ResourceBundle.getBundle(bundleName, locale, module);
    }

    
    public AutoResourceBundle(String bundleName) 
    {
        this(bundleName, Locale.getDefault());
    }
    
    public String getString(String key)
    {
        return bundle.getString(key);
    }
    
    public void setLocale(Locale locale)
    {
        bundle = ResourceBundle.getBundle(bundleName, locale);
        objects.keySet().forEach(e ->
        {
            try {
                var node = objects.get(e);
                
                Class<?> clazz = node.getClass();
                
                if (!e.isEmpty())
                {
                    var setTextMethod = clazz.getMethod("setText", String.class);
                    setTextMethod.invoke(node, bundle.getString(e));
                }
                
                var setFontMethod = clazz.getMethod("setFont", Font.class);
                var getFontMethod = clazz.getMethod("getFont");
                
                Font font = (Font) getFontMethod.invoke(node);
                
                
                setFontMethod.invoke(node, 
                    FontManager.getFont(bundle.getString("font"), 
                        font.getSize()));
            } catch (NoSuchMethodException | SecurityException | 
                    IllegalAccessException | IllegalArgumentException | 
                    InvocationTargetException ex) {
                Logger.getLogger(AutoResourceBundle.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    public void addObject(Object node, String propertyName)
    {
        try {
            var clazz = node.getClass();
            if (!propertyName.isEmpty())
            {
                var method = clazz.getMethod("setText", String.class);

                if (!bundle.containsKey(propertyName))
                    throw new IllegalArgumentException("key '" + propertyName +
                            "' not exists");

                method.invoke(node, bundle.getString(propertyName));
            }
            objects.put(propertyName, node);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(AutoResourceBundle.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Node " + node + " haven't method setText(String)");
            
        } catch (SecurityException | IllegalAccessException | 
                IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(AutoResourceBundle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getBundleName() {
        return bundleName;
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public Locale locale() 
    {
        return locale;
    }
    
    
    
}
