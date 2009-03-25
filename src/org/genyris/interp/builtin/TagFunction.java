// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.classification.ClassWrapper;
import org.genyris.core.Dictionary;
import org.genyris.core.Exp;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.GenyrisTypeMismatchException;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;

public class TagFunction extends ApplicableFunction {

	public TagFunction(Interpreter interp) {
		super(interp, "tag", true);
	}

	private SimpleSymbol VALIDATE() {
		return _interp.getSymbolTable().VALIDATE();
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment environment) throws GenyrisException {
		checkArguments(arguments, 2);
		Class[] types = { Dictionary.class };
		checkArgumentTypes(types, arguments);
		Exp object = arguments[1];
		Dictionary klass = (Dictionary) arguments[0];
		callValidator(klass, object, klass); // use the class itself as the
												// env for validation so it can
												// access class params etc.
		object.addClass(klass);
		return object;
	}

	// TODO move these into ClassWrapper:
	public void callValidator(Environment environment, Exp object,
			Dictionary klassobject) throws GenyrisException {
		// TODO DRY
		Exp NIL = environment.getNil();
		Exp validator = null;
		ClassWrapper klass = new ClassWrapper(klassobject);
		try {
			validator = klassobject.lookupVariableValue(VALIDATE());
		} catch (UnboundException ignore) {
			return;
		}
		Exp args[] = new Exp[1];
		args[0] = object;
		Exp result = validator.applyFunction(environment, args);
		if (result == NIL) {
			throw new GenyrisException("class " + klass.getClassName()
					+ " validator error for object " + object);
		}

	}

	public static void validateObjectInClass(Environment environment,
			Exp object, Dictionary klassobject) throws GenyrisException {
		Exp NIL = environment.getNil();
		Exp validator = null;
		ClassWrapper klass = new ClassWrapper(klassobject);
		try {
			validator = klassobject.lookupVariableValue(VALIDATE(environment));
		} catch (UnboundException ignore) { 
			if (!klass.isInstance(object)) {
				throw new GenyrisTypeMismatchException("class "
						+ klass.getClassName() + " validator error for object "
						+ object);
			}
		}
		if (validator != null) {
			Exp args[] = new Exp[1];
			args[0] = object;
			Exp result = validator.applyFunction(environment, args);
			if (result == NIL) {
				throw new GenyrisException("class " + klass.getClassName()
						+ " validator error for object " + object);
			}
		}
	}

	private static Symbol VALIDATE(Environment environment) {
		return environment.getSymbolTable().VALIDATE();
	}

}
