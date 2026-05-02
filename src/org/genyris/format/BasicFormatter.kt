// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.format

import org.genyris.core.*
import org.genyris.exception.AccessException
import org.genyris.exception.GenyrisException
import org.genyris.interp.EagerProcedure
import org.genyris.interp.LazyProcedure
import java.io.IOException
import java.io.Writer
import java.math.BigDecimal

open class BasicFormatter : AbstractFormatter {
    constructor(out: Writer?, expandPrefix: Boolean) : super(out, expandPrefix)

    constructor(out: Writer?) : super(out)

    @kotlin.Throws(GenyrisException::class)
    override fun visitDictionary(frame: Dictionary) {
        frame.asAlist().acceptVisitor(this)
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitEagerProc(proc: EagerProcedure) {
        write(proc.toString())
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitLazyProc(proc: LazyProcedure) {
        write(proc.toString())
    }

    @kotlin.Throws(GenyrisException::class)
    fun writeCdr(cons: Exp) {
        try {
            if (cons.isNil()) {
                return
            }
            write(" ")
            if (!cons.isPair()) {
                write(Constants.CDRCHAR.toString() + " ") // cdr_char
                cons.acceptVisitor(this)
                return
            }
            cons.car().acceptVisitor(this)
            if (cons.cdr().isNil()) {
                return
            }
            writeCdr(cons.cdr())
        } catch (e: AccessException) {
            throw GenyrisException(
                (this.getClass().getName() + ": "
                        + e.getMessage())
            )
        }
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitPair(cons: Pair) {
        write("(")
        cons.car().acceptVisitor(this)
        if (cons is PairEquals) {
            write(" " + Constants.CDRCHAR + " ")
            cons.cdr().acceptVisitor(this)
        } else {
            writeCdr(cons.cdr())
        }
        write(")")
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitBignum(bignum: Bignum) {
        val value = bignum.bigDecimalValue()
        if (value.remainder(BigDecimal(1)).compareTo(BigDecimal(0)) > 0) {
            val padded = value.toPlainString()
            write(trim(padded, '0'))
        } else {
            write(value.toPlainString())
        }
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitStrinG(lst: StrinG) {
        write(lst.getQuoteChar())
        val str = StringBuffer(lst.toString())
        for (i in 0..<str.length()) {
            val ch: Char = str.charAt(i)
            if (ch == '\n') { // TODO move this into a table in Lex.
                write("\\n")
            } else if (ch == lst.getQuoteChar()) {
                write("\\")
                write(ch)
            } else if (ch == '\t') {
                write("\\t")
            } else if (ch == '\r') {
                write("\\r")
            } else {
                write(ch)
            }
        }
        write(lst.getQuoteChar())
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitExpWithEmbeddedClasses(exp: ExpWithEmbeddedClasses) {
        write("[Exp: " + exp.toString() + "]")
    }

    @kotlin.Throws(GenyrisException::class, IOException::class)
    override fun print(message: String?) {
        write(message)
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitPrefixSymbol(sym: PrefixSymbol) {
        write(sym.getPrintNameOpt(_expandPrefix))
    }


    companion object {
        fun trim(n: String, ch: Char): String {
            //
            // Remove trailing from a string
            //
            if (n.length() == 1) return n
            if (n.charAt(n.length() - 1) == ch) {
                return trim(n.substring(0, n.length() - 1), ch)
            } else {
                return n
            }
        }
    }
}
