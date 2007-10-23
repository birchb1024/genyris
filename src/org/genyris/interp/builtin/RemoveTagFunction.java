package org.genyris.interp.builtin;

import org.genyris.core.Exp;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.LispinException;

public class RemoveTagFunction extends ApplicableFunction {

	public RemoveTagFunction(Interpreter interp) {
		super(interp);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws LispinException {
		if( arguments.length != 2)
			throw new LispinException("Too few arguments to removetag: " + arguments.length);
		Exp object = arguments[0];
		Exp newClass = arguments[1];
		object.removeClass(newClass);
		return NIL;
	}
}
