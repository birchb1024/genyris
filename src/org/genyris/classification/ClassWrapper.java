// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.classification;

import org.genyris.classes.GlobalDescriptions;
import org.genyris.core.Dictionary;
import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.Symbol;
import org.genyris.core.Visitor;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;
import org.genyris.interp.UnboundException;

public class ClassWrapper {
	private Dictionary _theClass;

	private Exp CLASSNAME, SUPERCLASSES, SUBCLASSES;

	private SimpleSymbol NIL;

	public ClassWrapper(Dictionary toWrap) {
		_theClass = toWrap;
		CLASSNAME = toWrap.getSymbolTable().CLASSNAME();
		SUPERCLASSES = toWrap.getSymbolTable().SUPERCLASSES();
		SUBCLASSES = toWrap.getSymbolTable().SUBCLASSES();
		NIL = toWrap.getNil();
	}

	public Dictionary getTheClass() {
		return _theClass;
	}

	public void acceptVisitor(Visitor guest) throws GenyrisException {
		guest.visitClassWrapper(this);
	}

	public String toString() {
		String result = "<class ";
		result += getClassName();
		try {
			result += classListToString(getSuperClasses());
			result += classListToString(getSubClasses());
			result += ">";
		} catch (AccessException e) {
			return this.getClassName() + " toString():  " + e.getMessage();
		}
		return result;
	}

	private String classListToString(Exp classes) throws AccessException {
		String result = " (";
		while (classes != NIL) {
			ClassWrapper klass = new ClassWrapper((Dictionary) classes.car());
			result += klass.getClassName();
			if (classes.cdr() != NIL)
				result += ' ';
			classes = classes.cdr();
		}
		result += ")";
		return result;
	}

	private Exp getSubClasses() {
		try {
			return _theClass.lookupVariableShallow(SUBCLASSES);
		} catch (UnboundException e) {
			return NIL;
		}
	}

	public void addSuperClass(Dictionary klass) throws GenyrisException {
		if (klass == null)
			return;

		Exp supers = _theClass.lookupVariableShallow(SUPERCLASSES);
		supers = new Pair(klass, supers);
		_theClass.setVariableValue(SUPERCLASSES, supers);
		new ClassWrapper(klass).addSubClass(_theClass);
		// TODO use a list set adding function to avoid duplicates.
	}

	public void addSubClass(Dictionary klass) throws UnboundException {
		if (klass == null)
			return;
		Exp subs = _theClass.lookupVariableShallow(SUBCLASSES);
		subs = new Pair(klass, subs);
		_theClass.setVariableValue(SUBCLASSES, subs);
		// TODO use a list set adding function to avoid duplicate subclasses.
	}

	private Exp getSuperClasses() {
		try {
			return _theClass.lookupVariableShallow(SUPERCLASSES);
		} catch (UnboundException e) {
			return NIL;
		}
	}

	public String getClassName() {
		try {
			return _theClass.lookupVariableShallow(CLASSNAME).toString();
		} catch (UnboundException e) {
			return "anonymous";
		}
	}

	public static Dictionary makeClass(Environment env, Symbol klassname,
			Exp superklasses) throws GenyrisException {
		Exp NIL = env.getNil();
		Exp standardClassSymbol = env.getSymbolTable().STANDARDCLASS();
		Exp standardClass = env.lookupVariableValue(standardClassSymbol);
		Dictionary newClass = new Dictionary(env);
		newClass.addClass(standardClass);
		newClass.defineVariableRaw(env.getSymbolTable().CLASSNAME(),
				klassname);
		newClass.defineVariableRaw(env.getSymbolTable().CLASSES(),
				new Pair(standardClass, NIL));
		newClass.defineVariableRaw(env.getSymbolTable().SUBCLASSES(), NIL);
		if (superklasses == NIL)
			superklasses = new Pair(env.getSymbolTable().THING(), NIL);
		{
			newClass.defineVariableRaw(
					env.getSymbolTable().SUPERCLASSES(), lookupClasses(
							env, superklasses));
			Exp sklist = superklasses;
			while (sklist != NIL) {
				Dictionary sk = (Dictionary) (env.lookupVariableValue(sklist.car()));
				Exp subklasses = NIL;
				try {
					subklasses = sk.lookupVariableShallow(env.getSymbolTable().SUBCLASSES());
				} catch (UnboundException ignore) {
					sk.defineVariable(env.getSymbolTable().SUBCLASSES(),
							NIL);
				}
				sk.setVariableValue(env.getSymbolTable().SUBCLASSES(),
						new Pair(newClass, subklasses));
				sklist = sklist.cdr();
			}
		}
		env.defineVariable(klassname, newClass);
		GlobalDescriptions.updateClass(env, env.getSymbolTable(), klassname, superklasses);
		return newClass;
	}

	private static Exp lookupClasses(Environment env, Exp superklasses)
			throws GenyrisException {
		Exp result = env.getNil();
		while (superklasses != env.getNil()) {
			result = new Pair(env.lookupVariableValue(superklasses.car()),
					result);
			superklasses = superklasses.cdr();
		}
		return result;
	}

	public boolean isSubClass(ClassWrapper klass) throws GenyrisException {
		if (klass._theClass == this._theClass) {
			return true;
		}
		Environment env = _theClass.getParent();
		Exp mysubclasses = getSubClasses();
		// TODO - maybe if ClassWRapper could return an array of ClassWrappers
		// this would be tidy!
		while (mysubclasses != env.getNil()) {
			Exp firstClass = mysubclasses.car();
			isThisObjectAClass(firstClass);
			ClassWrapper mysubklass = new ClassWrapper((Dictionary) firstClass); 
			if (mysubklass._theClass == klass._theClass) {
				return true;
			} else if (mysubklass.isSubClass(klass)) {
				return true;
			}
			mysubclasses = mysubclasses.cdr();
		}
		return false;
	}

	static void  isThisObjectAClass(Exp firstClass) throws GenyrisException {
		// TODO improve this method to check is something is really a class.
		if(! (firstClass instanceof Dictionary)) {
			throw new GenyrisException(firstClass + "is not a class.");
		}
	}

	public boolean isInstance(Exp object) throws GenyrisException {
		Environment env = _theClass.getParent();
		Exp classes;

		classes = object.getClasses(env);
		while (classes != env.getNil()) {
			isThisObjectAClass(classes.car());
			ClassWrapper klass = new ClassWrapper((Dictionary) classes.car()); 
			if (classes.car() == _theClass) {
				return true;
			}
			if (isSubClass(klass)) {
				return true;
			}
			classes = classes.cdr();
		}
		return false;
	}
}
