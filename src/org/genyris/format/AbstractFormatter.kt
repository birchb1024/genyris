// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.format

import org.genyris.core.*
import org.genyris.dl.AbstractGraph
import org.genyris.dl.Triple
import org.genyris.exception.GenyrisException
import org.genyris.interp.EagerProcedure
import org.genyris.interp.Interpreter
import org.genyris.interp.LazyProcedure
import org.genyris.interp.TailCall
import org.genyris.io.pipe.Pipe
import org.genyris.java.JavaWrapper
import java.io.IOException
import java.io.Writer

abstract class AbstractFormatter : Visitor, Formatter {
    protected var _output: Writer
    protected var _expandPrefix: Boolean

    constructor(out: Writer) {
        _output = out
        _expandPrefix = false
    }

    constructor(out: Writer, expandPrefix: Boolean) {
        _output = out
        _expandPrefix = expandPrefix
    }

    @kotlin.Throws(GenyrisException::class)
    abstract override fun visitDictionary(frame: Dictionary?)

    @kotlin.Throws(GenyrisException::class)
    override fun visitStandardClass(klass: StandardClass) {
        write(klass.toString())
    }

    @kotlin.Throws(GenyrisException::class)
    abstract override fun visitEagerProc(proc: EagerProcedure?)

    @kotlin.Throws(GenyrisException::class)
    abstract override fun visitLazyProc(proc: LazyProcedure?)

    @kotlin.Throws(GenyrisException::class)
    abstract override fun visitPair(cons: Pair?)

    @kotlin.Throws(GenyrisException::class)
    abstract override fun visitBignum(bignum: Bignum?)

    @kotlin.Throws(GenyrisException::class)
    abstract override fun visitStrinG(lst: StrinG?)

    @kotlin.Throws(GenyrisException::class)
    override fun visitSimpleSymbol(sym: SimpleSymbol) {
        write(sym.toString())
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitPrefixSymbol(sym: PrefixSymbol) {
        write(sym.getEscapedPrintName())
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitTailCall(tc: TailCall) {
        write(tc.toString())
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitBiscuit(b: Biscuit) {
        write(b.toString())
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitDynamicSymbol(sym: DynamicSymbol) {
        write(sym.toString())
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitPipe(pipe: Pipe) {
        write(pipe.toString())
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitFullyQualifiedSymbol(sym: URISymbol) {
        write(sym.toString())
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitFullyQualifiedSymbol(sym: EscapedSymbol) {
        write(sym.toString())
    }

    @kotlin.Throws(GenyrisException::class)
    override fun printClassNames(result: Exp, interp: Interpreter) {
        var klasses = result.getClasses(interp.getGlobalEnv())
        while (klasses !is NilSymbol) {
            val klass = klasses.car() as Dictionary
            write(
                " "
                        + klass.lookupVariableShallow(
                    interp.getSymbolTable().CLASSNAME()
                ).toString()
            )
            klasses = klasses.cdr()
        }
    }

    @kotlin.Throws(GenyrisException::class)
    private fun handleIO(e: IOException) {
        throw GenyrisException(
            (this.getClass().getName() + " write: "
                    + e.getMessage())
        )
    }

    @kotlin.Throws(GenyrisException::class)
    protected fun write(ch: Char) {
        try {
            _output.write(ch.code)
        } catch (e: IOException) {
            handleIO(e)
        }
    }

    @kotlin.Throws(GenyrisException::class)
    fun write(str: String) {
        try {
            _output.write(str)
        } catch (e: IOException) {
            handleIO(e)
        }
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitTriple(triple: Triple) {
        val basic: Formatter = BasicFormatter(_output)
        write("(triple ")
        triple.subject.acceptVisitor(basic)
        write(" ")
        triple.predicate.acceptVisitor(basic)
        write(" ")
        triple.`object`.acceptVisitor(basic)
        write(")")
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitGraph(ts: AbstractGraph) {
        write("(graph")
        val triarray = ts.toSortedTriplesArray()
        for (T in triarray) {
            write(" ")
            T.acceptVisitor(BasicFormatter(_output))
        }
        write(")")
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitJavaWrapper(javaWrapper: JavaWrapper) {
        write(
            ("[" + javaWrapper.getValue().getClass().getName() + " "
                    + javaWrapper.getValue().toString() + "]")
        )
    }
}
