package org.genyris.classes;

import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.Symbol;
import org.genyris.dl.Triple;
import org.genyris.dl.TripleSet;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;

public class GlobalDescriptions {
	public static void updateClassSingleSuper(Environment env, Internable table, Symbol klassname, Symbol superclass) throws GenyrisException {
		TripleSet globalDescriptions = (TripleSet)env.lookupVariableValue(table.DESCRIPTIONS());
		TripleSet result = globalDescriptions.difference(globalDescriptions.select(klassname, table.TYPE(), null, null, env));
		result.add(new Triple(klassname, table.TYPE(), table.STANDARDCLASS()));
		if(superclass != null) {
			result.add(new Triple(klassname, table.SUBCLASSOF(), superclass ));
		}
		env.setVariableValue(table.DESCRIPTIONS(), result);
	}
	public static void updateClass(Environment env, Internable table, Symbol klassname, Exp superClassSymList) throws GenyrisException {
		TripleSet globalDescriptions = (TripleSet)env.lookupVariableValue(table.DESCRIPTIONS());
		TripleSet result = globalDescriptions.difference(globalDescriptions.select(klassname, table.TYPE(), null, null, env));
		result.add(new Triple(klassname, table.TYPE(), table.STANDARDCLASS()));
		while(superClassSymList != table.NIL()) {
			result.add(new Triple(klassname, table.SUBCLASSOF(), (Symbol)superClassSymList.car()));
			superClassSymList = superClassSymList.cdr();
		}
		env.setVariableValue(table.DESCRIPTIONS(), result);
	}

}
