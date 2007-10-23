package org.genyris.core;

import java.util.HashMap;
import java.util.Map;
import org.lispin.jlispin.interp.LispinException;

public class SymbolTable {
	
	// TODO refactor and push these 'optimisations' into the user classes. 
	// ie use lookup() instead of a static, except in using classes.
    private Map _table;
    private Lsymbol NIL;
    
    public SymbolTable() {
        _table = new HashMap();
    }
    
    public void init(Lsymbol nil) {
    	NIL = nil;
        _table.put("nil", NIL);
    }

    public Lsymbol internString(String newSym) {
        if (_table.containsKey(newSym)) {
            return (Lsymbol)_table.get(newSym);
        } else {
            Lsymbol sym = new Lsymbol(newSym);
            _table.put(newSym, sym);
            return sym;
        }
    }

    public void intern(Exp newSym) throws Exception {
        if (_table.containsKey(((Lsymbol)newSym).getPrintName())) {
            throw new LispinException("Can't intern symbol - already exists.");
        } else {
            _table.put(((Lsymbol)newSym).getPrintName(), newSym);
        }
    }
    
    public Exp getNil() {
    	return NIL;
    }
}
