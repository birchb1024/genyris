package org.lispin.jlispin.interp.builtin;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lcons;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.ClassicFunction;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.EagerProcedure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.LispinException;

public class DefFunction extends ApplicableFunction {
	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations)
			throws LispinException {
		Exp lambdaExpression = new Lcons(SymbolTable.lambda, arrayToList(arguments).cdr());
		// TODO inefficient
		EagerProcedure fn = new EagerProcedure(envForBindOperations, lambdaExpression, new ClassicFunction());
		envForBindOperations.defineVariable(arguments[0], fn);
		return fn;
	}

}
