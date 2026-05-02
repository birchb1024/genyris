// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.os

import org.genyris.core.*
import org.genyris.core.Dictionary
import org.genyris.exception.AccessException
import org.genyris.exception.GenyrisException
import org.genyris.interp.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

class ExecMethod(interp: Interpreter) : AbstractMethod(interp, "exec") {
    private val ListOfLinesClazz: Dictionary?

    init {
        ListOfLinesClazz = interp.lookupGlobalFromString(Constants.LISTOFLINES) as Dictionary?
    }

    fun error(msg: String?): GenyrisException {
        return GenyrisException(_name + ": " + msg)
    }

    @kotlin.Throws(GenyrisException::class)
    private fun toStringArray(args: Exp?): Array<String?> {
        var args = args
        val result: MutableList<String?> = ArrayList<String?>()
        while (args !== this.NIL) {
            if (args !is Pair) {
                throw error("not a list in arguments " + args)
            }
            result.add(args.car().toString())
            args = args.cdr()
        }
        return result.toArray<String?>(arrayOfNulls<String>(0))
    }

    @kotlin.Throws(GenyrisException::class)
    private fun toEnvStringArray(args: Exp?): Array<String?> {
        var args = args
        val result: MutableList<String?> = ArrayList<String?>()
        while (args !== this.NIL) {
            if (args !is Pair) {
                throw error("not a list in arguments " + args)
            }
            if (args.car() !is Pair) {
                throw error("not a list in arguments " + args.car())
            }
            result.add(args.car().car().toString() + "=" + args.car().cdr().toString())
            args = args.cdr()
        }
        return result.toArray<String?>(arrayOfNulls<String>(0))
    }

    @kotlin.Throws(GenyrisException::class)
    private fun toStringArray(expArray: Array<Exp?>): Array<String?> {
        val result = arrayOfNulls<String>(expArray.size)
        for (i in expArray.indices) {
            result[i] = expArray[i].toString()
        }
        return result
    }

    enum class CallType {
        Error,
        AllStrings,
        ListofStringsNoEnv,
        ListofStringsWithEnv
    }

    @kotlin.Throws(GenyrisException::class)
    fun isListAllSameClass(klass: Class<*>, args: Exp?): Boolean {
        if (args === this.NIL) {
            return true
        }
        if (args !is Pair) {
            throw error("not a list in arguments " + args)
        }
        if (!klass.isInstance(args.car())) {
            return false
        }
        return isListAllSameClass(klass, args.cdr())
    }

    @kotlin.Throws(GenyrisException::class)
    fun isListSimpleAssoc(args: Exp?): Boolean {
        if (args === this.NIL) {
            return true
        }
        if (args !is Pair) {
            throw error("not a list in environment " + args)
        }
        if (args.car() !is Pair) {
            throw error("not a pair in environment " + args.car())
        }
        if (args.car().car() !is StrinG) {
            throw error("not a String in environment " + args.car())
        }
        return isListSimpleAssoc(args.cdr())
    }

    @kotlin.Throws(GenyrisException::class)
    fun classifyArguments(arguments: Array<Exp?>): CallType? {
        if (arguments.size == 0) {
            throw error("Insufficient arguments " + arguments)
        }
        if (isAllSameClass(StrinG::class.java, arguments)) {
            return CallType.AllStrings
        }
        if (arguments.size == 1 && isAllSameClass(Pair::class.java, arguments)) {
            if (isListAllSameClass(StrinG::class.java, arguments[0])) {
                return CallType.ListofStringsNoEnv
            }
        }
        if (arguments.size == 2 && arguments[1] === NIL) {
            val tmp = arrayOf<Exp?>(arguments[0])
            return classifyArguments(tmp)
        }
        if (arguments.size == 2 && isAllSameClass(Pair::class.java, arguments)) {
            if (arguments[1] === NIL) {
                val tmp = arrayOf<Exp?>(arguments[0])
                return classifyArguments(tmp)
            }
            if (isListAllSameClass(StrinG::class.java, arguments[0]) && isListAllSameClass(
                    Pair::class.java,
                    arguments[1]
                )
            ) {
                if (!isListSimpleAssoc(arguments[1])) {
                    throw error("Environment variables not strings " + arguments[1])
                }
                return CallType.ListofStringsWithEnv
            }
        }
        throw error("Unknown argument structure " + Arrays.toString(arguments))
    }

    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(proc: Closure?, arguments: Array<Exp?>, env: Environment?): Exp {
        val child: Process?
        var lines: Exp? = NIL
        var errors: Exp? = NIL
        try {
            when (classifyArguments(arguments)) {
                CallType.ListofStringsWithEnv -> {
                    val cmdarray = toStringArray(arguments[0])
                    val envarray = toEnvStringArray(arguments[1])
                    val envList: MutableList<String?> = updateEnvironment(envarray)
                    child = Runtime.getRuntime().exec(cmdarray, envList.toArray<String?>(arrayOfNulls<String>(0)))
                    lines = convertResultToListOfLines(child.getInputStream())
                    errors = convertResultToListOfLines(child.getErrorStream())
                }

                CallType.ListofStringsNoEnv -> {
                    val cmdarray = toStringArray(arguments[0])
                    child = Runtime.getRuntime().exec(cmdarray)
                    lines = convertResultToListOfLines(child.getInputStream())
                    errors = convertResultToListOfLines(child.getErrorStream())
                }

                CallType.AllStrings -> {
                    val args = toStringArray(arguments)
                    child = Runtime.getRuntime().exec(args)
                    lines = convertResultToListOfLines(child.getInputStream())
                    errors = convertResultToListOfLines(child.getErrorStream())
                }

                else -> throw error("Unknown argument structure " + Arrays.toString(arguments))
            }
        } catch (e: IOException) {
            throw error("failed: " + e.getMessage())
        }
        try {
            if (child.waitFor() != 0) {
                throw error(Pair(lines, errors).toString())
            }
        } catch (e: InterruptedException) {
            throw error("failed:" + e.getMessage().toString())
        }
        return Pair(lines, errors)
    }

    @kotlin.Throws(GenyrisException::class)
    private fun convertResultToListOfLines(inputStream: InputStream): Exp {
        var read: InputStreamReader? = null
        var buf: BufferedReader? = null
        read = InputStreamReader(inputStream)
        buf = BufferedReader(read)
        var lines: Exp = NIL
        var tail: Exp = NIL

        var line: String?
        try {
            while ((buf.readLine().also { line = it }) != null) {
                if (lines === NIL) {
                    lines = Pair(StrinG(line), NIL)
                    tail = lines
                } else {
                    tail.setCdr(Pair(StrinG(line), NIL))
                    tail = tail.cdr()
                }
            }
        } catch (e: AccessException) {
            throw error(" failed, " + e.getMessage())
        } catch (e: IOException) {
            throw error(" failed, " + e.getMessage())
        }
        if (inputStream != null) try {
            inputStream.close()
        } catch (ignore: IOException) {
        }
        if (read != null) try {
            read.close()
        } catch (ignore: IOException) {
        }
        if (buf != null) try {
            buf.close()
        } catch (ignore: IOException) {
        }
        lines.addClass(ListOfLinesClazz)
        return lines
    }

    companion object {
        fun isAllSameClass(klass: Class<*>, args: Array<Exp?>): Boolean {
            for (i in args.indices) {
                if (!klass.isInstance(args[i])) {
                    return false
                }
            }
            return true
        }

        private fun updateEnvironment(envarray: Array<String?>): MutableList<String?> {
            // BEGIN Eeewww...
            val currentEnv: MutableMap<String?, String?> =
                HashMap<String?, String?>(System.getenv()) // getEnv() returns non-modifiable map, but HashMap is mutable
            // insert or update current variables
            for (i in envarray.indices) {
                val toke: Array<String?> = envarray[i].split("=")
                if (toke.size == 1) {
                    currentEnv.put(toke[0], "")
                }
                if (toke.size > 1) {
                    currentEnv.put(toke[0], toke[1])
                }
            }
            val envList: MutableList<String?> = asListofKeqV(currentEnv)
            // END Eeewww...
            return envList
        }

        private fun asListofKeqV(M: MutableMap<String?, String?>): MutableList<String?> {
            val envList: MutableList<String?> = ArrayList<String?>()
            for (entry in M.entrySet()) {
                envList.add(entry.getKey() + "=" + entry.getValue())
            }
            return envList
        }

        @kotlin.Throws(UnboundException::class, GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindMethodInstance(Constants.OS, ExecMethod(interpreter))
        }
    }
}
