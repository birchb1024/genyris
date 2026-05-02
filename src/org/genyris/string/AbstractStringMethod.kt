// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.string

import org.genyris.core.Constants
import org.genyris.core.StrinG
import org.genyris.exception.GenyrisException
import org.genyris.interp.AbstractMethod
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import org.genyris.interp.UnboundException

abstract class AbstractStringMethod(interp: Interpreter?, name: String?) : AbstractMethod(interp, name) {
    @kotlin.Throws(GenyrisException::class)
    protected fun getSelfString(env: Environment): StrinG {
        getSelf(env)
        if (_self !is StrinG) {
            throw GenyrisException("Non-String passed to getSelfString: " + _self.toString())
        } else {
            val theString = _self as StrinG
            return theString
        }
    }

    companion object {
        @kotlin.Throws(UnboundException::class, GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindMethodInstance(Constants.STRING, SplitMethod(interpreter))
            interpreter.bindMethodInstance(Constants.STRING, ConcatMethod(interpreter))
            interpreter.bindMethodInstance(Constants.STRING, MatchMethod(interpreter))
            interpreter.bindMethodInstance(Constants.STRING, RegexMethod(interpreter))
            interpreter.bindMethodInstance(Constants.STRING, LengthMethod(interpreter))
            interpreter.bindMethodInstance(Constants.STRING, ToLowerCaseMethod(interpreter))
            interpreter.bindMethodInstance(Constants.STRING, StringFormatMethod(interpreter))
            interpreter.bindMethodInstance(Constants.STRING, ReplaceMethod(interpreter))
            interpreter.bindMethodInstance(Constants.STRING, ToIntsMethod(interpreter))
            interpreter.bindMethodInstance(Constants.STRING, FromIntsMethod(interpreter))
            interpreter.bindMethodInstance(Constants.STRING, SliceMethod(interpreter))
            interpreter.bindMethodInstance(Constants.STRING, JoinMethod(interpreter))
            interpreter.bindMethodInstance(Constants.STRING, FromJSONMethod(interpreter))
        }
    }
}
