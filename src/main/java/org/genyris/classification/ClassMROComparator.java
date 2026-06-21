package org.genyris.classification;

import java.util.Comparator;

import org.genyris.core.Dictionary;
import org.genyris.core.Exp;
import org.genyris.core.SimpleSymbol;
import org.genyris.exception.AccessException;
import org.genyris.interp.UnboundException;

public class ClassMROComparator implements Comparator {

	private SimpleSymbol NIL, SUPERCLASSES;
    public ClassMROComparator(SimpleSymbol NIL, SimpleSymbol superclasses) {
       this.NIL = NIL;
       this.SUPERCLASSES = superclasses;
    }

    public int compare(Object o1, Object o2) {
        Dictionary c1 = (Dictionary) o1;
        Dictionary c2 = (Dictionary) o2;
        return getClassDepth(c1) - getClassDepth(c2);
    }
    private int getClassDepth(Exp klass) {
        Dictionary c1 = (Dictionary) klass;
        try {
            Exp superclasses = c1.lookupVariableShallow(SUPERCLASSES);
            if(superclasses == NIL) {
                return 0;
            }
            int retval = 0;
            while( superclasses != NIL) {
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
}
