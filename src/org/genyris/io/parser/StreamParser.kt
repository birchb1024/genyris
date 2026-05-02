// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io.parser

import org.genyris.core.Atom
import org.genyris.core.Constants
import org.genyris.core.Exp
import org.genyris.exception.GenyrisException
import org.genyris.interp.*
import org.genyris.io.InStream
import org.genyris.io.Parser

abstract class StreamParser : Atom() {
    protected var _input: InStream? = null
    protected var _parser: Parser? = null

    @kotlin.Throws(GenyrisException::class)
    override fun eval(env: Environment?): Exp {
        return this
    }

    @kotlin.Throws(GenyrisException::class)
    fun close() {
        _input!!.close()
    }

    class PrefixMethod(interp: Interpreter?) : AbstractParserMethod(interp, "namespace") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment): Exp? {
            checkArguments(arguments, 2)
            getSelfParser(env)._parser!!.addPrefix(_interp, arguments[0].toString(), arguments[1].toString())
            return NIL
        }
    }

    abstract class AbstractParserMethod(interp: Interpreter?, name: String?) : AbstractMethod(interp, name) {
        @kotlin.Throws(GenyrisException::class)
        protected fun getSelfParser(env: Environment): StreamParser {
            getSelf(env)
            if (_self !is StreamParser) {
                throw GenyrisException(
                    "Non-Parser passed to a Parser method."
                )
            } else {
                return _self as StreamParser
            }
        }
    }

    class ReadMethod(interp: Interpreter?) : AbstractParserMethod(interp, "read") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>?, env: Environment): Exp? {
            val self = getSelfParser(env)
            return self._parser!!.read(env)
        }
    }

    class CloseMethod(interp: Interpreter?) : AbstractParserMethod(interp, "close") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>?, env: Environment): Exp? {
            getSelfParser(env).close()
            return NIL
        }
    }

    companion object {
        @kotlin.Throws(UnboundException::class, GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindMethodInstance(Constants.ABSTRACTPARSER, ReadMethod(interpreter))
            interpreter.bindMethodInstance(Constants.ABSTRACTPARSER, CloseMethod(interpreter))
            interpreter.bindMethodInstance(Constants.ABSTRACTPARSER, PrefixMethod(interpreter))
        }
    }
}
