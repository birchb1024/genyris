// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.dl

import org.genyris.core.Bignum
import org.genyris.core.DynamicSymbol
import org.genyris.core.Exp
import org.genyris.core.SimpleSymbol
import org.genyris.interp.Environment
import org.genyris.interp.ExpressionEnvironment
import org.genyris.interp.UnboundException

class GraphEnvironment(env: Environment, theObject: AbstractGraph?) : ExpressionEnvironment(env, theObject as Exp?) {
    // This environment encompasses a Graph
    var _subjects: SimpleSymbol?
    var _length: SimpleSymbol?

    init {
        _subjects = env.getSymbolTable().SUBJECTS()
        _length = env.getSymbolTable().LENGTH()
    }

    //	public void setDynamicVariableValue(DynamicSymbol symbol, Exp valu)
    //			throws UnboundException {
    //		throw new UnboundException("Attempt to alter immutable triple: "
    //				+ _theExpression.toString());
    //	}
    @kotlin.Throws(UnboundException::class)
    override fun lookupDynamicVariableValue(dsym: DynamicSymbol): Exp? {
        val sym = dsym.getRealSymbol()
        if (sym === _subjects) {
            return (_theExpression as AbstractGraph).subjects(NIL)
        } else if (sym === _length) {
            return Bignum((_theExpression as AbstractGraph).length())
        } else {
            return super.lookupDynamicVariableValue(dsym)
        }
    }
}
