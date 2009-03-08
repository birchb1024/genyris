package org.genyris.classification;

import java.util.Comparator;
import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Lobject;
import org.genyris.core.Symbol;
import org.genyris.exception.AccessException;
import org.genyris.interp.Environment;
import org.genyris.interp.UnboundException;

public class ClassMROComparator implements Comparator {
	private Environment env;
    public ClassMROComparator(Environment env) {
        env = this.env;

    }
    private Symbol SUPERCLASSES() {
    	return env.internString(Constants.SUPERCLASSES);
    }
    public int compare(Object o1, Object o2) {
        Lobject c1 = (Lobject) o1;
        Lobject c2 = (Lobject) o2;
        return getClassDepth(c1) - getClassDepth(c2);
    }
    private int getClassDepth(Exp klass) {
        Lobject c1 = (Lobject) klass;
        try {
            Exp superclasses = c1.lookupVariableShallow(SUPERCLASSES());
            if(superclasses == NIL()) {
                return 0;
            }
            int retval = 0;
            while( superclasses != NIL()) {
                int tmp = 1 + getClassDepth(superclasses.car());
                if (retval < tmp ){
                    retval = tmp;
                }
                superclasses = superclasses.cdr();
            }
            return retval;
        } catch (UnboundException e) {
            return 0;
        } catch (AccessException e) {
            return 1000000;
        }
    }
	private Exp NIL() {
		return env.getNil();
	}
}
