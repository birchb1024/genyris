package org.genyris.core

import org.genyris.exception.GenyrisException
import org.genyris.interp.Environment
import org.genyris.interp.UnboundException

class DynamicSymbol(val realSymbol: Symbol) : Symbol() {
    override fun compareTo(arg0: Any?): Int {
        if (arg0 !is DynamicSymbol) {
            return -1
        }
        return arg0.realSymbol.compareTo(this.realSymbol)
    }

    override fun equals(arg0: Any?): Boolean {
        if (arg0 !is DynamicSymbol) {
            return false
        }
        return this.realSymbol == arg0.realSymbol
    }

    override fun hashCode(): Int {
        return realSymbol.hashCode()
    }

    @kotlin.Throws(GenyrisException::class)
    override fun acceptVisitor(guest: Visitor) {
        guest.visitDynamicSymbol(this)
    }

    override fun getBuiltinClassSymbol(table: Internable): Symbol? {
        return table.DYNAMICSYMBOLREF()
    }

    override fun toString(): String {
        return "." + realSymbol.toString()
    }

    override fun getPrintName(): String {
        return "." + realSymbol.getPrintName()
    }

    @kotlin.Throws(GenyrisException::class)
    override fun defineVariable(env: Environment, valu: Exp?) {
        env.defineDynamicVariable(this, valu)
    }

    @kotlin.Throws(UnboundException::class)
    override fun lookupVariableValue(env: Environment): Exp? {
        return env.lookupDynamicVariableValue(this)
    }

    @kotlin.Throws(UnboundException::class)
    override fun setVariableValue(env: Environment, valu: Exp?) {
        env.setDynamicVariableValue(this, valu)
    }
}
