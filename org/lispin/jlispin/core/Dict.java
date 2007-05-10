package org.lispin.jlispin.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Dict extends Exp {
	private Map _dict;
	
	public Dict() {
		_dict = new HashMap();
	}
	
	public int hashCode() {
    	return _dict.hashCode();
    }

	public boolean equals(Object compare) {
		if( compare.getClass() != this.getClass())
			return false;
		else 
			return _dict.equals(((Dict)compare)._dict);
	}

	public Object getJavaValue() {
		return _dict;
	}

	public void acceptVisitor(Visitor guest) {
		guest.visitDict(this);
	}
		
	public boolean isSelfEvaluating() {
		return false;
	}
	public boolean hasKey(Exp a) {
		return _dict.containsKey(a);
	}
	public void add(Exp key, Exp value) {
		_dict.put(key, value);
		
	}

	public Exp getAlist() {
		Iterator iter = _dict.keySet().iterator();
		Exp result = SymbolTable.NIL;
		while(iter.hasNext()) {
			Exp key = (Exp) iter.next();
			Exp value = (Exp) _dict.get(key);
			result = new Lcons( new Lcons(key, new Lcons(value, SymbolTable.NIL)), result);
		}
		return result;
	}
}
