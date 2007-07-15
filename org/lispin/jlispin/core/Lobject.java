package org.lispin.jlispin.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.Evaluator;
import org.lispin.jlispin.interp.LispinException;
import org.lispin.jlispin.interp.SpecialEnvironment;
import org.lispin.jlispin.interp.UnboundException;


public class Lobject extends Exp implements Environment {
	private Map _dict;
	
	public Lobject() {
		_dict = new HashMap();
	}
	
	public Lobject(Lsymbol key, Exp value) {
		_dict = new HashMap();
		_dict.put(key, value);
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
		if( symbol == SymbolTable._self ) {
			return this;
		}
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

	public Exp getClasses() {
		return (Exp)_dict.get(SymbolTable.classes);
	}

	public void addClass(Exp klass) {
		Exp classes = SymbolTable.NIL;
		if( _dict.containsKey(SymbolTable.classes) ) {
			classes = (Exp)_dict.get(SymbolTable.classes);
		}
		_dict.put(SymbolTable.classes, new Lcons (klass, classes));
	}


	public void removeClass(Exp klass) {
		Exp classes = SymbolTable.NIL;
		if( _dict.containsKey(SymbolTable.classes) ) {
			classes = (Exp)_dict.get(SymbolTable.classes);
		}
		try {
			_dict.put(SymbolTable.classes, removeIf (klass, classes));
		}
		catch (AccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	private Exp removeIf(Exp exp, Exp list) throws AccessException {
		if( list == SymbolTable.NIL) {
			return SymbolTable.NIL;
		}
		if( list == exp) {
			return removeIf(exp, list.cdr());
		} else {
			return new Lcons(list.car(), removeIf(exp, list.cdr()) );
		}
	}

	public Exp applyFunction(Environment environment, Exp[] arguments) throws LispinException {
		Map bindings = new HashMap();
		bindings.put(SymbolTable.self, this);
		SpecialEnvironment newEnv = new SpecialEnvironment(environment, bindings, this); 
		return Evaluator.evalSequence(newEnv, arguments[0]);
	}


}
