// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;
import org.genyris.interp.Evaluator;
import org.genyris.interp.SpecialEnvironment;
import org.genyris.interp.UnboundException;
import org.genyris.interp.builtin.TagFunction;

public class Dictionary extends ExpWithEmbeddedClasses implements Environment {
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

	public Dictionary(Symbol key, Exp value, Environment parent) {
		_dict = mapFactory();
		_dict.put(key, value);
		_parent = parent;
	}

	public Environment getParent() {
		return _parent;
	}

	public void acceptVisitor(Visitor guest) throws GenyrisException {
		guest.visitDictionary(this);
	}

	public boolean isSelfEvaluating() {
		return true;
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
			Exp key = (Exp) iter.next();
			Exp value = (Exp) _dict.get(key);
			Exp tmp = new PairWithcolons(key, value);
			result = new Pair(tmp, result);
		}
		return new Pair(_parent.getSymbolTable().DICT(), result);
	}

	public void defineVariableRaw(Exp exp, Exp valu) throws GenyrisException {

		if (!(exp instanceof Symbol)) {
			throw new GenyrisException("cannot define non-symbol: "
					+ exp.toString());
		}
		Exp sym = exp;
		if (sym == CLASSES()) {
			setClasses(valu, _parent.getNil());
			return;
		} else {
			_dict.put(sym, valu);
			return;
		}
	}

	public void defineVariable(Exp exp, Exp valu) throws GenyrisException {
		if (Symbol.isDynamic(exp, DYNAMIC())) {
			defineVariableRaw(Symbol.realSymbol(exp, DYNAMIC()), valu);
			return;
		}
		throw new GenyrisException(
				"cannot define non-dynamic symbol in object: " + exp.toString());
	}

	public Exp lookupVariableValue(Exp symbol) throws UnboundException {

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
			return lookupInClasses(symbol);
		} catch (UnboundException ignore) {
		}

		if (_dict.containsKey(SUPERCLASSES())) {
			return lookupInSuperClasses(symbol);
		}
		throw new UnboundException("unbound " + symbol.toString());
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

	private Symbol DYNAMIC() {
		return _parent.getSymbolTable().DYNAMIC_SYMBOL();
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

	private Exp lookupInClasses(Exp symbol) throws UnboundException {
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
		throw new UnboundException("dict does not contain key: "
				+ symbol.toString());
	}

	public Exp lookupInThisClassAndSuperClasses(Exp symbol)
			throws UnboundException {
		if (_dict.containsKey(symbol)) {
			return (Exp) _dict.get(symbol);
		} else {
			return lookupInSuperClasses(symbol);
		}
	}

	private Exp lookupInSuperClasses(Exp symbol) throws UnboundException {
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
		throw new UnboundException("dict does not contain key: "
				+ symbol.toString());
	}

	public void setVariableValue(Exp symbol, Exp valu) throws UnboundException {
		Symbol sym = Symbol.realSymbol(symbol, DYNAMIC());
		if (sym == CLASSES()) {
			try {
				setClasses(valu, _parent.getNil());
			} catch (AccessException ignore) {
			}
		} else {
			if (_dict.containsKey(sym)) {
				_dict.put(sym, valu);
			} else {
				throw new UnboundException("in object, undefined variable: "
						+ symbol);
			}
		}
	}

	public String toString() {
		return "<dict " + asAlist().toString() + ">";
	}

	public Exp lookupVariableShallow(Exp symbol) throws UnboundException {
		if (symbol == CLASSES()) {
			return getClasses(_parent);
		} else if (_dict.containsKey(symbol)) {
			return (Exp) _dict.get(symbol);
		} else {
			throw new UnboundException("dict does not contain key: "
					+ symbol.toString());
		}
	}

	public Exp applyFunction(Environment environment, Exp[] arguments)
			throws GenyrisException {
		if (arguments[0].isNil()) {
			return this;
		}
		Map bindings = mapFactory();
		bindings.put(SELF(), this);
		SpecialEnvironment newEnv = new SpecialEnvironment(environment,
				bindings, this);
		if (arguments[0].listp()) {
			return Evaluator.evalSequence(newEnv, arguments[0]);
		} else {
			try {
				Dictionary klass = (Dictionary) Evaluator.eval(newEnv, arguments[0]);
				TagFunction.validateObjectInClass(environment, this, klass);
				return this;
			} catch (ClassCastException e) {
				throw new GenyrisException("type tag failure: " + arguments[0]
						+ " is not a class");
			}
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

	public Exp lookupDynamicVariableValue(Exp symbol) throws UnboundException {
		return lookupVariableValue(symbol);
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

}
