package org.genyris.java

import org.genyris.exception.GenyrisException
import org.genyris.interp.AbstractMethod
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import org.genyris.interp.UnboundException
import org.genyris.java.swing.ListenerFunction

// Copyright 2010 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//

abstract class AbstractJavaMethod(interp: Interpreter?, name: String?) : AbstractMethod(interp, name) {
    @kotlin.Throws(GenyrisException::class)
    protected fun getSelfJava(env: Environment): JavaWrapper {
        getSelf(env)
        if (_self !is JavaWrapper) {
            return JavaWrapper(_self)
        } else {
            val theJava = _self as JavaWrapper
            return theJava
        }
    }

    companion object {
        @kotlin.Throws(UnboundException::class, GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindGlobalProcedureInstance(ImportFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(ToJavaFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(ToGenyrisFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(ListenerFunction(interpreter))
        }
    }
}
