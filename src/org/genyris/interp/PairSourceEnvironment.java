package org.genyris.interp;

import org.genyris.core.Bignum;
import org.genyris.core.DynamicSymbol;
import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.core.PairSource;
import org.genyris.core.StrinG;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;

public class PairSourceEnvironment extends PairEnvironment {
    public PairSourceEnvironment(Environment runtime, Pair theObject)
            throws GenyrisException {
        super(runtime, theObject);

    }

    public Exp lookupDynamicVariableValue(DynamicSymbol dsym)
            throws UnboundException {
        Symbol sym = dsym.getRealSymbol();
        if (sym == _lineNumber) {
            return new Bignum(((PairSource)_theExpression).lineNumber);
        } else if (sym == _filename) {
            return new StrinG(((PairSource)_theExpression).filename);
        } else {
            return super.lookupDynamicVariableValue(dsym);
        }

    }

}
