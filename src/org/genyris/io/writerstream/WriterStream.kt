// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io.writerstream

import org.genyris.core.*
import org.genyris.exception.GenyrisException
import org.genyris.format.*
import org.genyris.interp.AbstractMethod
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import java.io.IOException
import java.io.Writer

class WriterStream(val writer: Writer) : Atom(), Comparable<Any?> {
    @kotlin.Throws(GenyrisException::class)
    override fun acceptVisitor(guest: Visitor) {
        guest.visitExpWithEmbeddedClasses(this)
    }

    override fun toString(): String {
        return writer.toString()
    }

    override fun getBuiltinClassSymbol(table: Internable): Symbol? {
        return table.WRITER()
    }

    @kotlin.Throws(GenyrisException::class)
    fun write(ch: Char) {
        try {
            writer.write(ch.code)
        } catch (e: IOException) {
            throw GenyrisException(e.getMessage())
        }
    }

    @kotlin.Throws(GenyrisException::class)
    fun close() {
        try {
            writer.close()
        } catch (e: IOException) {
            throw GenyrisException(e.getMessage())
        }
    }

    @kotlin.Throws(GenyrisException::class)
    fun format(formatString: StrinG, offset: Int, args: Array<Exp?>, env: Environment): Exp? {
        val format = StringBuffer(formatString.toString())
        var argCounter = offset
        val escape = '%'
        try {
            var i = 0
            while (i < format.length()) {
                val ch: Char = format.charAt(i)
                if (ch != escape) {
                    writer.append(ch)
                    i++
                    continue
                }
                if ((ch == escape) && (i == format.length() - 1)) {
                    throw GenyrisException("Bad format: " + format)
                }
                val order: Char = format.charAt(i + 1)
                if (ch == escape && order == 'a') {
                    // display - TODO DRY
                    i++
                    if (argCounter > args.size) {
                        break
                    }
                    val formatter: Formatter = DisplayFormatter(this.writer)
                    if (argCounter == args.size) {
                        throw GenyrisException("Bad format: " + format + " too few real arguments.")
                    }
                    args[argCounter++]!!.acceptVisitor(formatter)
                } else if (ch == escape && (order == 'i' || order == 'I')) {
                    // write - TODO DRY
                    i++
                    if (argCounter > args.size) {
                        break
                    }
                    val formatter: Formatter =
                        IndentedFormatter(this.writer, 3, (if (Character.isUpperCase(order)) true else false))
                    if (argCounter == args.size) {
                        throw GenyrisException("Bad format: " + format + " too few real arguments.")
                    }
                    args[argCounter++]!!.acceptVisitor(formatter)
                } else if (ch == escape && (order == 's' || order == 'S')) {
                    // write - TODO DRY
                    if (argCounter > args.size) {
                        break
                    }
                    val formatter: Formatter =
                        BasicFormatter(this.writer, (if (Character.isUpperCase(order)) true else false))
                    if (argCounter == args.size) {
                        throw GenyrisException("Bad format: " + format + " too few real arguments.")
                    }
                    i++
                    args[argCounter++]!!.acceptVisitor(formatter)
                } else if (ch == escape && order == 'x') {
                    // write - TODO DRY
                    i++
                    if (argCounter > args.size) {
                        break
                    }
                    val formatter: Formatter = HTMLFormatter(this.writer)
                    if (argCounter == args.size) {
                        throw GenyrisException("Bad format: " + format + " too few real arguments.")
                    }
                    args[argCounter++]!!.acceptVisitor(formatter)
                } else if (ch == escape && order == 'u') {
                    // write - TODO DRY
                    i++
                    if (argCounter > args.size) {
                        break
                    }
                    val formatter: Formatter = UrlFormatter(this.writer)
                    if (argCounter == args.size) {
                        throw GenyrisException("Bad format: " + format + " too few real arguments.")
                    }
                    args[argCounter++]!!.acceptVisitor(formatter)
                } else if (ch == escape && order == 'n') {
                    i++
                    writer.append('\n')
                } else if (ch == escape && order == 'j') {
                    i++
                    if (argCounter > args.size) {
                        break
                    }
                    val formatter: Formatter = JSONFormatter(this.writer)
                    if (argCounter == args.size) {
                        throw GenyrisException("Bad format: " + format + " too few real arguments.")
                    }
                    args[argCounter++]!!.acceptVisitor(formatter)
                } else if (ch == escape && order == escape) {
                    i++
                    writer.append(escape)
                } else {
                    writer.append(ch)
                }
                i++
            }
            if (argCounter != args.size) {
                throw GenyrisException("Bad format: " + format + " too many real arguments.")
            }
        } catch (e: ArrayIndexOutOfBoundsException) {
            throw GenyrisException("Bad format: " + format + " " + e.getMessage())
        } catch (e: StringIndexOutOfBoundsException) {
            throw GenyrisException("Bad format: " + format + " " + e.getMessage())
        } catch (e: IOException) {
            if (e.getMessage() == null) {
                throw GenyrisException("IOException")
            }
            throw GenyrisException(e.getMessage())
        }
        return env.getNil()
    }

    abstract class AbstractWriterMethod(interp: Interpreter?, name: String?) : AbstractMethod(interp, name) {
        @kotlin.Throws(GenyrisException::class)
        protected fun getSelfWriter(env: Environment): WriterStream {
            getSelf(env)
            if (_self !is WriterStream) {
                throw GenyrisException("Non-Writer passed to a Writer method.")
            } else {
                return _self as WriterStream
            }
        }

        companion object {
            @kotlin.Throws(GenyrisException::class)
            fun bindFunctionsAndMethods(interpreter: Interpreter) {
                interpreter.bindMethodInstance(Constants.WRITER, FormatMethod(interpreter))
                interpreter.bindMethodInstance(Constants.WRITER, CloseMethod(interpreter))
                interpreter.bindMethodInstance(Constants.WRITER, FlushMethod(interpreter))
            }
        }
    }

    class FormatMethod(interp: Interpreter) : AbstractWriterMethod(
        interp,
        staticName
    ) {
        private var STDOUT: Exp? = null
        private var STDERR: Exp? = null

        init {
            try {
                STDOUT = interp.lookupGlobalFromString(Constants.STDOUT)
                STDERR = interp.lookupGlobalFromString(Constants.STDERR)
            } catch (e: GenyrisException) {
                STDOUT = null
                STDERR = null
            }
        }

        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment): Exp? {
            if (arguments.size > 0) {
                if (arguments[0] !is StrinG) {
                    throw GenyrisException("Non string passed to FormatMethod")
                }
                val self = getSelfWriter(env)
                val retval = self.format((arguments[0] as StrinG?)!!, 1, arguments, env)
                if ((self === STDOUT) or (self === STDERR)) {
                    try {
                        self.writer.flush()
                    } catch (e: IOException) {
                        throw GenyrisException(e.getMessage())
                    }
                }
                return retval
            } else {
                throw GenyrisException("Missing argument to FormatMethod")
            }
        }

        companion object {
            val staticName: String
                get() = "format"
        }
    }

    class CloseMethod(interp: Interpreter?) : AbstractWriterMethod(
        interp,
        staticName
    ) {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>?, env: Environment): Exp? {
            getSelfWriter(env).close()
            return NIL
        }

        companion object {
            val staticName: String
                get() = "close"
        }
    }

    class FlushMethod(interp: Interpreter?) : AbstractWriterMethod(
        interp,
        staticName
    ) {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>?, env: Environment): Exp? {
            getSelfWriter(env).flush()
            return NIL
        }

        companion object {
            val staticName: String
                get() = "flush"
        }
    }

    @kotlin.Throws(GenyrisException::class)
    fun flush(): Exp? {
        try {
            writer.flush()
        } catch (e: IOException) {
            throw GenyrisException(e.getMessage())
        }
        return null
    }

    @kotlin.Throws(GenyrisException::class)
    override fun eval(env: Environment?): Exp {
        return this
    }

    override fun compareTo(o: Any?): Int {
        return if (this === o) 0 else 1
    }
}
