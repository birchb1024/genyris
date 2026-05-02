// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp

import org.genyris.core.*
import org.genyris.exception.GenyrisException
import org.genyris.format.Formatter
import org.genyris.format.IndentedFormatter
import org.genyris.io.InStream
import org.genyris.io.JlineStdioInStream
import org.genyris.load.SourceLoader
import java.io.IOException
import java.io.PrintWriter
import java.io.Writer
import java.lang.String
import java.nio.file.Paths
import java.util.*
import kotlin.Array
import kotlin.Int
import kotlin.toString

class ClassicReadEvalPrintLoop {
    // TODO DRY
    private var _interpreter: Interpreter? = null

    private fun runWithJline(args: Array<String?>): Int {
        try {
            val console: JlineStdioInStream = JlineStdioInStream.Companion.knew()
            _interpreter = Interpreter(
                console as InStream?,
                console.getOutput()
            )
            _interpreter!!.init(false, ".")
            Companion.setArgs(args, _interpreter!!)
            console.setInterpreter(_interpreter)
            _interpreter!!
                .evalStringInGlobalEnvironment("sys:read-eval-print-loop")
            return 0
        } catch (e1: GenyrisException) {
            e1.printStackTrace()
            return -1
        }
    }

    companion object {
        @kotlin.jvm.JvmStatic
        fun main(args: Array<String>) {
            var result = 0
            try {
                if (args.size == 0) {
                    result = ClassicReadEvalPrintLoop().runWithJline(args)
                } else {
                    if (args[0] == "-eval") {
                        val expression = StringBuffer("")
                        for (i in 1..<args.size) {
                            expression.append(args[i])
                            expression.append(" ")
                        }
                        expression.append("\n\n")
                        evalString(expression.toString(), "-eval")
                        result = 0
                    } else if (args[0] == "-") {
                        result = Companion.evalFileWithArguments(args[0], args)
                    } else if (args[0].startsWith("-")) {
                        usage()
                        result = -1
                    } else {
                        result = Companion.evalFileWithArguments(args[0], args)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            System.exit(result)
        }

        @kotlin.Throws(IOException::class)
        private fun evalFileWithArguments(filename: String, args: Array<String?>): Int {
            val output: Writer = PrintWriter(System.out)
            try {
                val interpreter = Interpreter()
                interpreter.init(false, getContainingDirectoryPath(filename))
                interpreter.getDebugBackTrace()
                setArgs(args, interpreter)
                Runtime.getRuntime().addShutdownHook(Thread(Runnable {
                    try {
                        interpreter.evalStringInGlobalEnvironment("(*shutdown-hook*)")
                    } catch (e: GenyrisException) {
                        System.err.println("Exception in shutdown hook" + e.getMessage())
                    }
                }))
                try {
                    if (filename == "-") {
                        SourceLoader.execAndClose(
                            interpreter.getGlobalEnv(),
                            interpreter.getSymbolTable(), System.`in`, filename,
                            output
                        )
                        return 0
                    }
                    SourceLoader.loadScriptFromFile(
                        interpreter.getGlobalEnv(),
                        interpreter.getSymbolTable(), filename, output
                    )
                    return 0
                } catch (e: GenyrisException) {
                    output.write("*** Error in file " + e.getMessage() + "\n")
                    var bt = interpreter.getDebugBackTraceAsList()
                    while (bt !== interpreter.NIL) {
                        output.write("\t" + bt.car() + "\n")
                        bt = bt.cdr()
                    }
                    output.flush()
                    return -1
                }
            } catch (e: GenyrisException) {
                output.write("*** Error in file " + e.getMessage() + "\n")
                output.flush()
                return -1
            }
        }

        @kotlin.Throws(IOException::class)
        private fun evalString(script: String, name: String?) {
            val interp: Interpreter?
            val output: Writer = PrintWriter(System.out)
            val formatter: Formatter = IndentedFormatter(output, 2)
            try {
                interp = Interpreter()
                interp.init(false, name)
                val result = interp.evalStringInGlobalEnvironment(script)
                result.acceptVisitor(formatter)
                output.write(" " + Constants.COMMENTCHAR)
                formatter.printClassNames(result, interp)
                output.write("\n")
                output.flush()
                System.exit(if (result === interp.NIL) 0 else 1)
            } catch (e: GenyrisException) {
                output.write("*** Error in script: " + e.getMessage())
                output.flush()
                System.exit(-1)
            }
        }

        private fun usage() {
            System.out
                .println("Usage: genyris [-h] [-eval (expression) args...] [- args...] [filename args... ] ")
            System.exit(-1)
        }

        @kotlin.Throws(GenyrisException::class)
        private fun setArgs(args: Array<String?>, interpreter: Interpreter) {
            val argv = interpreter.intern(PrefixSymbol(Constants.GENYRIS + "system#", Constants.ARGV, "sys"))
            val argsAlist: Exp? = makeListOfStrings(interpreter.getSymbolTable().NIL(), args, 0)
            interpreter.getGlobalEnv().defineVariable(argv, argsAlist)
        }

        @kotlin.Throws(GenyrisException::class)
        fun getContainingDirectoryPath(url: String): String {
            var filename: String? = url
            try {
                if (url === "-") {
                    return "."
                }
                if (url.startsWith("jar:")) {
                    val path: Array<String?> = url.split("/")
                    val parent = Arrays.copyOfRange<String?>(path, 0, path.size - 1)
                    val result = String.join("/", Arrays.asList<kotlin.String?>(*parent))
                    return result
                }
                if (url.startsWith("file:")) {
                    filename = url.substring("file:".length())
                }
                val scriptPath = Paths.get(filename).toRealPath()
                return scriptPath.getParent().toString()
            } catch (e: IOException) {
                throw GenyrisException(e)
            }
        }

        private fun makeListOfStrings(
            NIL: Symbol?, args: Array<kotlin.String?>,
            startFrom: Int
        ): Exp? {
            var arglist: Exp? = NIL
            for (i in args.size - 1 downTo startFrom) {
                arglist = Pair(StrinG(args[i]), arglist)
            }
            return arglist
        }
    }
}
