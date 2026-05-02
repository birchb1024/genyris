// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io.pipe

import org.genyris.core.*
import org.genyris.exception.GenyrisException
import org.genyris.interp.*
import org.genyris.io.readerstream.ReaderStream
import org.genyris.io.writerstream.WriterStream
import java.io.IOException
import java.io.PipedReader
import java.io.PipedWriter
import kotlin.collections.HashMap
import kotlin.collections.MutableIterator
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet

class Pipe(var name: String) : Atom() {
    var pipeout: PipedWriter
    var pipein: PipedReader? = null

    init {
        pipeout = PipedWriter()
        try {
            pipein = PipedReader(pipeout)
        } catch (e: IOException) {
            throw GenyrisException(e.getMessage())
        }
    }

    class PipeOpenMethod(interp: Interpreter?) : AbstractMethod(interp, "open") {
        @kotlin.jvm.Synchronized
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment?): Exp? {
            checkArguments(arguments, 1)
            val name: String = getPipeName(arguments[0])
            if (sharedPipeTable.containsKey(name)) {
                return sharedPipeTable.get(name) as Exp?
            } else {
                val newpipe = Pipe(name)
                sharedPipeTable.put(name, newpipe)
                return newpipe
            }
        }
    }

    class PipeInputMethod(interp: Interpreter?) : AbstractMethod(interp, "input") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>?, env: Environment): Exp {
            checkArguments(arguments, 0)
            val self: Pipe = getSelfPipe(this, env)
            return ReaderStream(self.pipein, self.toString())
        }
    }

    class PipeOutputMethod(interp: Interpreter?) : AbstractMethod(interp, "output") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>?, env: Environment): Exp {
            checkArguments(arguments, 0)
            val self: Pipe = getSelfPipe(this, env)
            return WriterStream(self.pipeout)
        }
    }

    class PipeDeleteMethod(interp: Interpreter?) : AbstractMethod(interp, "delete") {
        @kotlin.jvm.Synchronized
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment?): Exp? {
            checkArguments(arguments, 1)
            val name: String = getPipeName(arguments[0])
            if (sharedPipeTable.containsKey(name)) {
                val pipe = sharedPipeTable.get(name) as Pipe
                try {
                    pipe.pipeout.close()
                } catch (ignore: IOException) {
                }
                sharedPipeTable.remove(name)
                return TRUE
            } else {
                return NIL
            }
        }
    }

    class PipeListMethod(interp: Interpreter?) : AbstractMethod(interp, "list") {
        @kotlin.jvm.Synchronized
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>?, env: Environment?): Exp? {
            val names: MutableSet<*> = sharedPipeTable.keySet()
            val iter: MutableIterator<*> = names.iterator()
            var result: Exp? = NIL
            while (iter.hasNext()) {
                result = Pair(StrinG(iter.next() as String?), result)
            }
            return result
        }
    }

    @kotlin.Throws(GenyrisException::class)
    override fun acceptVisitor(guest: Visitor) {
        guest.visitPipe(this)
    }

    @kotlin.Throws(GenyrisException::class)
    override fun eval(env: Environment?): Exp {
        return this
    }

    override fun getBuiltinClassSymbol(table: Internable): Symbol? {
        return table.PIPE()
    }

    override fun toString(): String {
        return "[Pipe: " + name + "]"
    }

    override fun compareTo(o: Any?): Int {
        return if (this === o) 0 else 1
    }

    companion object {
        var sharedPipeTable: MutableMap<*, *> = HashMap<Any?, Any?>()

        @kotlin.Throws(GenyrisException::class)
        fun getPipeName(argument: Exp?): String {
            if (!(argument is StrinG || argument is Symbol || argument is Bignum)) {
                throw GenyrisException("pipe name " + argument + " not String, Symbol or Bignum")
            }
            return argument.toString()
        }


        @kotlin.Throws(GenyrisException::class)
        fun getSelfPipe(m: AbstractMethod, env: Environment): Pipe { // Go style
            m.getSelf(env)
            if (m._self !is Pipe) {
                throw GenyrisException(".input method called on non-Pipe " + m._self)
            }
            return m._self as Pipe
        }

        @kotlin.Throws(UnboundException::class, GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindMethodInstance("Pipe", PipeOpenMethod(interpreter))
            interpreter.bindMethodInstance("Pipe", PipeInputMethod(interpreter))
            interpreter.bindMethodInstance("Pipe", PipeOutputMethod(interpreter))
            interpreter.bindMethodInstance("Pipe", PipeDeleteMethod(interpreter))
            interpreter.bindMethodInstance("Pipe", PipeListMethod(interpreter))
        }
    }
}
