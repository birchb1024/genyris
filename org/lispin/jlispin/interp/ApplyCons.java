package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lcons;

public class ApplyCons extends Applicable {

	public Exp apply(Procedure proc, Environment env, Exp[] arguments) throws Exception {
		// TODO check number of args
		return new Lcons(arguments[0], arguments[1]);
	}

}
