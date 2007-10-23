package org.genyris.interp.builtin;

import java.io.Writer;

import org.genyris.core.Exp;
import org.genyris.core.Lstring;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.LispinException;
import org.lispin.jlispin.io.NullWriter;
import org.lispin.jlispin.load.SourceLoader;

public class LoadFunction extends ApplicableFunction {
	
	public LoadFunction(Interpreter interp) {
		super(interp);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws LispinException {
		Exp result;
		Writer out = new NullWriter();
		if( !( arguments[0] instanceof Lstring) ) {
			throw new LispinException("non-string argument passed to load: " + arguments[0].toString());
		}
		if( arguments.length > 1 ) { 
			if( arguments[1] == TRUE) {
				out = _interp.getDefaultOutputWriter();
			}
		}
		result = SourceLoader.loadScriptFromClasspath(_interp, arguments[0].toString(), out);
		if( result == NIL ) {
			return NIL;
		}

		return TRUE;
	}
}
