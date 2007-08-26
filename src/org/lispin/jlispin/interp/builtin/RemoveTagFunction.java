package org.lispin.jlispin.interp.builtin;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.LispinException;

public class RemoveTagFunction extends ApplicableFunction {

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws LispinException {
		if( arguments.length != 2)
			throw new LispinException("Too few arguments to removetag: " + arguments.length);
		Exp object = arguments[0];
		Exp newClass = arguments[1];
		object.removeClass(newClass);
		return env.getNil();
	}
}
