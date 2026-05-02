package org.genyris.java

import org.genyris.core.Exp
import org.genyris.core.Internable
import org.genyris.core.Symbol
import org.genyris.exception.GenyrisException
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

class JavaMethod(
    interp: Interpreter?, name: String?, protected var method: Method,
    protected var params: Array<Class<*>?>
) : AbstractJavaMethod(interp, name) {
    fun getBuiltinClassSymbol(table: Internable): Symbol? {
        return table.JAVAMETHOD()
    }

    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>?,
        envForBindOperations: Environment
    ): Exp? {
        val `object` = getSelfJava(envForBindOperations).getValue()
        val javaArgsArray = JavaUtils.toJavaArray(params, arguments, envForBindOperations)
        try {
            method.setAccessible(true)
            val rawResult = method.invoke(`object`, *javaArgsArray)
            return JavaUtils.javaToGenyris(envForBindOperations, rawResult)
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
        return "JavaMethod " + getName()
    }
}
