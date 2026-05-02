package org.genyris.java

import org.genyris.core.Constants
import org.genyris.core.Exp
import org.genyris.core.PrefixSymbol
import org.genyris.exception.GenyrisException
import org.genyris.interp.ApplicableFunction
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter


class ToGenyrisFunction(interp: Interpreter) :
    ApplicableFunction(interp, PrefixSymbol(Constants.PREFIX_JAVA, "toGenyris", "java"), true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>,
        env: Environment
    ): Exp? {
        return JavaUtils.javaToGenyris(env, (arguments[0] as JavaWrapper).getValue())
    }
}
