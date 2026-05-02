// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core

import org.genyris.dl.AbstractGraph
import org.genyris.dl.Triple
import org.genyris.exception.GenyrisException
import org.genyris.interp.EagerProcedure
import org.genyris.interp.LazyProcedure
import org.genyris.interp.TailCall
import org.genyris.io.pipe.Pipe
import org.genyris.java.JavaWrapper

interface Visitor {
    @kotlin.Throws(GenyrisException::class)
    fun visitPair(cons: Pair?)

    @kotlin.Throws(GenyrisException::class)
    fun visitStrinG(lst: StrinG?)

    @kotlin.Throws(GenyrisException::class)
    fun visitLazyProc(lproc: LazyProcedure?)

    @kotlin.Throws(GenyrisException::class)
    fun visitEagerProc(eproc: EagerProcedure?)

    @kotlin.Throws(GenyrisException::class)
    fun visitDictionary(frame: Dictionary?)

    @kotlin.Throws(GenyrisException::class)
    fun visitBignum(bignum: Bignum?)

    @kotlin.Throws(GenyrisException::class)
    fun visitStandardClass(klass: StandardClass?)

    @kotlin.Throws(GenyrisException::class)
    fun visitExpWithEmbeddedClasses(exp: ExpWithEmbeddedClasses?)

    @kotlin.Throws(GenyrisException::class)
    fun visitSimpleSymbol(simpleSymbol: SimpleSymbol?)

    @kotlin.Throws(GenyrisException::class)
    fun visitFullyQualifiedSymbol(sym: EscapedSymbol?)

    @kotlin.Throws(GenyrisException::class)
    fun visitFullyQualifiedSymbol(sym: URISymbol?)

    @kotlin.Throws(GenyrisException::class)
    fun visitPrefixSymbol(sym: PrefixSymbol?)

    @kotlin.Throws(GenyrisException::class)
    fun visitDynamicSymbol(symbol: DynamicSymbol?)

    @kotlin.Throws(GenyrisException::class)
    fun visitTriple(triple: Triple?)

    @kotlin.Throws(GenyrisException::class)
    fun visitGraph(store: AbstractGraph?)

    @kotlin.Throws(GenyrisException::class)
    fun visitJavaWrapper(javaWrapper: JavaWrapper?)

    @kotlin.Throws(GenyrisException::class)
    fun visitPipe(pipe: Pipe?)

    @kotlin.Throws(GenyrisException::class)
    fun visitTailCall(tc: TailCall?)

    @kotlin.Throws(GenyrisException::class)
    fun visitBiscuit(biscuit: Biscuit?)
}
