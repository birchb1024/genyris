package org.genyris.core;

import org.genyris.classes.GlobalDescriptions;
import org.genyris.classification.ClassWrapper;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;

public class StandardClass extends Dictionary {

	public StandardClass(SimpleSymbol classname, SimpleSymbol symbolicName, Environment env) {
		super( classname,  symbolicName,  env);
	}

	public StandardClass(Environment env) {
		super(env);
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
			new ClassWrapper(newClass).addSuperClass(superClass);
		env.defineVariable(symbolicName, newClass);
		return newClass;
	}


}
