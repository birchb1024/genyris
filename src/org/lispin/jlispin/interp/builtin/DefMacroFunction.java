package org.lispin.jlispin.interp.builtin;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lcons;
import org.lispin.jlispin.core.Lsymbol;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.LazyProcedure;
import org.lispin.jlispin.interp.LispinException;
import org.lispin.jlispin.interp.MacroFunction;

public class DefMacroFunction extends ApplicableFunction {
	
	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations)
			throws LispinException {
		Lsymbol NIL = envForBindOperations.getNil();
		Exp lambdaExpression = new Lcons(SymbolTable.lambdam, arrayToList(arguments, NIL).cdr());
		// TODO inefficient
		LazyProcedure fn = new LazyProcedure(envForBindOperations, lambdaExpression, new MacroFunction());
		envForBindOperations.defineVariable(arguments[0], fn);
		return fn;
	}

}
