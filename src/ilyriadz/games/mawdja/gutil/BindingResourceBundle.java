/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ilyriadz.games.mawdja.gutil;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author kiradja
 */
public final class BindingResourceBundle 
{
    private final Map<String, StringProperty> bindingMap = new HashMap<>();
    private ResourceBundle bundle;
    private final String baseName;
    
    public BindingResourceBundle(String baseName, Locale locale) 
    {
        this.bundle = ResourceBundle.getBundle(baseName, locale);
        this.baseName = baseName;
    }
    
    public void bind(String propertyName, StringProperty stringProperty)
    {
        StringProperty bindProperty = 
            new SimpleStringProperty(bundle.getString(propertyName));
        stringProperty.bind(bindProperty);
        bindingMap.put(propertyName, bindProperty);
    }
    
    public StringProperty getStringProperty(String name)
    {
        Objects.requireNonNull(name, "pass a null argument");
        
        if (bindingMap.containsKey(name))
            return bindingMap.get(name);
        else
            throw new IllegalArgumentException("key '" + name + "' not exist");
    }
    
    public void setLocal(Locale locale)
    {
        if (locale == null)
            throw new IllegalArgumentException("locale is null");
        
        this.bundle = ResourceBundle.getBundle(baseName, locale);
        bindingMap.keySet().forEach(k ->
        {
            bindingMap.get(k).set(bundle.getString(k));
        });
    }   
    
    public final Locale locale()
    {
        return bundle.getLocale();
    }
}
