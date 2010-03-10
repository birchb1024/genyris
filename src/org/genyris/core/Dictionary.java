// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.DynamicEnvironment;
import org.genyris.interp.Environment;
import org.genyris.interp.UnboundException;

public class Dictionary extends Atom implements Environment {
	private Map _dict;

	protected Environment _parent;

    private static Map mapFactory() {
    	return new HashMap();
    }

	public Dictionary() {
		_dict = mapFactory();
		_parent = null;
	}

	public Dictionary(Environment parent) {
		_dict = mapFactory();
		_parent = parent;
	}

	public Dictionary(SimpleSymbol key, Exp value, Environment parent) {
		_dict = mapFactory();
		_dict.put(key, value);
		_parent = parent;
	}

	public Dictionary(Environment parent, final HashMap map) {
		_dict = new HashMap(map);
		_parent = parent;
	}

	public Environment getParent() {
		return _parent;
	}

	public void acceptVisitor(Visitor guest) throws GenyrisException {
		guest.visitDictionary(this);
	}

	public boolean hasKey(Exp a) {
		return _dict.containsKey(a);
	}

	public Exp asAlist() {
		Map keys = new TreeMap(_dict);
		Iterator iter = keys.keySet().iterator();
		// TODO Sort the keyset to get a consistent result for test cases.
		Exp result = _parent.getNil();
		while (iter.hasNext()) {
			Exp key = (Exp)iter.next();
			Exp value = (Exp)  _dict.get(key);
			Exp tmp = new PairEquals( new DynamicSymbol((SimpleSymbol)key), value);
			result = new Pair(tmp, result);
		}
		return new Pair(_parent.getSymbolTable().DICT(), result);
	}

	public void defineVariableRaw(SimpleSymbol sym, Exp valu) throws GenyrisException {
		if (sym == CLASSES()) {
			setClasses(valu, _parent.getNil());
			return;
		} else {
			_dict.put(sym, valu);
			return;
		}
	}

	public void defineDynamicVariable(DynamicSymbol sym, Exp valu) throws GenyrisException {
		defineVariableRaw(sym.getRealSymbol(), valu);
	}

	private Exp CLASSES() {
		return _parent.getSymbolTable().CLASSES();
	}

	private Exp SELF() {
		return _parent.getSymbolTable().SELF();
	}

	private Exp SUPERCLASSES() {
		return _parent.getSymbolTable().SUPERCLASSES();
	}

	private Exp VARS() {
		return _parent.getSymbolTable().VARS();
	}

	private Exp getVarsList() {
		Iterator iter = _dict.keySet().iterator();
		Exp result = new Pair(VARS(), _parent.getNil());
		while (iter.hasNext()) {
			Exp key = (Exp) iter.next();
			result = new Pair(key, result);
		}
		return result;
	}

	private Exp lookupInClasses(DynamicSymbol symbol) throws UnboundException {
		Exp classes = getClasses(_parent);
		while (classes != _parent.getNil()) {
			try {
				if(!(classes.car() instanceof Environment)) {
					throw new UnboundException("damaged class in lass list: " + classes.car());
				}
				Environment klass = (Environment) (classes.car());
				try {
					return (Exp) klass.lookupInThisClassAndSuperClasses(symbol);
				} catch (UnboundException e) {
					;
				} finally {
					classes = classes.cdr();
				}
			} catch (AccessException e) {
				throw new UnboundException("bad classes list in object");
			}
		}
		throw new UnboundException("key " + symbol.toString() + 
				" not found in dict: " + this);
	}

	public Exp lookupInThisClassAndSuperClasses(DynamicSymbol symbol)
			throws UnboundException {
		if (_dict.containsKey(symbol.getRealSymbol())) {
			return (Exp) _dict.get(symbol.getRealSymbol());
		} else {
			return lookupInSuperClasses(symbol);
		}
	}

	private Exp lookupInSuperClasses(DynamicSymbol symbol) throws UnboundException {
		if (!_dict.containsKey(SUPERCLASSES())) {
			throw new UnboundException("object has no superclasses");
		}
		Exp superclasses = (Exp) _dict.get(SUPERCLASSES());
		while (superclasses != _parent.getNil()) {
			try {
				Environment klass = (Environment) (superclasses.car());
				try {
					return (Exp) klass.lookupInThisClassAndSuperClasses(symbol);
				} catch (UnboundException e) {
					;
				} finally {
					superclasses = superclasses.cdr();
				}
			} catch (AccessException e) {
				throw new UnboundException("bad classes list in object");
			}
		}
		throw new UnboundException("key " + symbol.toString() + 
				" not found in dict: " + this);
	}

	public void setVariableValue(Symbol symbol, Exp valu) throws UnboundException {
		symbol.setVariableValue(this, valu);
	}
	public void setDynamicVariableValueRaw(SimpleSymbol sym, Exp valu) throws UnboundException {
		if (sym == CLASSES()) {
			try {
				setClasses(valu, _parent.getNil());
			} catch (AccessException ignore) {
			}
		} else {
			if (_dict.containsKey(sym)) {
				_dict.put(sym, valu);
			} else {
				throw new UnboundException("in object, undefined variable: " + sym);
			}
		}
	}
	public void setDynamicVariableValue(DynamicSymbol symbol, Exp valu) throws UnboundException {
		setDynamicVariableValueRaw(symbol.getRealSymbol(), valu);
	}
	
	public void setLexicalVariableValue(SimpleSymbol symbol, Exp valu) throws UnboundException {
		throw new UnboundException("Dictionary expected dynamic symbol ref " 	+ symbol);
	}

	public String toString() {
		return "<dict " + asAlist().toString() + ">";
	}

	public Exp lookupVariableShallow(SimpleSymbol symbol) throws UnboundException {
		if (symbol == CLASSES()) {
			return getClasses(_parent);
		} else if (_dict.containsKey(symbol)) {
			return (Exp) _dict.get(symbol);
		} else {
			throw new UnboundException("key " + symbol.toString() + 
					" not found in dict: " + this);
		}
	}

	public SimpleSymbol getNil() {
		return _parent.getNil();
	}

	public Symbol internString(String symbolName) {
		return _parent.internString(symbolName);
	}

	public Symbol getBuiltinClassSymbol(Internable table) {
		return table.DICTIONARY();
	}

	public Exp getSelf() {
		return this;
	}
	public Internable getSymbolTable() {
		return _parent.getSymbolTable();
	}

	public Exp eval(Environment env) throws GenyrisException {
		return this;
	}

	public Exp lookupVariableValue(Symbol symbol) throws UnboundException {
		try {
			return symbol.lookupVariableValue(this);
		} catch (UnboundException e) {
			throw new UnboundException("variable " + symbol + " unbound in " + this);
		}
	}
	public Exp lookupDynamicVariableValue(DynamicSymbol dsymbol) throws UnboundException {
		Symbol symbol = ((DynamicSymbol)dsymbol).getRealSymbol();

		if (symbol == SELF()) {
			return this;
		} else if (symbol == CLASSES()) {
			return getClasses(_parent);
		} else if (symbol == VARS()) {
			return getVarsList();
		} else if (_dict.containsKey(symbol)) {
			return (Exp) _dict.get(symbol);
		}
		try {
			return lookupInClasses(dsymbol);
		} catch (UnboundException ignore) {
		}

		if (_dict.containsKey(SUPERCLASSES())) {
			return lookupInSuperClasses(dsymbol);
		}
		throw new UnboundException("unbound " + symbol.toString());
	}

	public Exp lookupLexicalVariableValue(SimpleSymbol symbol) throws UnboundException {
		throw new UnboundException("no lexical symbols in Dictionary: " + this);
	}

	public void defineLexicalVariable(SimpleSymbol symbol, Exp valu) throws GenyrisException {
		throw new UnboundException("no lexical symbols in Dictionary: " + symbol);		
	}

	public void defineVariable(Symbol symbol, Exp valu) throws GenyrisException {
			symbol.defineVariable(this, valu);
	}
	public Environment  makeEnvironment(Environment parent) throws GenyrisException {
		Map bindings = mapFactory();
		bindings.put(SELF(), this);
		return new DynamicEnvironment(parent, bindings, this);
	}
	public Dictionary addProperty(Environment env, String name, Exp value) throws GenyrisException {
    	defineDynamicVariable(new DynamicSymbol((SimpleSymbol) env.internString(name)), 
    			value);
		return this;
	}

	public void defineInt(String name, int i) throws GenyrisException {
		defineDynamicVariable(new DynamicSymbol((SimpleSymbol) internString(name)), new Bignum(i));	
	}

	public void defineString(String name, String string2) throws GenyrisException {
		defineDynamicVariable(new DynamicSymbol((SimpleSymbol) internString(name)), new StrinG(string2));	
	}
	public void defineSymbol(String name, String string2) throws GenyrisException {
		defineDynamicVariable(new DynamicSymbol((SimpleSymbol) internString(name)), internString(string2));	
	}
}
