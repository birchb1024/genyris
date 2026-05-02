package org.genyris.java.swing

import org.genyris.core.Constants
import org.genyris.core.Exp
import org.genyris.core.PrefixSymbol
import org.genyris.exception.GenyrisException
import org.genyris.interp.ApplicableFunction
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import org.genyris.java.JavaWrapper


class ListenerFunction(interp: Interpreter) :
    ApplicableFunction(interp, PrefixSymbol(Constants.PREFIX_JAVA, "actionListener", "sys"), true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>,
        env: Environment?
    ): Exp {
        val types = arrayOf<Class<*>?>(Closure::class.java)
        this.checkArgumentTypes(types, arguments)
        return JavaWrapper(GenyrisActionListener(arguments[0] as Closure?, env))
    }
}
