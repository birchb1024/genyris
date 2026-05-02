package org.genyris.core

import org.genyris.exception.GenyrisException
import org.genyris.interp.Environment
import org.genyris.interp.PairSourceEnvironment

class PairSource(car: Exp?, cdr: Exp?, var filename: String?, var lineNumber: Int) : Pair(car, cdr), Comparable<Any?> {
    override fun getBuiltinClassSymbol(table: Internable): Symbol? {
        return table.PAIRSOURCE()
    }

    @kotlin.Throws(GenyrisException::class)
    override fun makeEnvironment(parent: Environment?): Environment {
        return PairSourceEnvironment(parent, this)
    }

    override fun dir(table: Internable): Exp {
        return Pair.Companion.cons2(
            DynamicSymbol(table.LINENUMBER()),
            DynamicSymbol(table.FILENAME()),
            super.dir(table)
        )
    }

    @kotlin.Throws(GenyrisException::class)
    override fun eval(env: Environment): Exp? {
        try {
            return super.eval(env)
        } catch (e: GenyrisException) {
            if (e.filename == null) {
                e.filename = this.filename
                e.lineNumber = this.lineNumber
            }
            throw e
        }
    }

    companion object {
        fun clone(l: PairSource, r: Exp?): PairSource {
            return PairSource(l, r, l.filename, l.lineNumber)
        }
    }
}
