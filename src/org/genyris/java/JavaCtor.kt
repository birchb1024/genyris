package org.genyris.java

import org.genyris.core.Exp
import org.genyris.core.Internable
import org.genyris.core.StandardClass
import org.genyris.core.Symbol
import org.genyris.exception.GenyrisException
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException

class JavaCtor(
    interp: Interpreter?, private val genyrisClass: StandardClass?, name: String?,
    private val method: Constructor<*>, private val params: Array<Class<*>?>
) : AbstractJavaMethod(interp, name) {
    fun getBuiltinClassSymbol(table: Internable): Symbol? {
        return table.JAVACTOR()
    }

    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>?,
        env: Environment?
    ): Exp {
        val javaArgsArray = JavaUtils.toJavaArray(params, arguments, env)
        try {
            val result = JavaWrapper(
                method
                    .newInstance(*javaArgsArray)
            )
            result.addClass(genyrisClass)
            return result
        } catch (e: IllegalArgumentException) {
            throw GenyrisException(
                ("Java " + this + " " + e.getClass().getName() + " "
                        + e.getMessage())
            )
        } catch (e: IllegalAccessException) {
            throw GenyrisException(
                ("Java " + this + " " + e.getClass().getName() + " "
                        + e.getMessage())
            )
        } catch (e: InstantiationException) {
            throw GenyrisException(
                ("Java " + this + " " + e.getClass().getName() + " "
                        + e.getMessage())
            )
        } catch (e: InvocationTargetException) {
            throw GenyrisException(
                ("Java " + this + " "
                        + e.getCause().getClass().getName() + " "
                        + e.getCause().getMessage())
            )
        }
    }
}
