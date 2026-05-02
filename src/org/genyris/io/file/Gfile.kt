// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io.file

import org.genyris.core.Exp
import org.genyris.core.Pair
import org.genyris.core.StrinG
import org.genyris.core.Symbol
import org.genyris.exception.GenyrisException
import org.genyris.interp.*
import org.genyris.io.readerstream.ReaderStream
import org.genyris.io.writerstream.WriterStream
import java.io.*
import java.nio.file.Paths
import java.util.*

object Gfile {
    @kotlin.Throws(GenyrisException::class)
    fun open(filename: StrinG, mode: Symbol): Exp? {
        if (mode.toString() == "read") {
            try {
                return ReaderStream(
                    InputStreamReader(
                        FileInputStream(filename.toString())
                    ), filename.toString()
                )
            } catch (e: FileNotFoundException) {
                throw GenyrisException(e.getMessage())
            }
        } else if (mode.toString() == "write") {
            try {
                return WriterStream(
                    OutputStreamWriter(
                        FileOutputStream(filename.toString())
                    )
                )
            } catch (e: FileNotFoundException) {
                throw GenyrisException(e.getMessage())
            }
        }
        return null
    }

    @kotlin.Throws(UnboundException::class, GenyrisException::class)
    fun bindFunctionsAndMethods(interpreter: Interpreter) {
        interpreter.bindMethodInstance(
            "File", FileOpenMethod(
                interpreter
            )
        )
        interpreter.bindMethodInstance(
            "File", FileListDir(
                interpreter
            )
        )
        interpreter.bindMethodInstance(
            "File", FileAbsPath(
                interpreter
            )
        )
        interpreter.bindMethodInstance("File", IsDir(interpreter))
    }

    class FileOpenMethod(interp: Interpreter?) : AbstractMethod(interp, "static-open") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment?): Exp? {
            checkMinArguments(arguments, 2)
            checkArgumentTypes(types, arguments)
            return Gfile.open((arguments[0] as StrinG?)!!, (arguments[1] as org.genyris.core.Symbol?)!!)
        }

        companion object {
            private val types = arrayOf<Class<*>?>(StrinG::class.java, Symbol::class.java)
        }
    }

    class FileListDir(interp: Interpreter?) : AbstractMethod(interp, "static-list-dir") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment?): Exp? {
            var retval: Exp? = NIL
            checkArguments(arguments, 1, 2)
            if (arguments.size == 1) {
                checkArgumentTypes(types, arguments)
            }
            val dirname = arguments[0].toString()
            val dir = File(dirname)
            var fullPath = false
            if (arguments.size > 1) {
                checkArgumentTypes(fullPathTypes, arguments)
                if (arguments[1] === _interp.intern("path")) {
                    fullPath = true
                }
            }
            val children = dir.list()

            if (children == null) {
                throw GenyrisException(
                    "File.static-list-dir: failed - either does not exist or is not a directory "
                            + arguments[0]
                )
            } else {
                Arrays.sort(children)
                for (i in children.indices.reversed()) {
                    val child = (if (fullPath) Paths.get(dirname, children[i]).toString() else children[i])
                    retval = Pair(StrinG(child), retval)
                }
            }
            return retval
        }

        companion object {
            private val types = arrayOf<Class<*>?>(StrinG::class.java)
            private val fullPathTypes = arrayOf<Class<*>?>(StrinG::class.java, Symbol::class.java)
        }
    }

    class IsDir(interp: Interpreter?) : AbstractMethod(interp, "static-is-dir?") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment?): Exp? {
            checkMinArguments(arguments, 1)
            checkArgumentTypes(types, arguments)
            val dir = File(arguments[0].toString())
            val isDir = dir.isDirectory()

            return (if (isDir) TRUE else NIL)
        }

        companion object {
            private val types = arrayOf<Class<*>?>(StrinG::class.java)
        }
    }

    class FileAbsPath(interp: Interpreter?) : AbstractMethod(interp, "static-abs-path") {
        @kotlin.Throws(GenyrisException::class)
        override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment?): Exp {
            checkMinArguments(arguments, 1)
            checkArgumentTypes(types, arguments)
            val file = File(arguments[0].toString())

            try {
                return StrinG(file.getCanonicalPath())
            } catch (e: IOException) {
                throw GenyrisException("static-abs-path: " + e.getMessage())
            }
        }

        companion object {
            private val types = arrayOf<Class<*>?>(StrinG::class.java)
        }
    }
}
