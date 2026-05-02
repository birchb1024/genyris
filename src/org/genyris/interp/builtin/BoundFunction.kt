package org.genyris.interp.builtin

import org.genyris.core.Exp
import org.genyris.core.Symbol
import org.genyris.exception.GenyrisException
import org.genyris.interp.*

class BoundFunction(interp: Interpreter) : ApplicableFunction(interp, "bound?", true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, envForBindOperations: Environment): Exp? {
        checkArguments(arguments, 1)
        val types = arrayOf<Class<*>?>(Symbol::class.java)
        this.checkArgumentTypes(types, arguments)
        try {
            envForBindOperations.lookupVariableValue(arguments[0] as Symbol?)
        } catch (e: UnboundException) {
            return NIL
        }
        return TRUE
    }
}
