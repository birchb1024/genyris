package org.lispin.jlispin.interp.builtin;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lcons;
import org.lispin.jlispin.core.Lsymbol;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.ClassicFunction;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.LazyProcedure;
import org.lispin.jlispin.interp.LispinException;

public class LambdaqFunction extends ApplicableFunction {

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws LispinException {
			Lsymbol NIL = env.getNil();
			//			 TODO Optimise -  repack of args inefficient
			Exp expression = arrayToList(arguments, NIL);
			expression = new Lcons( SymbolTable.lambdaq, expression);
			return new LazyProcedure(env, expression,  new ClassicFunction());

		}
		public Object getJavaValue() {
			return "<the lambdaq builtin function>";
		}

	}
