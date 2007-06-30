package org.lispin.jlispin.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.UnboundException;


public class Dict extends Environment {
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

	public void defineVariable(Exp symbol, Exp valu) {
		_dict.put(symbol, valu);		
	}

	public Exp lookupVariableValue(Exp symbol) throws UnboundException {
		if( _dict.containsKey(symbol) ) {
			return (Exp)_dict.get(symbol);
		} else { 
			if( _dict.containsKey(SymbolTable.classes) ) {
				return lookupInClasses(symbol);
			} 
			else
				throw new UnboundException("dict does not contain key: " + symbol.toString());
		}
	}

	private Exp lookupInClasses(Exp symbol) throws UnboundException {
		Exp classes = (Exp)_dict.get(SymbolTable.classes);
		while( classes != SymbolTable.NIL) {
			try {
				Environment klass = (Environment)(classes.car());
				if( klass.boundp(symbol) ) {
					return (Exp)klass.lookupVariableShallow(symbol);
				} else {
					classes = classes.cdr();
				}
			}
			catch (AccessException e) {
				throw new UnboundException("bad classes list in object");
			}
		}
		throw new UnboundException("dict does not contain key: " + symbol.toString());
	}

	public void setVariableValue(Exp symbol, Exp valu) throws UnboundException {
		_dict.put(symbol, valu);
	}

	public String toString() {
		return "<dict "+ getAlist().toString() +">";
	}

	public Exp lookupVariableShallow(Exp symbol) throws UnboundException {
		if( _dict.containsKey(symbol) ) {
			return (Exp)_dict.get(symbol);
		} else { 
			throw new UnboundException("dict does not contain key: " + symbol.toString());
		}
	}


}
