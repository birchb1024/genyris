package org.genyris.java

import org.genyris.core.Constants
import org.genyris.core.Exp
import org.genyris.core.PrefixSymbol
import org.genyris.exception.GenyrisException
import org.genyris.interp.ApplicableFunction
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter


class ImportFunction(interp: Interpreter) :
    ApplicableFunction(interp, PrefixSymbol(Constants.PREFIX_JAVA, "import", "java"), false) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>,
        env: Environment?
    ): Exp {
        val javaClassName = arguments[0]!!.eval(env).toString()
        var genyrisClassName: String = javaClassName.substring(1 + javaClassName.lastIndexOf('.'.code))
        if (arguments.size == 3) {
            if (arguments[1].toString() != "as") {
                throw GenyrisException("java import missing 'as'.")
            }
            genyrisClassName = arguments[2].toString()
        }

        return JavaUtils.importJavaClass(_interp, genyrisClassName, env, javaClassName)
    }
}
