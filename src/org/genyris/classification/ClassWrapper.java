// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.classification;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.genyris.core.Lobject;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.Visitor;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;
import org.genyris.interp.UnboundException;

public class ClassWrapper {
	private Lobject _theClass;

	private Exp CLASSNAME, SUPERCLASSES, SUBCLASSES;

	private SimpleSymbol NIL;

	public ClassWrapper(Lobject toWrap) {
		_theClass = toWrap;
		CLASSNAME = toWrap.internString(Constants.CLASSNAME);
		SUPERCLASSES = toWrap.internString(Constants.SUPERCLASSES);
		SUBCLASSES = toWrap.internString(Constants.SUBCLASSES);
		NIL = toWrap.getNil();
	}

	public Lobject getTheClass() {
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
			ClassWrapper klass = new ClassWrapper((Lobject) classes.car());
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

	public void addSuperClass(Lobject klass) throws GenyrisException {
		if (klass == null)
			return;

		Exp supers = _theClass.lookupVariableShallow(SUPERCLASSES);
		supers = new Lcons(klass, supers);
		_theClass.setVariableValue(SUPERCLASSES, supers);
		new ClassWrapper(klass).addSubClass(_theClass);
		// TODO use a list set adding function to avoid duplicates.
	}

	public void addSubClass(Lobject klass) throws UnboundException {
		if (klass == null)
			return;
		Exp subs = _theClass.lookupVariableShallow(SUBCLASSES);
		subs = new Lcons(klass, subs);
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

	public static Lobject makeClass(Environment env, Exp klassname,
			Exp superklasses) throws GenyrisException {
		Exp NIL = env.getNil();
		Exp standardClassSymbol = env.internString(Constants.STANDARDCLASS);
		Exp standardClass = env.lookupVariableValue(standardClassSymbol);
		Lobject newClass = new Lobject(env);
		newClass.addClass(standardClass);
		newClass.defineVariableRaw(env.internString(Constants.CLASSNAME),
				klassname);
		newClass.defineVariableRaw(env.internString(Constants.CLASSES),
				new Lcons(standardClass, NIL));
		newClass.defineVariableRaw(env.internString(Constants.SUBCLASSES), NIL);
		if (superklasses == NIL)
			superklasses = new Lcons(env.internString(Constants.THING), NIL);
		{
			newClass.defineVariableRaw(
					env.internString(Constants.SUPERCLASSES), lookupClasses(
							env, superklasses));
			Exp sklist = superklasses;
			while (sklist != NIL) {
				Lobject sk = (Lobject) (env.lookupVariableValue(sklist.car()));
				Exp subklasses = NIL;
				try {
					subklasses = sk.lookupVariableShallow(env
							.internString(Constants.SUBCLASSES));
				} catch (UnboundException ignore) {
					sk.defineVariable(env.internString(Constants.SUBCLASSES),
							NIL);
				}
				sk.setVariableValue(env.internString(Constants.SUBCLASSES),
						new Lcons(newClass, subklasses));
				sklist = sklist.cdr();
			}
		}
		env.defineVariable(klassname, newClass);
		return newClass;
	}

	private static Exp lookupClasses(Environment env, Exp superklasses)
			throws GenyrisException {
		Exp result = env.getNil();
		while (superklasses != env.getNil()) {
			result = new Lcons(env.lookupVariableValue(superklasses.car()),
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
			ClassWrapper mysubklass = new ClassWrapper((Lobject) firstClass); 
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
		if(! (firstClass instanceof Lobject)) {
			throw new GenyrisException(firstClass + "is not a class.");
		}
	}

	public boolean isInstance(Exp object) throws GenyrisException {
		Environment env = _theClass.getParent();
		Exp classes;

		classes = object.getClasses(env);
		while (classes != env.getNil()) {
			isThisObjectAClass(classes.car());
			ClassWrapper klass = new ClassWrapper((Lobject) classes.car()); 
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
