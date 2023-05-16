/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ilyriadz.games.mawdja.core;

import java.util.Objects;

/**
 *
 * @author user
 */
public class CombineManager
{
    private CombineValidator combineValidator;

    public CombineManager(CombineValidator combineValidator) 
    {
        Objects.nonNull(combineValidator);
        this.combineValidator = combineValidator;
    }

    public CombineManager() {
    }
    
    public final void clear()
    {
        if (combineValidator != null)
            combineValidator.clear();
    }
    
    private void setCombineValidator(CombineValidator combineValidator)
    {
        this.combineValidator = combineValidator;
        this.combineValidator.clear();
    }
    
    public final void add(Combineable combinable)
        throws CombineTypeNotPresentException
    {
        var combineValidator0 = combinable.combineValidator();
        
        Objects.nonNull(combinable);
        Objects.nonNull(combineValidator0);

        if (combineValidator0 != this.combineValidator)
        {
            if (this.combineValidator != null)
                this.combineValidator.clear();
            setCombineValidator(combineValidator0);     
        } // end if
        
        combineValidator0.add(combinable);
        
        combineValidator0.validate();   
    }  
}
