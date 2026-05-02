package org.genyris.format

import org.genyris.core.*
import org.genyris.exception.AccessException
import org.genyris.exception.GenyrisException
import org.genyris.interp.EagerProcedure
import org.genyris.interp.Interpreter
import org.genyris.interp.LazyProcedure
import org.genyris.interp.UnboundException
import java.io.IOException

interface Formatter : Visitor {
    @kotlin.Throws(GenyrisException::class)
    override fun visitDictionary(frame: Dictionary?)

    @kotlin.Throws(GenyrisException::class)
    override fun visitEagerProc(proc: EagerProcedure?)

    @kotlin.Throws(GenyrisException::class)
    override fun visitLazyProc(proc: LazyProcedure?)

    @kotlin.Throws(GenyrisException::class)
    override fun visitPair(cons: Pair?)

    @kotlin.Throws(GenyrisException::class)
    override fun visitBignum(bignum: Bignum?)

    @kotlin.Throws(GenyrisException::class)
    override fun visitStrinG(lst: StrinG?)

    @kotlin.Throws(GenyrisException::class)
    override fun visitSimpleSymbol(sym: SimpleSymbol?)

    @kotlin.Throws(GenyrisException::class)
    override fun visitFullyQualifiedSymbol(sym: URISymbol?)

    @kotlin.Throws(GenyrisException::class)
    override fun visitStandardClass(klass: StandardClass?)

    @kotlin.Throws(AccessException::class, IOException::class, UnboundException::class, GenyrisException::class)
    fun printClassNames(item: Exp?, interp: Interpreter?)

    @kotlin.Throws(GenyrisException::class, IOException::class)
    fun print(message: String?)
}