// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp

import org.genyris.core.*
import org.genyris.exception.AccessException
import org.genyris.exception.GenyrisException

abstract class AbstractClosure(
    val env: Environment, @get:kotlin.Throws(AccessException::class) val code: Exp,
    val _functionToApply: ApplicableFunction
) : Atom(), Closure {
    protected var _numberOfRequiredArguments: Int
    private var _returnClass: StandardClass? = null
    private var _restArgs = false

    init {
        _numberOfRequiredArguments = -1
    }

    private fun REST(): Symbol? {
        return env.getSymbolTable().REST()
    }

    private fun NIL(): Symbol? {
        return env.getNil()
    }

    @kotlin.Throws(GenyrisException::class)
    private fun countFormalArguments(exp: Exp?): Int {
        var exp = exp
        var count = 0
        while (exp !== NIL()) {
            if (exp !is Pair) { // ignore trailing type specification
                break
            }
            if (exp.car() === REST()) {
                _restArgs = true
                return count
            }
            count += 1
            exp = exp.cdr()
        }
        return count
    }

    @kotlin.Throws(GenyrisException::class)
    fun getArgumentOrNIL(index: Int): Exp? {
        try {
            return code.cdr().car().nth(index, NIL())
        } catch (e: AccessException) {
            throw GenyrisException(
                "Additional argument to function "
                        + this.code
            )
        }
    }

    override fun getBody(NIL: Exp?): Exp? {
        if (this.code === NIL) return NIL
        try {
            return code.cdr().cdr()
        } catch (e: AccessException) {
            // internal error if this structure not present.
            e.printStackTrace()
            return NIL
        }
    }

    @kotlin.Throws(GenyrisException::class)
    abstract override fun computeArguments(env: Environment?, exp: Exp?): Array<Exp?>?

    @kotlin.Throws(GenyrisException::class)
    override fun applyFunction(environment: Environment?, arguments: Array<Exp?>?): Exp? {
        return _functionToApply.bindAndExecuteAux(this, arguments, environment) // double dispatch
    }

    @get:kotlin.Throws(GenyrisException::class)
    val numberOfRequiredArguments: Int
        get() {
            if (_numberOfRequiredArguments < 0) {
                _numberOfRequiredArguments = countFormalArguments(
                    code.cdr()
                        .car()
                )
            }
            return _numberOfRequiredArguments
        }

    val name: String?
        get() = _functionToApply.getName()

    @get:kotlin.Throws(GenyrisException::class)
    val returnClassOrNull: StandardClass?
        get() {
            if (_returnClass != null) {
                return _returnClass
            }
            val args = code.cdr().car()
            var returnTypeSymbolFound = NIL()
            var possibleReturnClass: Exp? = NIL()
            if (args !== NIL()) {
                var tmp = args
                while (tmp.cdr() !== NIL()) { // TODO refactor this loop into
                    if (tmp.cdr() is Symbol) {
                        returnTypeSymbolFound = tmp.cdr() as Symbol?
                        possibleReturnClass = env.lookupVariableValue(returnTypeSymbolFound)
                        break
                    }
                    tmp = tmp.cdr()
                }
            }
            if (possibleReturnClass === NIL()) {
                return null
            }
            if (possibleReturnClass is StandardClass) {
                return (possibleReturnClass.also { _returnClass = it })
            }
            throw GenyrisException(possibleReturnClass.toString() + " return class not a class.")
        }

    @kotlin.Throws(GenyrisException::class)
    override fun makeEnvironment(parent: Environment?): Environment {
        return ProcEnvironment(parent, this)
    }

    override fun dir(table: Internable): Exp {
        return Pair.Companion.cons2(
            DynamicSymbol(table.SOURCE()),
            DynamicSymbol(table.NAME()),
            Pair.Companion.cons3(
                DynamicSymbol(table.SELF()),
                DynamicSymbol(table.VARS()),
                DynamicSymbol(table.CLASSES()), table.NIL()
            )
        )
    }

    @kotlin.Throws(GenyrisException::class)
    fun checkTooFewArgumentCount(arguments: Array<Exp?>) {
        if (arguments.size < this.numberOfRequiredArguments) {
            throw GenyrisException(
                ("Too few arguments supplied to proc: " + this.name
                        + ". Args were: " + argsToString(arguments))
            )
        }
    }

    @kotlin.Throws(GenyrisException::class)
    open fun checkTooManyArgumentCount(arguments: Array<Exp?>) {
        if (!_restArgs && arguments.size > this.numberOfRequiredArguments) {
            throw GenyrisException(
                ("Too many arguments supplied to proc: "
                        + this.name + ". Args were: " + argsToString(arguments))
            )
        }
    }

    private fun argsToString(arguments: Array<Exp?>): StringBuffer {
        val args = StringBuffer("(")
        for (i in arguments.indices) {
            args.append(arguments[i].toString() + " ")
        }
        args.append(")")
        return args
    }

    override fun getPrintableFrame(NIL: Exp?): Exp {
        val body = getBody(NIL)
        var location = NIL
        if (body is PairSource) {
            val source = body
            location = Pair.Companion.cons2(
                Bignum(source.lineNumber), StrinG(
                    source.filename
                ), NIL
            )
        }
        val frame: Exp = Pair.Companion.cons(StrinG(toString()), location)
        return frame
    }
}