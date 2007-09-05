package org.lispin.jlispin.core;

import java.util.HashMap;
import java.util.Map;
import org.lispin.jlispin.interp.LispinException;

public class SymbolTable {
	
	// TODO refactor and push these 'optimisations' into the user classes. 
	// ie use lookup() instead of a static, except in using classes.
	public static final String DYNAMICSCOPECHAR = "_";
    private Map _table;
    private Lsymbol NIL;
    
    public static Lsymbol lambda;
    public static Lsymbol lambdaq;
    public static Lsymbol lambdam;
    public static Lsymbol method;
    public Lsymbol REST;
    public static Lsymbol DICT;
	public static Lsymbol _validate;

    public SymbolTable() {
        _table = new HashMap();
    }
    
    public void init(Lsymbol nil) {
    	NIL = nil;
        _table.put("nil", NIL);
        lambda = internString("lambda");
        lambdaq = internString("lambdaq");
        lambdam = internString("lambdam");
        method = internString("method");
        
        REST = internString("&rest");
        DICT = internString("dict");
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
