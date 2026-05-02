package org.genyris.io

import org.genyris.core.Exp
import org.genyris.exception.GenyrisException
import org.genyris.interp.*

class ReadFunction(interp: Interpreter) : ApplicableFunction(interp, "read", true) {
    private val input: InStream?
    private var parser: Parser? = null

    init {
        input = UngettableInStream(
            ConvertEofInStream(
                IndentStream(
                    UngettableInStream(_interp.getInput()), true
                )
            )
        )
    }

    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>,
        envForBindOperations: Environment?
    ): Exp? {
        //
        // (read true) gets line numbers via PairSource objects
        //
        if (arguments.size == 0) {
            parser = Parser(_interp.getSymbolTable(), input)
        } else if (arguments.size == 1) {
            if (!arguments[0]!!.isNil()) {
                parser = ParserSource(_interp.getSymbolTable(), input)
            } else {
                throw GenyrisException("nil argument to read.")
            }
        } else {
            throw GenyrisException(
                "too many arguments to read: "
                        + arguments.toString()
            )
        }
        parser!!.setUsualPrefixes(_interp)
        return parser!!.read(envForBindOperations)
    }

    companion object {
        @kotlin.Throws(UnboundException::class, GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindGlobalProcedureInstance(ReadFunction(interpreter))
        }
    }
}
