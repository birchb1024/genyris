package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lcons;

public class BuiltInCons extends BuiltInProcedure {
	
	public Exp apply(Environment env,  Exp[] arguments) {
		// TODO check args
		return new Lcons(arguments[0], arguments[1]);
	}

	public Object getJavaValue() {
		// TODO Auto-generated method stub
		return null;
	}

}
