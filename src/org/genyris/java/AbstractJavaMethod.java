package org.genyris.java;

// Copyright 2010 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
import org.genyris.exception.GenyrisException;
import org.genyris.interp.AbstractMethod;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;

public abstract class AbstractJavaMethod extends AbstractMethod {

	public AbstractJavaMethod(Interpreter interp, String name) {
		super(interp, name);
	}

	protected JavaWrapper getSelfJava(Environment env) throws GenyrisException {
		getSelf(env);
		if (!(_self instanceof JavaWrapper)) {
			return new JavaWrapper(_self);
		} else {
			JavaWrapper theJava = (JavaWrapper) _self;
			return theJava;
		}
	}

	public static void bindFunctionsAndMethods(Interpreter interpreter)
			throws UnboundException, GenyrisException {
		interpreter.bindGlobalProcedureInstance(new ImportFunction( interpreter));
		interpreter.bindGlobalProcedureInstance(new ToJavaFunction( interpreter));
		interpreter.bindGlobalProcedureInstance(new ToGenyrisFunction( interpreter));
		interpreter.bindGlobalProcedureInstance(new ListenerFunction( interpreter));
	}
}
