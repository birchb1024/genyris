// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.format

import org.genyris.core.*
import org.genyris.exception.GenyrisException
import org.genyris.interp.EagerProcedure
import org.genyris.interp.LazyProcedure
import java.io.IOException
import java.io.Writer

class IndentedFormatter @kotlin.jvm.JvmOverloads constructor(
    out: Writer?,
    private val INDENT_DEPTH: Int,
    expandPrefix: Boolean = false
) : AbstractFormatter(out, expandPrefix) {
    private var _consDepth = 0
    private val _basic: Formatter?

    init {
        _basic = BasicFormatter(out)
    }

    @kotlin.Throws(IOException::class)
    private fun printSpaces(level: Int) {
        for (i in 1..<level) _output.write("   ")
    }

    @kotlin.Throws(GenyrisException::class, IOException::class)
    fun printPair(cons: Pair) {
        // TODO - Yuck!
        _consDepth += 1
        var head: Exp = cons
        var countOfRight = 0
        if (cons is PairEquals) {
            printSpaces(_consDepth)
            cons.car().acceptVisitor(_basic)
            _output.write(" " + Constants.CDRCHAR + " ")
            cons.cdr().acceptVisitor(_basic)
            _consDepth -= 1
            return
        }
        while (head !is NilSymbol) {
            countOfRight += 1
            if (head.isPair()) {
                val headCons = (head as Pair)
                if (headCons.car().isPair()) {
                    val first = (headCons.car() as Pair)
                    if (countOfRight <= INDENT_DEPTH) {
                        if (countOfRight > 1) _output.write(' '.code)
                        headCons.car().acceptVisitor(_basic)
                        head = headCons.cdr()
                        continue
                    } else {
                        _output.write('\n'.code)

                        printSpaces(_consDepth + 1)
                        printPair(first)
                    }
                    if (headCons.cdr().isPair()) {
                        val rest = headCons.cdr() as Pair
                        if (!rest.car().isPair()) {
                            _output.write('\n'.code)
                            printSpaces(_consDepth + 1)
                            _output.write('~'.code)
                        }
                    }
                } else {
                    if (countOfRight > 1) _output.write(' '.code)
                    headCons.car().acceptVisitor(this)
                }
                head = headCons.cdr()
            } else {
                if (countOfRight > 1) _output.write(' '.code)
                _output.write(Constants.CDRCHAR.toString() + " ")
                head.acceptVisitor(this)
                _consDepth -= 1
                return
            }
        }
        _consDepth -= 1
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitPair(cons: Pair) {
        try {
            printPair(cons)
        } catch (e: IOException) {
            throw GenyrisException(e.getMessage())
        }
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitEagerProc(proc: EagerProcedure) {
        writeAtom(proc)
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitLazyProc(proc: LazyProcedure) {
        writeAtom(proc)
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitBignum(bignum: Bignum) {
        writeAtom(bignum)
    }

    @kotlin.Throws(GenyrisException::class)
    private fun writeAtom(exp: Exp) {
        try {
            if (_consDepth == 0) _output.write("~ ")
            exp.acceptVisitor(_basic)
        } catch (e: IOException) {
            throw GenyrisException(
                (this.getClass().getName() + ": "
                        + e.getMessage())
            )
        }
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitSimpleSymbol(sym: SimpleSymbol) {
        writeAtom(sym)
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitFullyQualifiedSymbol(sym: URISymbol) {
        writeAtom(sym)
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitPrefixSymbol(sym: PrefixSymbol) {
        write(sym.getPrintNameOpt(_expandPrefix))
    }


    @kotlin.Throws(GenyrisException::class)
    override fun visitStrinG(lst: StrinG) {
        writeAtom(lst)
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitDictionary(frame: Dictionary) {
        try {
            printPair((frame.asAlist() as org.genyris.core.Pair?)!!)
        } catch (e: IOException) {
            throw GenyrisException(
                (this.getClass().getName() + ": "
                        + e.getMessage())
            )
        }
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitExpWithEmbeddedClasses(exp: ExpWithEmbeddedClasses) {
        writeAtom(exp)
    }

    @kotlin.Throws(GenyrisException::class, IOException::class)
    override fun print(message: String) {
        _output.write(message)
    }
}
