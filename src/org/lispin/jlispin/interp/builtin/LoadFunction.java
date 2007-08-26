package org.lispin.jlispin.interp.builtin;

import java.io.Writer;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lstring;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.Interpreter;
import org.lispin.jlispin.interp.LispinException;
import org.lispin.jlispin.io.NullWriter;
import org.lispin.jlispin.load.SourceLoader;

public class LoadFunction extends ApplicableFunction {

	private Interpreter _interp;
	private Exp NIL;
	
	public LoadFunction(Interpreter interp) {
		_interp = interp;
		NIL = _interp.getNil();
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws LispinException {
		Exp result;
		Writer out = new NullWriter();
		if( !( arguments[0] instanceof Lstring) ) {
			throw new LispinException("non-string argument passed to load: " + arguments[0].toString());
		}
		if( arguments.length > 1 ) { 
			if( arguments[1] == SymbolTable.T) {
				out = _interp.getDefaultOutputWriter();
			}
		}
		result = SourceLoader.loadScriptFromClasspath(_interp, arguments[0].toString(), out);
		if( result == NIL ) {
			return NIL;
		}

		return SymbolTable.T;
	}
}
