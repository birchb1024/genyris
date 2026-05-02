// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.io

import junit.framework.Assert
import junit.framework.TestCase
import org.genyris.exception.GenyrisException
import org.genyris.interp.Interpreter
import org.genyris.load.SourceLoader
import java.io.StringReader
import java.io.StringWriter

class SourceLoaderTest : TestCase() {
    @kotlin.Throws(Exception::class)
    override fun setUp() {
        super.setUp()
    }

    @kotlin.Throws(GenyrisException::class)
    private fun excerciseSourceLoader(input: String, expected: String?) {
        val interp = Interpreter()
        interp.init(false, "excerciseSourceLoader")
        val `in` = StringReader(input)
        val out = StringWriter()
        SourceLoader.executeScript(
            interp.getGlobalEnv(), "-",
            interp.getSymbolTable(), `in`, out
        )
        Assert.assertEquals(expected, out.toString())
    }

    @kotlin.Throws(GenyrisException::class)
    fun testSourceLoader1() {
        excerciseSourceLoader("list 1\n list 2\n ~22\n\nlist 3 4", "")
        excerciseSourceLoader(
            "list 1\n list 2\n ~22\n\ndef f (x) \n      cons x x", ""
        )
        excerciseSourceLoader(
            "list 22\n  list\n      the 333\n\nlist ^f ^(x)\n    cons 1 2\n",
            ""
        )
    }

    @kotlin.Throws(GenyrisException::class)
    fun testSourceLoader2() {
        val interp = Interpreter()
        interp.init(false, "testSourceLoader2")
        val out = StringWriter()
        SourceLoader.loadScriptFromClasspath(
            interp.getGlobalEnv(),
            interp.getSymbolTable(), "org/genyris/load/boot/init.g", out
        )
    }

    @kotlin.Throws(GenyrisException::class)
    fun testSourceLoader3() {
        val interp = Interpreter()
        interp.init(false, "test/fixtures")
        val out = StringWriter()
        SourceLoader.loadScriptFromFile(
            interp.getGlobalEnv(),
            interp.getSymbolTable(), "test/fixtures/factorial.g", out
        )
    }

    @kotlin.Throws(GenyrisException::class)
    fun testSourceLoaderLisp() {
        val interp = Interpreter()
        interp.init(false, "test/fixtures")
        val out = StringWriter()
        SourceLoader.loadScriptFromFile(
            interp.getGlobalEnv(),
            interp.getSymbolTable(), "test/fixtures/factorial.lsp", out
        )
    }
}
