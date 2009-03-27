package org.genyris.core;

import org.genyris.classes.GlobalDescriptions;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;
import org.genyris.interp.UnboundException;

public class StandardClass extends Dictionary {

	private SimpleSymbol CLASSNAME, SUPERCLASSES, SUBCLASSES;

	private SimpleSymbol NIL;

	public StandardClass(SimpleSymbol classname, SimpleSymbol symbolicName, Environment env) {
		super( classname,  symbolicName,  env);
		CLASSNAME = env.getSymbolTable().CLASSNAME();
		SUPERCLASSES = env.getSymbolTable().SUPERCLASSES();
		SUBCLASSES = env.getSymbolTable().SUBCLASSES();
		NIL = env.getNil();
	}

	public StandardClass(Environment env) {
		super(env);
		CLASSNAME = env.getSymbolTable().CLASSNAME();
		SUPERCLASSES = env.getSymbolTable().SUPERCLASSES();
		SUBCLASSES = env.getSymbolTable().SUBCLASSES();
		NIL = env.getNil();
	}

	public static StandardClass mkClass(String name, Environment env,
			StandardClass superClass) throws GenyrisException {
		Internable table = env.getSymbolTable();
		Symbol STANDARDCLASS = table.STANDARDCLASS();
		StandardClass standardClassDict = (StandardClass)env.lookupVariableValue(STANDARDCLASS);
		SimpleSymbol classname = table.CLASSNAME();
		SimpleSymbol symbolicName = table.internString(name);

		StandardClass newClass = makeTheClass(env, superClass, table,
				standardClassDict, classname, symbolicName);
		GlobalDescriptions.updateClassSingleSuper(env, table, symbolicName,
				(superClass!=null?(Symbol) superClass.lookupVariableShallow(classname):null));
		return newClass;
	}

	private static StandardClass makeTheClass(Environment env,
			StandardClass superClass, Internable table, StandardClass standardClassDict,
			SimpleSymbol classname, SimpleSymbol symbolicName) throws GenyrisException {
		StandardClass newClass = new StandardClass(classname, symbolicName, env);
		newClass.defineVariableRaw(table.SUPERCLASSES(), env.getNil());
		newClass.defineVariableRaw(table.SUBCLASSES(), env.getNil());
		newClass.addClass(standardClassDict);
		if (superClass != null)
			newClass.addSuperClass(superClass);
		env.defineVariable(symbolicName, newClass);
		return newClass;
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
			StandardClass klass = (StandardClass) classes.car();
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
			return lookupVariableShallow(SUBCLASSES);
		} catch (UnboundException e) {
			return NIL;
		}
	}

	public void addSuperClass(StandardClass klass) throws GenyrisException {
		if (klass == null)
			return;

		Exp supers = lookupVariableShallow(SUPERCLASSES);
		supers = new Pair(klass, supers);
		setDynamicVariableValueRaw(SUPERCLASSES, supers);
		klass.addSubClass(this);
		// TODO use a list set adding function to avoid duplicates.
	}

	public void addSubClass(StandardClass klass) throws UnboundException {
		if (klass == null)
			return;
		Exp subs = lookupVariableShallow(SUBCLASSES);
		subs = new Pair(klass, subs);
		setDynamicVariableValueRaw(SUBCLASSES, subs);
		// TODO use a list set adding function to avoid duplicate subclasses.
	}

	private Exp getSuperClasses() {
		try {
			return lookupVariableShallow(SUPERCLASSES);
		} catch (UnboundException e) {
			return NIL;
		}
	}

	public String getClassName() {
		try {
			return lookupVariableShallow(CLASSNAME).toString();
		} catch (UnboundException e) {
			return "anonymous";
		}
	}

	public static StandardClass makeClass(Environment env, Symbol klassname,
			Exp superklasses) throws GenyrisException {
		Exp NIL = env.getNil();
		Symbol standardClassSymbol = env.getSymbolTable().STANDARDCLASS();
		StandardClass standardClass = (StandardClass)env.lookupVariableValue(standardClassSymbol);
		StandardClass newClass = new StandardClass(env);
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
				StandardClass sk = (StandardClass) (env.lookupVariableValue((Symbol)sklist.car()));
				Exp subklasses = NIL;
				try {
					subklasses = sk.lookupVariableShallow(env.getSymbolTable().SUBCLASSES());
				} catch (UnboundException ignore) {
					sk.defineVariable(env.getSymbolTable().SUBCLASSES(),
							NIL);
				}
				sk.setDynamicVariableValueRaw(env.getSymbolTable().SUBCLASSES(),
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
			result = new Pair(env.lookupVariableValue((Symbol)superklasses.car()),
					result);
			superklasses = superklasses.cdr();
		}
		return result;
	}

	public boolean isSubClass(StandardClass klass) throws GenyrisException {
		if (klass  == this) {
			return true;
		}
		Environment env = getParent();
		Exp mysubclasses = getSubClasses();

		while (mysubclasses != env.getNil()) {
			assertIsThisObjectAClass(mysubclasses.car());
			StandardClass firstClass = (StandardClass)mysubclasses.car();
			if (firstClass == klass) {
				return true;
			} else if (firstClass.isSubClass(klass)) {
				return true;
			}
			mysubclasses = mysubclasses.cdr();
		}
		return false;
	}

	public static void  assertIsThisObjectAClass(Exp firstClass) throws GenyrisException {
		if(! (firstClass instanceof StandardClass)) {
			throw new GenyrisException(firstClass + "is not a class.");
		}
	}

	public boolean isInstance(Exp object) throws GenyrisException {
		Environment env = getParent();
		Exp classes;

		classes = object.getClasses(env);
		while (classes != env.getNil()) {
			assertIsThisObjectAClass(classes.car());
			StandardClass klass = (StandardClass) classes.car(); 
			if (classes.car() == this) {
				return true;
			}
			if (isSubClass(klass)) {
				return true;
			}
			classes = classes.cdr();
		}
		return false;
	}
	
	public void acceptVisitor(Visitor guest) throws GenyrisException {
		guest.visitStandardClass(this);
	}


}
