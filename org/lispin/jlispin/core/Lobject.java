package org.lispin.jlispin.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.UnboundException;


public class Lobject implements Environment {
	private Map _dict;
	
	public Lobject() {
		_dict = new HashMap();
	}
	
	public int hashCode() {
    	return _dict.hashCode();
    }

	public boolean equals(Object compare) {
		if( compare.getClass() != this.getClass())
			return false;
		else 
			return _dict.equals(((Lobject)compare)._dict);
	}

	public Object getJavaValue() {
		return _dict;
	}

	public void acceptVisitor(Visitor guest) {
		guest.visitLobject(this);
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
		} 
		if( _dict.containsKey(SymbolTable.classes) ) {
			try {
				return lookupInClasses(symbol);
			} catch (UnboundException e) {}
		}
		if(_dict.containsKey(SymbolTable.superclasses)) {
			return lookupInSuperClasses(symbol);				
		}
		throw new UnboundException("unbound " + symbol.toString());
	}

	private Exp lookupInClasses(Exp symbol) throws UnboundException {
		if (!_dict.containsKey(SymbolTable.classes)) {
			throw new UnboundException("object has no classes");
		}
		Exp classes = (Exp)_dict.get(SymbolTable.classes);
		while( classes != SymbolTable.NIL) {
			try {
				Environment klass = (Environment)(classes.car());
				try {
					return (Exp)klass.lookupInThisClassAndSuperClasses(symbol);
				} catch (UnboundException e) {
					;
				} finally {
					classes = classes.cdr();
				}
			}
			catch (AccessException e) {
				throw new UnboundException("bad classes list in object");
			}
		}
		throw new UnboundException("dict does not contain key: " + symbol.toString());
	}

	public Exp lookupInThisClassAndSuperClasses(Exp symbol) throws UnboundException {
		if( _dict.containsKey(symbol) ) {
			return (Exp)_dict.get(symbol);
		} else {
			return lookupInSuperClasses(symbol);
		}
	}

	private Exp lookupInSuperClasses(Exp symbol) throws UnboundException {
		if( !_dict.containsKey(SymbolTable.superclasses) ) {
			throw new UnboundException("object has no superclasses");
		}
		Exp superclasses = (Exp)_dict.get(SymbolTable.superclasses);
		while( superclasses != SymbolTable.NIL) {
			try {
				Environment klass = (Environment)(superclasses.car());
				try {
					return (Exp)klass.lookupInThisClassAndSuperClasses(symbol);
				} catch (UnboundException e) {
					;
				} finally {
					superclasses = superclasses.cdr();
				}
			}
			catch (AccessException e) {
				throw new UnboundException("bad classes list in object");
			}
		}
		throw new UnboundException("dict does not contain key: " + symbol.toString());
	}

	public void setVariableValue(Exp symbol, Exp valu) throws UnboundException {
		if( _dict.containsKey(symbol)) {
			_dict.put(symbol, valu);
		}
		else {
			throw new UnboundException("in object, undefined variable: " + ((Lsymbol)symbol).getPrintName()); // TODO downcast
		}
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
