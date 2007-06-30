package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.Exp;

public class MacroFunction extends ClassicFunction {

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws LispinException  { 

		return Evaluator.eval(proc.getEnv(), super.bindAndExecute( proc, arguments, null));
	}
	public String getName() {
		return "anonymous macro";
	}

	public Object getJavaValue() {
		// TODO Auto-generated method stub
		return null;
	}

}
