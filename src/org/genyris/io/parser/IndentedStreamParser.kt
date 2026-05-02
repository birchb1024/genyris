package org.genyris.io.parser

import org.genyris.core.*
import org.genyris.exception.GenyrisException
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import org.genyris.interp.UnboundException
import org.genyris.io.*
import org.genyris.io.readerstream.ReaderStream

class IndentedStreamParser : StreamParser {
    constructor(interp: Interpreter, reader: ReaderStream, source: Boolean) {
        _input = mkIndentedStream(reader.getInStream())
        if (source) {
            _parser = ParserSource(interp.getSymbolTable(), _input)
        } else {
            _parser = Parser(interp.getSymbolTable(), _input)
        }
    }

    constructor(interp: Interpreter, script: StrinG) {
        _input = mkIndentedStream(StringInStream(script.toString()))
        _parser = interp.newParser(_input)
    }

    @kotlin.Throws(GenyrisException::class)
    override fun acceptVisitor(guest: Visitor) {
        guest.visitExpWithEmbeddedClasses(this)
    }

    override fun getBuiltinClassSymbol(table: Internable): Symbol? {
        return table.INDENTPARSER()
    }

    override fun toString(): String {
        return "<IndentedStreamParser>"
    }

    class NewMethod(interp: Interpreter?) : AbstractParserMethod(interp, "new") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment?): Exp {
            if (arguments[0] is ReaderStream) {
                var source = false
                if (arguments.size == 2) {
                    source = !arguments[1]!!.isNil()
                }
                return IndentedStreamParser(
                    _interp, (arguments[0] as ReaderStream?)!!,
                    source
                )
            } else if (arguments[0] is StrinG) {
                return IndentedStreamParser(_interp, arguments[0] as StrinG)
            } else {
                throw GenyrisException("Argument to new method of IndentedStreamParser is not Reader or String")
            }
        }
    }

    override fun compareTo(o: Any?): Int {
        return if (this === o) 0 else 1
    }

    companion object {
        private fun mkIndentedStream(inStream: InStream?): InStream {
            return UngettableInStream(
                ConvertEofInStream(
                    IndentStream(
                        UngettableInStream(inStream), true
                    )
                )
            )
        }

        @kotlin.Throws(UnboundException::class, GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindMethodInstance(
                Constants.INDENTEDPARSER,
                NewMethod(interpreter)
            )
        }
    }
}
