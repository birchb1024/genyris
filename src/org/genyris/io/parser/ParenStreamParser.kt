package org.genyris.io.parser

import org.genyris.core.*
import org.genyris.exception.GenyrisException
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import org.genyris.interp.UnboundException
import org.genyris.io.Parser
import org.genyris.io.StringInStream
import org.genyris.io.UngettableInStream
import org.genyris.io.readerstream.ReaderStream

class ParenStreamParser : StreamParser {
    constructor(interp: Interpreter, reader: ReaderStream) {
        _input = UngettableInStream(reader.getInStream())
        _parser = interp.newParser(_input) //TODO two ways to do the same thing
    }

    constructor(interp: Interpreter, script: StrinG) {
        _input = UngettableInStream(StringInStream(script.toString()))
        _parser = Parser(interp.getSymbolTable(), _input) //TODO two ways to do the same thing
    }

    @kotlin.Throws(GenyrisException::class)
    override fun acceptVisitor(guest: Visitor) {
        guest.visitExpWithEmbeddedClasses(this)
    }

    override fun toString(): String {
        return "<ParenStreamParser>"
    }

    override fun getBuiltinClassSymbol(table: Internable): Symbol? {
        return table.PARENPARSER()
    }

    class NewMethod(interp: Interpreter?) : AbstractParserMethod(interp, "new") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment?): Exp {
            if (arguments[0] is ReaderStream) {
                return ParenStreamParser(_interp, arguments[0] as ReaderStream)
            } else if (arguments[0] is StrinG) {
                return ParenStreamParser(_interp, arguments[0] as StrinG)
            } else {
                throw GenyrisException("Argument to new method of ParenStreamParser is not Reader or String")
            }
        }
    }

    override fun compareTo(o: Any?): Int {
        return if (this === o) 0 else 1
    }

    companion object {
        @kotlin.Throws(UnboundException::class, GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindMethodInstance(Constants.PARENPARSER, NewMethod(interpreter))
        }
    }
}
