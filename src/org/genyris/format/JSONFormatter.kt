// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.format

import org.genyris.core.*
import org.genyris.core.Dictionary
import org.genyris.exception.GenyrisException
import org.genyris.interp.EagerProcedure
import org.genyris.interp.LazyProcedure
import java.io.Writer
import java.util.*

class JSONFormatter(out: Writer?) : AbstractFormatter(out, true) {
    @kotlin.Throws(GenyrisException::class)
    private fun emit(s: String?) {
        write(s)
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitDynamicSymbol(sym: DynamicSymbol) {
        emitStringEscaped(sym.getRealSymbol().getPrintName())
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitPrefixSymbol(sym: PrefixSymbol) {
        emitStringEscaped(sym.getPrintName())
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitFullyQualifiedSymbol(sym: URISymbol) {
        emitStringEscaped(sym.getPrintName())
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitFullyQualifiedSymbol(sym: EscapedSymbol) {
        emitStringEscaped(sym.getPrintName())
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitSimpleSymbol(sym: SimpleSymbol) {
        if (sym.isNil()) {
            write("[]")
            return
        }
        val asString = sym.toString()
        if (asString == "true" || asString == "false" || asString == "null") {
            write(asString)
        } else {
            emitStringEscaped(asString)
        }
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitDictionary(frame: Dictionary) {
        val _dict = frame.getMap()
        val keys: SortedSet<*> = TreeSet<Any?>(_dict.keySet())
        val iter: MutableIterator<Exp> = keys.iterator()
        write("{ ")
        while (iter.hasNext()) {
            val key = iter.next()
            val value = _dict.get(key) as Exp
            key.acceptVisitor(this)
            write(" : ")
            value.acceptVisitor(this)
            if (iter.hasNext()) {
                write(" , ")
            }
        }
        write(" }")
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitEagerProc(proc: EagerProcedure) {
        emitStringEscaped(proc.toString())
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitLazyProc(proc: LazyProcedure) {
        emitStringEscaped(proc.toString())
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitPair(cons: Pair) {
        write("[ ")
        var head: Exp = cons
        while (!head.isNil()) {
            head.car().acceptVisitor(this)
            head = head.cdr()
            if (!head.isNil() && !head.isPair()) {
                // end of list item or Assoc perhaps
                write(" , ")
                head.acceptVisitor(this)
                write(" ]")
                return
            }
            if (!head.isNil()) write(" , ")
        }
        write(" ]")
    }


    @kotlin.Throws(GenyrisException::class)
    override fun visitBignum(bignum: Bignum) {
        write(bignum.toString())
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitStrinG(lst: StrinG) {
        emitStringEscaped(lst.toString())
    }

    @kotlin.Throws(GenyrisException::class)
    private fun emitStringEscaped(lst: String) {
        write("\"")
        val str = StringBuffer(lst)
        for (i in 0..<str.length()) {
            val ch: Char = str.charAt(i)
            if (ch == '\n') {
                write("\\n")
            } else if (ch == '\\') {
                write("\\\\")
            } else if (ch == '"') {
                write("\\\"")
            } else if (ch == '\t') {
                write("\\t")
            } else if (ch == '\r') {
                write("\\r")
            } else {
                write(ch)
            }
        }
        write("\"")
    }


    @kotlin.Throws(GenyrisException::class)
    override fun visitExpWithEmbeddedClasses(exp: ExpWithEmbeddedClasses) {
        emit(exp.toString())
    }

    @kotlin.Throws(GenyrisException::class)
    override fun print(message: String?) {
        emit(message)
    }
}
