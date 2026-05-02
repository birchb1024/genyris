package org.genyris.java

import org.genyris.core.Constants
import org.genyris.core.Exp
import org.genyris.core.PrefixSymbol
import org.genyris.core.StrinG
import org.genyris.exception.GenyrisException
import org.genyris.interp.ApplicableFunction
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter


class ToJavaFunction(interp: Interpreter) :
    ApplicableFunction(interp, PrefixSymbol(Constants.PREFIX_JAVA, "toJava", "java"), true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>,
        env: Environment
    ): Exp {
        val types = arrayOf<Class<*>?>(StrinG::class.java)
        checkArgumentTypes(types, arguments)
        val klassName: String? = (arguments[0] as StrinG).toString()
        val klass: Class<*>?
        try {
            klass = Class.forName(klassName)
        } catch (e: ClassNotFoundException) {
            throw GenyrisException("ClassNotFoundException: " + e.getMessage())
        }

        return JavaUtils.wrapJavaObject(env, JavaUtils.convertToJava(klass, arguments[1], env))
    }
}
