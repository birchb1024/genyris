// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core

import org.genyris.exception.AccessException
import org.genyris.exception.GenyrisException
import org.genyris.format.AbstractFormatter
import org.genyris.format.BasicFormatter
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.PairEnvironment
import org.genyris.interp.UnboundException
import java.io.StringWriter

open class Pair(private var _car: Exp, private var _cdr: Exp) : ExpWithEmbeddedClasses(), Comparable<Any?> {
    override fun getBuiltinClassSymbol(table: Internable): Symbol? {
        return table.PAIR()
    }

    @kotlin.Throws(GenyrisException::class)
    override fun acceptVisitor(guest: Visitor) {
        guest.visitPair(this)
    }

    override fun equals(compare: Any?): Boolean {
        if (compare !is Pair) return false
        else return this._car == compare._car
                && this._cdr == compare._cdr
    }

    override fun isPair(): Boolean {
        return true
    }

    override fun car(): Exp {
        return _car
    }

    override fun cdr(): Exp {
        return _cdr
    }

    override fun setCar(exp: Exp): Exp {
        this._car = exp

        return this
    }

    override fun setCdr(exp: Exp): Exp {
        this._cdr = exp

        return this
    }

    override fun toString(): String {
        val buffer = StringWriter()
        val formatter: AbstractFormatter = BasicFormatter(buffer)
        try {
            this.acceptVisitor(formatter)
        } catch (e: GenyrisException) {
            return e.getMessage()
        }
        return buffer.toString()
    }

    override fun hashCode(): Int {
        return _car.hashCode() + _cdr.hashCode()
    }

    @kotlin.Throws(GenyrisException::class)
    override fun eval(env: Environment): Exp? {
        var proc: Closure? = null
        var arguments: Array<Exp?>? = null
        var toEvaluate: Exp = this
        var retval: Exp? = env.getNil()
        do {
            try {
                proc = (toEvaluate.car().eval(env)) as Closure?
            } catch (e1: UnboundException) {
                try {
                    // Is there are sys:procedure-missing defined?
                    proc = env.getSymbolTable().PROCEDUREMISSING()
                        .lookupVariableValue(env)
                } catch (e2: UnboundException) {
                    // no - just throw exception
                    throw e1
                }
                if (alreadyInProcedureMissing) {
                    // protect user by catching undefineds in a sys:procedure-missing
                    alreadyInProcedureMissing = false
                    throw GenyrisException(
                        ("Unbound symbol within "
                                + env.getSymbolTable().PROCEDUREMISSING() + " "
                                + e1.getMessage())
                    )
                }
                // Now process the missing function logic...
                alreadyInProcedureMissing = true
                try {
                    arguments = prependArgument(
                        toEvaluate.car(),
                        proc.computeArguments(env, toEvaluate.cdr())
                    )
                    retval = proc.applyFunction(env, arguments)
                } catch (e3: GenyrisException) {
                    // turn off the flag and re-throw any exceptions
                    throw e3
                } finally {
                    alreadyInProcedureMissing = false
                }
                // Process a trampoline if returned...
                if (retval is Biscuit) {
                    toEvaluate = retval.getExpression()
                    if (toEvaluate !is Pair) {
                        // can only use this do-while loop for expressions, 
                        // have to use function call for all others.
                        return toEvaluate.eval(env)
                    }
                }
                return retval
            }
            arguments = proc!!.computeArguments(env, toEvaluate.cdr())
            retval = proc.applyFunction(env, arguments)
            if (retval is Biscuit) {
                toEvaluate = retval.getExpression()
                if (toEvaluate !is Pair) {
                    // can only use this do-while loop for expressions, 
                    // have to use function call for all others.
                    return toEvaluate.eval(env)
                }
            }
        } while (retval is Biscuit)
        return retval
    }

    @kotlin.Throws(GenyrisException::class)
    private fun prependArgument(firstArg: Exp?, tmparguments: Array<Exp?>): Array<Exp?> {
        val arguments = arrayOfNulls<Exp>(tmparguments.size + 1)
        arguments[0] = firstArg
        for (i in tmparguments.indices) {
            arguments[i + 1] = tmparguments[i]
        }
        return arguments
    }

    @kotlin.Throws(GenyrisException::class)
    override fun evalSequence(env: Environment): Exp? {
        val NIL = env.getNil()
        val body: Exp = this
        if (body.cdr() === NIL) {
            return body.car().eval(env)
        } else {
            body.car().eval(env)
            return body.cdr().evalSequence(env)
        }
    }

    @kotlin.Throws(AccessException::class)
    override fun length(NIL: Symbol?): Int {
        var tmp: Exp = this
        var count = 0

        while (tmp !== NIL && (tmp is Pair)) {
            tmp = tmp.cdr()
            count++
        }
        return count
    }

    @kotlin.Throws(AccessException::class)
    override fun nth(number: Int, NIL: Symbol?): Exp? {
        var tmp: Exp = this
        var count = 0
        while (tmp !== NIL) {
            if (count == number) {
                return tmp.car()
            }
            tmp = tmp.cdr()
            count++
        }
        throw AccessException("nth could not find item: " + number)
    }

    @kotlin.Throws(GenyrisException::class)
    override fun makeEnvironment(parent: Environment?): Environment? {
        return PairEnvironment(parent, this)
    }

    override fun dir(table: Internable): Exp {
        return cons2(
            DynamicSymbol(table.LEFT()),
            DynamicSymbol(table.RIGHT()), super.dir(table)
        )
    }

    override fun compareTo(o: Any?): Int {
        if (!((o is Pair) || o !is PairEquals)) {
            return -1
        }
        val p = o as Pair?
        if (this._car.compareTo(p!!._car) == 0) {
            return this._cdr.compareTo(p._cdr)
        }
        return this._car.compareTo(p._car)
    }

    companion object {
        private var alreadyInProcedureMissing = false // TODO: Not re-entrant

        @kotlin.Throws(GenyrisException::class)
        fun reverse(list: Exp, NIL: Exp): Exp {
            var list = list
            if (list.isNil()) {
                return list
            }
            if (list is Pair) {
                var rev_result = NIL

                while (list !== NIL) {
                    rev_result = Pair(list.car(), rev_result)
                    list = list.cdr()
                }
                return (rev_result)
            } else {
                throw GenyrisException("reverse: not a list: " + list)
            }
        }

        fun cons(a: Exp, b: Exp): Exp {
            return Pair(a, b)
        }

        fun cons2(a: Exp, b: Exp, NIL: Exp): Exp {
            return Pair(a, Pair(b, NIL))
        }

        fun cons3(a: Exp, b: Exp, c: Exp, NIL: Exp): Exp {
            return Pair(a, Pair(b, Pair(c, NIL)))
        }

        fun cons4(a: Exp, b: Exp, c: Exp, d: Exp, NIL: Exp): Exp {
            return Pair(a, Pair(b, Pair(c, Pair(d, NIL))))
        }
    }
}
