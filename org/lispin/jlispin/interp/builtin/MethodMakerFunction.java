package org.lispin.jlispin.interp.builtin;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lcons;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.EagerProcedure;
import org.lispin.jlispin.interp.LispinException;
import org.lispin.jlispin.interp.MethodApplicationFunction;

public class MethodMakerFunction extends ApplicableFunction {

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws LispinException {

			// TODO - inefficient
			Exp expression = arrayToList(arguments); 
			expression = new Lcons( SymbolTable.method, expression);
			return new EagerProcedure(env, expression,  new MethodApplicationFunction());

		}
		public Object getJavaValue() {
			return "<the methodmaker builtin function>";
		}

	}
