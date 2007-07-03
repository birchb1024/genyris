package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.Exp;

public class MacroFunction extends ClassicFunction {

	public Exp bindAndExecute(Closure closure, Exp[] arguments, Environment env) throws LispinException  { 

		AbstractClosure proc = (AbstractClosure)closure; // TODO run time validation

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
