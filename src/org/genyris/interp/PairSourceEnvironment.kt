package org.genyris.interp

import org.genyris.core.*

class PairSourceEnvironment(runtime: Environment?, theObject: Pair?) : PairEnvironment(runtime, theObject) {
    @kotlin.Throws(UnboundException::class)
    override fun lookupDynamicVariableValue(dsym: DynamicSymbol): Exp? {
        val sym = dsym.getRealSymbol()
        if (sym === _lineNumber) {
            return Bignum((_theExpression as PairSource).lineNumber)
        } else if (sym === _filename) {
            return StrinG((_theExpression as PairSource).filename)
        } else {
            return super.lookupDynamicVariableValue(dsym)
        }
    }

    @kotlin.Throws(UnboundException::class)
    override fun lookupLexicalVariableValue(sym: SimpleSymbol?): Exp? {
        if (sym === _lineNumber) {
            return Bignum((_theExpression as PairSource).lineNumber)
        } else if (sym === _filename) {
            return StrinG((_theExpression as PairSource).filename)
        } else {
            return super.lookupLexicalVariableValue(sym)
        }
    }
}
