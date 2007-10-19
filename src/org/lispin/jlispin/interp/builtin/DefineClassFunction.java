package org.lispin.jlispin.interp.builtin;

import genyris.classification.ClassWrapper;
import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lcons;
import org.lispin.jlispin.core.Lsymbol;
import org.lispin.jlispin.interp.ApplicableFunction;
import org.lispin.jlispin.interp.Closure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.Evaluator;
import org.lispin.jlispin.interp.Interpreter;
import org.lispin.jlispin.interp.LispinException;

public class DefineClassFunction extends ApplicableFunction {

	public DefineClassFunction(Interpreter interp) {
		super(interp);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env) throws LispinException {

        if( arguments.length < 1)
            throw new LispinException("Incorrect number of arguments to class.");
        if(! (arguments[0] instanceof Lsymbol)) {
            throw new LispinException("class expects a symbols.");
        }
        Exp klassname = arguments[0];
        Exp superklasses = ( arguments.length > 1 ? arguments[1] : NIL);
        Exp newClass = ClassWrapper.makeClass(env, klassname, superklasses);
        if (arguments.length > 2) {
            Exp body = arrayToList(arguments);
            body = body.cdr().cdr();
            body = new Lcons(klassname, body);
            return Evaluator.eval(env, body);
        }
        return newClass;
    }

    public Object getJavaValue() {
        return "<the class builtin function>";
    }

}
