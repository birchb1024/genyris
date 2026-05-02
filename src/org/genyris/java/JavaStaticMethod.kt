package org.genyris.java

import org.genyris.core.Exp
import org.genyris.core.Internable
import org.genyris.core.Symbol
import org.genyris.exception.GenyrisException
import org.genyris.interp.ApplicableFunction
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class JavaStaticMethod(
    interp: Interpreter, name: String?, protected var method: Method,
    protected var params: Array<Class<*>?>
) : ApplicableFunction(interp, name, true) {
    fun getBuiltinClassSymbol(table: Internable): Symbol? {
        return table.JAVASTATICMETHOD()
    }

    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>?, env: Environment): Exp? {
        val args = JavaUtils.toJavaArray(params, arguments, env)
        try {
            method.setAccessible(true)
            val rawResult = method.invoke(null, *args)
            return JavaUtils.javaToGenyris(env, rawResult)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            throw GenyrisException(
                ("Java " + this + " " + e.getClass().getName() + " "
                        + e.getMessage())
            )
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            throw GenyrisException(
                ("Java " + this + " " + e.getClass().getName() + " "
                        + e.getMessage())
            )
        } catch (e: InvocationTargetException) {
            e.getCause().printStackTrace()
            throw GenyrisException(
                ("Java " + this + " "
                        + e.getCause().getClass().getName() + " "
                        + e.getCause().getMessage())
            )
        }
    }

    override fun toString(): String {
        return "JavaStaticMethod " + getName()
    }
}
