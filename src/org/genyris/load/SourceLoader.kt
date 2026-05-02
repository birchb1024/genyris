// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.load

import org.genyris.core.*
import org.genyris.exception.GenyrisException
import org.genyris.interp.ClassicReadEvalPrintLoop
import org.genyris.interp.Environment
import org.genyris.io.*
import java.io.*

object SourceLoader {
    @kotlin.Throws(GenyrisException::class)
    fun parserFactory(
        filename: String, input: Reader?,
        table: Internable?
    ): Parser {
        if (filename.endsWith(".g") || filename == "-") {
            val `is`: InStream = UngettableInStream(
                ConvertEofInStream(
                    IndentStream(
                        UngettableInStream(
                            ReaderInStream(
                                input, filename
                            )
                        ), false
                    )
                )
            )
            return ParserSource(table, `is`)
        } else if (filename.endsWith(".lsp")) {
            val `is`: InStream = UngettableInStream(ReaderInStream(input, filename))
            return ParserSource(table, `is`)
        } else {
            throw GenyrisException("unknown file suffix in : " + filename)
        }
    }

    @kotlin.Throws(GenyrisException::class)
    fun execAndClose(
        env: Environment, table: Internable, `in`: InputStream,
        filename: String?, writer: Writer?
    ) {
        try {
            executeScript(env, filename, table, InputStreamReader(`in`), writer)
        } finally {
            try {
                `in`.close()
            } catch (e: IOException) {
                throw GenyrisException(
                    "loadScriptFromInputStream: "
                            + e.getMessage()
                )
            }
        }
    }

    @kotlin.Throws(GenyrisException::class)
    fun loadScriptFromClasspath(
        env: Environment, table: Internable,
        filename: String?, writer: Writer?
    ): Exp {
        val `in`: InputStream? = SourceLoader::class.java.getClassLoader()
            .getResourceAsStream(filename)
        if (`in` == null) {
            throw GenyrisException(
                "loadScriptFromInputStream: could not open: " + filename
            )
        }
        val url: String? = SourceLoader::class.java.getClassLoader().getResource(filename)
            .toString()
        execAndClose(env, table, `in`, url, writer)
        return StrinG(url)
    }

    @kotlin.Throws(GenyrisException::class)
    fun loadScriptFromFile(
        env: Environment, table: Internable, filename: String,
        writer: Writer?
    ): Exp {
        val `in`: InputStream?
        try {
            `in` = FileInputStream(filename)
        } catch (e: FileNotFoundException) {
            throw GenyrisException("loadScriptFromFile: " + filename + " (No such file or directory)")
        }
        execAndClose(env, table, `in`, filename, writer)
        return StrinG(filename)
    }

    @kotlin.Throws(GenyrisException::class)
    fun executeScript(
        env: Environment, filename: String?, table: Internable,
        reader: Reader?, output: Writer?
    ): Exp? {
        val scriptDirVar =
            env.getSymbolTable().internSymbol(PrefixSymbol(Constants.PREFIX_SYSTEM, "script-directory", "sys"))
        var currentScriptDirectory: Exp? = table.NIL()
        if (env.isBound(scriptDirVar)) {
            currentScriptDirectory = env.lookupVariableValue(scriptDirVar)
        }
        if (filename != null) { // when a unit test has no file, it's a string
            env.defineVariable(
                scriptDirVar,
                StrinG(ClassicReadEvalPrintLoop.Companion.getContainingDirectoryPath(filename))
            )
        }
        val parser = SourceLoader.parserFactory(filename!!, reader, table)
        var expression: Exp? = null
        var result: Exp? = null
        do {
            expression = parser.read()
            if (expression == table.EOF()) {
                break
            }
            result = expression.evalCatchOverFlow(env)
        } while (true)
        // restore sys:script-directory
        if (currentScriptDirectory !== table.NIL()) {
            env.defineVariable(scriptDirVar, currentScriptDirectory)
        }
        return result
    }
}
