package org.genyris.interp;

import org.genyris.core.*;
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

    public Exp lookupLexicalVariableValue(SimpleSymbol sym)
			throws UnboundException {
        if (sym == _lineNumber) {
            return new Bignum(((PairSource)_theExpression).lineNumber);
        } else if (sym == _filename) {
            return new StrinG(((PairSource)_theExpression).filename);
        } else {
            return super.lookupLexicalVariableValue(sym);
        }
	}


}
