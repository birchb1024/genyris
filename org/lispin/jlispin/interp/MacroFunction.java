package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.Exp;

public class MacroFunction extends ClassicFunction {

	public Exp apply(Procedure proc, Environment env, Exp[] arguments) throws LispinException  { 

		return env.eval(super.apply( proc, env, arguments));
	}

	public Object getJavaValue() {
		// TODO Auto-generated method stub
		return null;
	}

}
