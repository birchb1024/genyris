// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.classes;

import org.genyris.classification.ClassWrapper;
import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Dictionary;
import org.genyris.core.Internable;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;

public class BuiltinClasses {

	private static Dictionary mkClass(String name, Environment env,
			Dictionary superClass) throws GenyrisException {
		Internable table = env.getSymbolTable();
		Symbol STANDARDCLASS = table.STANDARDCLASS();
		Exp standardClassDict = env.lookupVariableValue(STANDARDCLASS);
		SimpleSymbol classname = table.CLASSNAME();
		SimpleSymbol symbolicName = table.internString(name);

		Dictionary newClass = makeTheClass(env, superClass, table,
				standardClassDict, classname, symbolicName);
		GlobalDescriptions.updateClassSingleSuper(env, table, symbolicName,
				(superClass!=null?(Symbol) superClass.lookupVariableShallow(classname):null));
		return newClass;
	}

	private static Dictionary makeTheClass(Environment env,
			Dictionary superClass, Internable table, Exp standardClassDict,
			SimpleSymbol classname, SimpleSymbol symbolicName) throws GenyrisException {
		Dictionary newClass = new Dictionary(classname, symbolicName, env);
		newClass.defineVariableRaw(table.SUPERCLASSES(), env.getNil());
		newClass.defineVariableRaw(table.SUBCLASSES(), env.getNil());
		newClass.addClass(standardClassDict);
		if (superClass != null)
			new ClassWrapper(newClass).addSuperClass(superClass);
		env.defineVariable(symbolicName, newClass);
		return newClass;
	}

	public static void init(Environment env) throws GenyrisException {
		Dictionary standardClassDict;
		Internable table = env.getSymbolTable();
		SimpleSymbol CLASSNAME = table.CLASSNAME();
		{
			// Bootstrap the meta-class
			standardClassDict = new Dictionary(CLASSNAME,
					table.STANDARDCLASS(), env);
			standardClassDict.addClass(standardClassDict);
			env.defineVariable(table.STANDARDCLASS(), standardClassDict);
		}

		Dictionary THING = mkClass("Thing", env, null);
		Dictionary builtin = mkClass("Builtin", env, THING);
		mkClass(Constants.DICTIONARY, env, builtin);
		mkClass(Constants.INTEGER, env, builtin);
		mkClass(Constants.BIGNUM, env, builtin);
		mkClass(Constants.STRING, env, builtin);
		mkClass(Constants.DOUBLE, env, builtin);
		mkClass(Constants.FILE, env, builtin);
		mkClass(Constants.READER, env, builtin);
		mkClass(Constants.WRITER, env, builtin);
		mkClass(Constants.SYSTEM, env, builtin);
		mkClass(Constants.INDENTEDPARSER, env, builtin);
		mkClass(Constants.PARENPARSER, env, builtin);
		mkClass(Constants.STRINGFORMATSTREAM, env, builtin);
		mkClass(Constants.SOUND, env, builtin);
		mkClass(Constants.TRIPLE, env, builtin);
		mkClass(Constants.TRIPLESET, env, builtin);

		Dictionary symbol = mkClass(Constants.SYMBOL, env, builtin);
		Dictionary closure = mkClass(Constants.CLOSURE, env, builtin);
		Dictionary pair = mkClass("Pair", env, builtin);
		mkClass(Constants.PRINTWITHCOLON, env, pair);
		mkClass(Constants.SIMPLESYMBOL, env, symbol);
		mkClass(Constants.URISYMBOL, env, symbol);
		mkClass(Constants.EAGERPROCEDURE, env, closure);
		mkClass(Constants.LAZYPROCEDURE, env, closure);
		mkClass(Constants.LISTOFLINES, env, pair);
		mkClass(Constants.DYNAMICSYMBOLREF, env, symbol);
	}
}
