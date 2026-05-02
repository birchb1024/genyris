// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp

import org.genyris.core.*
import org.genyris.exception.GenyrisException

abstract class ApplicableFunction {
    protected var _interp: Interpreter

    protected var NIL: SimpleSymbol?
    protected var TRUE: SimpleSymbol?
    protected var _lambda: SimpleSymbol?
    protected var _lambdam: SimpleSymbol?
    protected var _lambdaq: SimpleSymbol?
    var _nameSymbol: PrefixSymbol? = null
    protected var _name: String? = null
    val isEager: Boolean
    protected var REST: SimpleSymbol?

    @kotlin.Throws(GenyrisException::class)
    open fun toSymbol(x: Exp): Symbol? {
        if (x is Bignum || x is StrinG) {
            return _interp.intern(x.toString())
        }
        if (x is Symbol && x !is DynamicSymbol) {
            return _interp.intern(x)
        }
        throw GenyrisException("Cannot intern " + x.toString() + " " + x.getClass().getName())
    }

    constructor(interp: Interpreter, name: PrefixSymbol?, eager: Boolean) {
        _nameSymbol = name
        this.isEager = eager
        _interp = interp
        NIL = interp.getSymbolTable().NIL()
        TRUE = interp.getSymbolTable().TRUE()
        _lambda = interp.getSymbolTable().LAMBDA()
        _lambdaq = interp.getSymbolTable().LAMBDAQ()
        _lambdam = interp.getSymbolTable().LAMBDAM()
        REST = interp.getSymbolTable().REST()
    }

    constructor(interp: Interpreter, name: String?, eager: Boolean) {
        _name = name
        this.isEager = eager
        _interp = interp
        NIL = interp.getSymbolTable().NIL()
        TRUE = interp.getSymbolTable().TRUE()
        _lambda = interp.getSymbolTable().LAMBDA()
        _lambdaq = interp.getSymbolTable().LAMBDAQ()
        _lambdam = interp.getSymbolTable().LAMBDAM()
        REST = interp.getSymbolTable().REST()
    }

    @kotlin.Throws(GenyrisException::class)
    fun bindAndExecuteAux(
        proc: Closure?, arguments: Array<Exp?>,
        envForBindOperations: Environment?
    ): Exp? {
        _interp.debugStackPush(proc, arguments)
        val result = bindAndExecute(proc, arguments, envForBindOperations)
        _interp.debugStackPop()
        return result
    }

    @kotlin.Throws(GenyrisException::class)
    abstract fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>?,
        envForBindOperations: Environment?
    ): Exp?

    protected fun arrayToList(array: Array<Exp?>): Exp? {
        var expression: Exp? = NIL
        for (i in array.indices.reversed()) {
            if (array[i] is PairSource) {
                expression = PairSource.Companion.clone(array[i] as PairSource?, expression)
            } else {
                expression = Pair(array[i], expression)
            }
        }
        return expression
    }

    val name: String?
        get() {
            if (_nameSymbol != null) {
                return _nameSymbol.toString()
            }
            return _name
        }

    override fun toString(): String {
        if (_nameSymbol != null) {
            return _nameSymbol.toString()
        }
        return _name!!
    }

    @kotlin.Throws(GenyrisException::class)
    protected fun checkArguments(arguments: Array<Exp?>, exactly: Int) {
        checkArguments(arguments, exactly, exactly)
    }

    @kotlin.Throws(GenyrisException::class)
    protected fun checkMinArguments(arguments: Array<Exp?>, minimum: Int) {
        if (arguments.size < minimum) throw GenyrisException(
            ("Not enough arguments to " + this.name
                    + " was expecting at least " + minimum + ".")
        )
    }

    @kotlin.Throws(GenyrisException::class)
    protected fun checkArguments(arguments: Array<Exp?>, minimum: Int, maximum: Int) {
        if (minimum == maximum && arguments.size != maximum) {
            throw GenyrisException(
                ("Incorrect number of arguments to "
                        + this.name + ", was expecting " + maximum + " .")
            )
        }
        if (arguments.size < minimum || arguments.size > maximum) throw GenyrisException(
            ("Incorrect number of arguments to "
                    + this.name + ", was expecting between " + minimum + " and "
                    + maximum + ".")
        )
    }

    @kotlin.Throws(GenyrisException::class)
    protected fun checkArgumentTypes(types: Array<Class<*>?>, args: Array<Exp?>) {
        if (args.size < types.size) {
            throw GenyrisException(this.name + " not enough arguments.")
        }
        for (i in types.indices) {
            if (!types[i]!!.isInstance(args[i])) {
                throw GenyrisException(
                    (this.name + " expects a "
                            + types[i]!!.getName() + " at position " + i + " got <" + args[i] + "> a " + args[i].getClass()
                        .getName())
                )
            }
        }
    }

    @kotlin.Throws(GenyrisException::class)
    protected fun checkArgumentTypes(types: Array<Array<Class<*>?>?>, args: Array<Exp?>) {
        if (args.size < types.size) {
            throw GenyrisException(this.name + " not enough arguments.")
        }
        for (i in types.indices) {
            if (!Companion.isAllowed(types[i], args[i])) {
                throw GenyrisException(
                    (this.name + " expects one of "
                            + Companion.asString(types[i]!!) + " at position " + i + " got <" + args[i] + "> a " + args[i].getClass()
                        .getName())
                )
            }
        }
    }

    @kotlin.Throws(GenyrisException::class)
    protected fun checkSuppliedArgumentTypes(types: Array<Array<Class<*>?>?>, args: Array<Exp?>) {
        for (i in args.indices) {
            if (!Companion.isAllowed(types[i], args[i])) {
                throw GenyrisException(
                    (this.name + " expects one of "
                            + Companion.asString(types[i]!!) + " at position " + i + " got <" + args[i] + "> a " + args[i].getClass()
                        .getName())
                )
            }
        }
    }

    @kotlin.Throws(GenyrisException::class)
    protected fun checkFormalArgumentSyntax(formals: Exp) {
        if (formals === NIL) {
            return
        }
        if (formals !is Pair) {
            throw GenyrisException("Syntax error in " + _name + ": arguments not a list: " + formals)
        }
        var head: Exp = formals
        while (head.isPair() && head !== NIL) {
            if (head.car() === REST) {
                if (!head.cdr().isPair()) throw GenyrisException(
                    ("Syntax error in " + _name + ": &rest has no following formal argument: "
                            + formals)
                )
            }
            head = head.cdr()
        }
    }

    @kotlin.Throws(GenyrisException::class)
    fun getArg(arguments: Array<Exp?>, index: Int, klass: Class<*>, mandatory: Boolean): Exp? {
        var retval: Exp? = NIL
        if (arguments.size <= index) {
            if (mandatory) {
                throw GenyrisException("Missing argument " + klass + " at arg " + index)
            }
        } else {
            retval = arguments[index]
            if (!klass.isInstance(retval)) {
                throw GenyrisException("Expecting type " + klass + " at arg " + index)
            }
        }
        return retval
    }

    @kotlin.Throws(GenyrisException::class)
    fun getArg(arguments: Array<Exp?>, index: Int, klass: Class<*>): Exp? {
        return getArg(arguments, index, klass, false)
    }

    companion object {
        fun isAllowed(allowed: Array<Class<*>>, O: Any?): Boolean {
            var AllowedClass = false
            for (a in allowed) {
                if (a.isInstance(O)) {
                    AllowedClass = true
                    break
                }
            }
            return AllowedClass
        }

        fun asString(classes: Array<Class<*>?>): String {
            var result = classes[0].toString()
            for (i in 1..<classes.size) {
                result += ", " + classes[i]
            }
            return result
        }
    }
}
