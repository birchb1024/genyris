// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.StandardClass;
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
		Class[] types = { StandardClass.class };
		checkArgumentTypes(types, arguments);
		Exp object = arguments[1];
		StandardClass klass = (StandardClass) arguments[0];
		callValidator(klass, object, klass); // use the class itself as the
												// env for validation so it can
												// access class params etc.
		object.addClass(klass);
		return object;
	}

	// TODO move these into ClassWrapper:
	public void callValidator(Environment environment, Exp object,
			StandardClass klassobject) throws GenyrisException {
		// TODO DRY
		Exp NIL = environment.getNil();
		Exp validator = null;
		try {
			validator = klassobject.lookupVariableValue(VALIDATE());
		} catch (UnboundException ignore) {
			return;
		}
		Exp args[] = new Exp[1];
		args[0] = object;
		Exp result = validator.applyFunction(environment, args);
		if (result == NIL) {
			throw new GenyrisException("class " + klassobject.getClassName()
					+ " validator error for object " + object);
		}

	}

	public static void validateObjectInClass(Environment environment,
			Exp object, StandardClass klassobject) throws GenyrisException {
		Exp NIL = environment.getNil();
		Exp validator = null;
		try {
			validator = klassobject.lookupVariableValue(VALIDATE(environment));
		} catch (UnboundException ignore) { 
			if (!klassobject.isInstance(object)) {
				throw new GenyrisTypeMismatchException("class "
						+ klassobject.getClassName() + " validator error for object "
						+ object);
			}
		}
		if (validator != null) {
			Exp args[] = new Exp[1];
			args[0] = object;
			Exp result = validator.applyFunction(environment, args);
			if (result == NIL) {
				throw new GenyrisException("class " + klassobject.getClassName()
						+ " validator error for object " + object);
			}
		}
	}

	private static Symbol VALIDATE(Environment environment) {
		return environment.getSymbolTable().VALIDATE();
	}

}
