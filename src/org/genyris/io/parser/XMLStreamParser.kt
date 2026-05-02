package org.genyris.io.parser

import org.genyris.core.*
import org.genyris.exception.GenyrisException
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import org.genyris.interp.UnboundException
import org.genyris.io.StringInStream
import org.genyris.io.UngettableInStream
import org.genyris.io.readerstream.ReaderStream

class XMLStreamParser : StreamParser {
    private val _optionQname: Boolean

    constructor(interp: Interpreter, reader: ReaderStream, optionQname: Boolean) {
        _optionQname = optionQname
        _input = UngettableInStream(reader.getInStream())
        _parser = ParserXML(interp.getSymbolTable(), _input)
    }

    constructor(interp: Interpreter, script: StrinG, optionQname: Boolean) {
        _optionQname = optionQname
        _input = UngettableInStream(StringInStream(script.toString()))
        _parser = ParserXML(interp.getSymbolTable(), _input)
    }

    @kotlin.Throws(GenyrisException::class)
    override fun acceptVisitor(guest: Visitor) {
        guest.visitExpWithEmbeddedClasses(this)
    }

    override fun toString(): String {
        return "<XMLStreamParser>"
    }

    override fun getBuiltinClassSymbol(table: Internable): Symbol? {
        return table.XMLPARSER()
    }

    class NewMethod(interp: Interpreter?) : AbstractParserMethod(interp, "new") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment?): Exp {
            var optionQname = true
            if (arguments.size == 2) {
                if (arguments[0]!!.isNil()) {
                    optionQname = false
                }
            }
            if (arguments[0] is ReaderStream) {
                return XMLStreamParser(_interp, arguments[0] as ReaderStream, optionQname)
            } else if (arguments[0] is StrinG) {
                return XMLStreamParser(_interp, arguments[0] as StrinG, optionQname)
            } else {
                throw GenyrisException("Argument to new method of XMLStreamParser is not Reader or String")
            }
        }
    }

    override fun compareTo(o: Any?): Int {
        return if (this === o) 0 else 1
    }

    companion object {
        @kotlin.Throws(UnboundException::class, GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindMethodInstance(Constants.XMLPARSER, NewMethod(interpreter))
        }
    }
}
