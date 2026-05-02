package org.genyris.core

import org.genyris.exception.GenyrisException
import org.genyris.interp.Environment
import org.genyris.interp.UnboundException
import java.net.URI
import java.net.URISyntaxException

abstract class Symbol : Atom(), Comparable<Any?> {
    override fun hashCode(): Int { // TODO make abstract
        return this.printName!!.hashCode()
    }

    override fun equals(other: Any?): Boolean { // TODO make abstract
        if (other !is Symbol) {
            return false
        }
        return this.printName == other.printName
    }

    override fun isNil(): Boolean {
        return false
    }

    abstract val printName: String?

    override fun toString(): String {
        return this.printName!!
    }

    @kotlin.Throws(UnboundException::class)
    abstract fun lookupVariableValue(env: Environment?): Exp?

    @kotlin.Throws(UnboundException::class)
    abstract fun setVariableValue(env: Environment?, valu: Exp?)

    @kotlin.Throws(GenyrisException::class)
    abstract fun defineVariable(env: Environment?, valu: Exp?)

    abstract override fun compareTo(arg0: Any?): Int

    @kotlin.Throws(GenyrisException::class)
    override fun eval(env: Environment): Exp? {
        return env.lookupVariableValue(this)
    }


    companion object {
        fun symbolFactory(name: String, escaped: Boolean): SimpleSymbol {
            try {
                val uri = URI(name)
                if (uri.isAbsolute()) {
                    return URISymbol(name)
                }
            } catch (e: URISyntaxException) {
            }
            return (if (escaped) EscapedSymbol(name) else SimpleSymbol(name))
        }
    }

    abstract fun getPrintName(): java.lang.String
}