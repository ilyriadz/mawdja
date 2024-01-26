/*
 * Combinableo change this license header, choose License Headers in Project Properties.
 * Combinableo change this template file, choose Combinableools | Combinableemplates
 * and open the template in the editor.
 */
package ilyriadz.games.mawdja.core;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import javafx.scene.paint.Color;

/**
 *
 * @author user
 */
public class CombineValidator
{
    private final Set<Class<? extends Combineable>> combineSet;
    private final Set<Combineable> validateSet = new HashSet<>();
    
    private Consumer<Combineable> onAdd;
    private Consumer<Combineable> onClear;
    
    private final Runnable onValidatedAction;

    @SuppressWarnings("unchecked")
    public CombineValidator(Runnable onValidatedAction, Class<? extends Combineable>... classes) 
    {
        Objects.nonNull(onValidatedAction);
        Objects.nonNull(classes);
        
        if (classes.length < 1)
            throw new IllegalArgumentException("combine class must be >= 1");
        
        this.combineSet = Set.of(classes);
        this.onValidatedAction = onValidatedAction;
    }

    public final Set<Class<? extends Combineable>> combineSet() 
    {
        return combineSet;
    }

    public Set<Combineable> validateSet() {
        return validateSet;
    }
    
    public final void clear()
    {
        var it = validateSet.iterator();
        
        while (it.hasNext())
        {
            var gameObject = it.next();
            if (onClear != null)
                onClear.accept(gameObject);
            it.remove();
        } // end while
    }

    public final  Runnable onValidatedAction() {
        return onValidatedAction;
    }
    
    public final void add(Combineable combinable) throws CombineTypeNotPresentException
    {
        Objects.nonNull(combinable);
        
        if (combineSet.contains(combinable.getClass()))
        {
            if (!validateSet.contains(combinable))
            {
                validateSet.add(combinable);
                
                if (onAdd != null)
                    onAdd.accept(combinable);
            } // end if
        }
        else
            throw new CombineTypeNotPresentException(
                combinable.getClass() + " not in " + combineSet);
    }
    
    public final void validate()
    {
        if (isValide())
            onValidatedAction.run();
    }
    
    private boolean isValide()
    {
        boolean valide = true && (validateSet.size() == combineSet.size());
        
        for (var combinable : validateSet) 
        {
            if (!combineSet.contains(combinable.getClass()))
            {
                valide = false;
                break;
            }
        }
                        
        return valide;               
    }
    
    public void setOnAdd(Consumer<Combineable> onAdd) {
        this.onAdd = onAdd;
    }

    public void setOnClearing(Consumer<Combineable> onClear) {
        this.onClear = onClear;
    }
    
    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws CombineTypeNotPresentException 
    {
        GameManager gm = null;
        
        CombineManager cm = new CombineManager();
        
        
        CombineValidator cv = new CombineValidator(() -> {
            System.out.println("fired");
        }, CircleObject.class, RectObject.class);
        
        CombineValidator cv2 = new CombineValidator(() -> {
            System.out.println("fired2");
        }, LightObject.class, GhostObject.class);
        
        CircleObject c = new CircleObject(gm, 0, 0, 0);
        c.setCombineValidator(cv);
        
        RectObject r = new RectObject(gm, 0, 0, 0, 0);
        r.setCombineValidator(cv);
        
        LightObject l = new LightObject(0, 0, Color.ALICEBLUE);
        l.setCombineValidator(cv2);
        GhostObject g = new GhostObject(0, 0, 0, 0);
        g.setCombineValidator(cv2);
        
        cm.add(c);
        cm.add(l);
        cm.add(g);
        cm.add(c);
        cm.add(r);
    }
        
}
