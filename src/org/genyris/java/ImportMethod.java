package org.genyris.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.genyris.core.Constants;
import org.genyris.core.DynamicSymbol;
import org.genyris.core.EscapedSymbol;
import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.StandardClass;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.EagerProcedure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class ImportMethod extends AbstractJavaMethod {

	public ImportMethod(Interpreter interp) {
		super(interp, "import");
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment envForBindOperations) throws GenyrisException {
		Class[] types = {StrinG.class};
		this.checkArgumentTypes(types, arguments);
		String klassName = arguments[0].toString();
		Class klass = null;
		try {
			klass = Class.forName(klassName);
		} catch (ClassNotFoundException e) {
			throw new GenyrisException("Java ClassNotFoundException: " + e.getMessage());
		}
		StandardClass glass = StandardClass.makeClass(_interp.getGlobalEnv(), _interp.intern(new EscapedSymbol(klassName)), new Pair(_interp.intern(Constants.JAVA),NIL));
		//
		Constructor[] ctors = klass.getConstructors();
		for(int c=0;c<ctors.length;c++) {
			Class[] params = ctors[c].getParameterTypes();
			String name = "new";
			for(int p=0;p<params.length;p++) {
				name += "-" + params[p].getName();
			}
			JavaCtor gctor = new JavaCtor(_interp, glass, name, ctors[c], params);
			glass.defineDynamicVariable(new DynamicSymbol(_interp.intern(name))
				, new EagerProcedure(envForBindOperations, NIL, gctor));
		}
		//
		Method[] methods = klass.getMethods();
		for(int i=0;i<methods.length;i++) {
			Class[] params = methods[i].getParameterTypes();
			String name = methods[i].getName();
			for(int p=0;p<params.length;p++) {
				name += "-" + params[p].getName();
			}
			JavaMethod gmethod = new JavaMethod(_interp, name, methods[i], params);
			glass.defineDynamicVariable(new DynamicSymbol(_interp.intern(name)), new EagerProcedure(envForBindOperations, NIL, gmethod));
		}
		return glass;
	}

}
