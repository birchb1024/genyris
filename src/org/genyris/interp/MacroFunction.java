package org.genyris.interp;

import org.genyris.core.Exp;

public class MacroFunction extends ClassicFunction {

	public MacroFunction(Interpreter interp) {
		super(interp);
	}

	public Exp bindAndExecute(Closure closure, Exp[] arguments, Environment env) throws GenyrisException  {
		AbstractClosure proc = (AbstractClosure)closure; // TODO run time validation
		return Evaluator.eval(env, super.bindAndExecute( proc, arguments, proc.getEnv()));
	}

	public Object getJavaValue() {
		// TODO Auto-generated method stub
		return null;
	}

}
