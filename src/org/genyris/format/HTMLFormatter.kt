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
import org.genyris.io.readerstream.ReaderStream
import java.io.Writer

class HTMLFormatter(out: Writer?) : AbstractFormatter(out) {
    @kotlin.Throws(GenyrisException::class)
    private fun emit(s: String?) {
        write(HTMLEntityEncode(s))
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitDictionary(frame: Dictionary) {
        val standardClassSymbol: Symbol? = frame.getSymbolTable().STANDARDCLASS()
        val standardClass = frame.getParent().lookupVariableValue(standardClassSymbol)
        if (standardClass !is Dictionary) {
            throw GenyrisException("Non-Dictionary class: " + standardClass.toString())
        }
        val scDict = standardClass
        if (frame.isTaggedWith(scDict)) {
            if (frame !is StandardClass) {
                throw GenyrisException("Non-StandardClass: " + frame.toString())
            }
            frame.acceptVisitor(this)
            return
        }
        frame.asAlist().acceptVisitor(this)
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitEagerProc(proc: EagerProcedure) {
        write("<EagerProc: " + proc.toString() + ">")
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitLazyProc(proc: LazyProcedure) {
        emit(proc.toString())
    }

    private fun abbreviate(T: Symbol): String? {
        if (T is PrefixSymbol) {
            return T.getAbbreviatedPrintName()
        }
        return T.getPrintName()
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitPair(exp: Pair) {
        if (exp.car() is Symbol) {
            val tag = exp.car() as Symbol
            var attributes: Exp = NilSymbol()
            var body: Exp = NilSymbol()
            if (exp.cdr() is NilSymbol) {
                // no attributes or body
                attributes = exp.cdr()
                body = attributes
            } else {
                if (exp.cdr().isPair()) {
                    attributes = exp.cdr().car()
                    body = exp.cdr().cdr()
                } else {
                    // skip bad or missing attributes list

                    body = exp.cdr()
                }
            }
            if (abbreviate(tag) == "nil" && exp.cdr().isNil()) {
                // skip
                return
            } else if (abbreviate(tag) == "verbatim") {
                while (!body.isNil()) {
                    val formatter = DisplayFormatter(_output)
                    body.car().acceptVisitor(formatter)
                    body = body.cdr()
                }
                return
            }
            if (abbreviate(tag) == "stream") {
                if (!body.isNil()) {
                    if (body.car() is ReaderStream) {
                        val str = body.car() as ReaderStream
                        str.copy(_output, 25 * 80)
                    } else {
                        throw GenyrisException("non-Reader passed in stream tag.")
                    }
                } else {
                    throw GenyrisException("non body in stream tag.")
                }
                return
            }
            write("<" + abbreviate(tag))
            writeAttributes(attributes)
            if (body is NilSymbol) {
                write("/>")
            } else {
                write(">")
                body.acceptVisitor(this)
                write("</" + abbreviate(tag) + ">")
            }
        } else {
            var head: Exp = exp
            while (!head.isNil()) {
                head.car().acceptVisitor(this)
                head = head.cdr()
            }
        }
    }

    @kotlin.Throws(GenyrisException::class)
    private fun writeAttributes(attributes: Exp) {
        var attributes = attributes
        if (attributes !is NilSymbol) {
            write(" ")
            while (attributes !is NilSymbol) {
                if (attributes !is Pair) {
                    write("*** error bad HTML attribute: ")
                    write(attributes.toString())
                    return
                }
                if (attributes.car() !is Pair) {
                    write("*** error bad HTML attribute: ")
                    write(attributes.toString())
                    return
                }
                val attrName = attributes.car().car()
                if (attrName !is Symbol) {
                    write("*** error bad HTML attribute: ")
                    write(attributes.toString())
                    return
                }
                write(abbreviate(attrName))
                write("=\"")
                write(attributes.car().cdr().toString())
                write("\"")
                if (attributes.cdr() !is NilSymbol) write(" ")
                attributes = attributes.cdr()
            }
        }
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitBignum(bignum: Bignum) {
        write(bignum.toString())
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitStrinG(lst: StrinG) {
        emit(lst.toString())
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitExpWithEmbeddedClasses(exp: ExpWithEmbeddedClasses) {
        emit(exp.toString())
    }

    @kotlin.Throws(GenyrisException::class)
    override fun print(message: String?) {
        emit(message)
    }

    @kotlin.Throws(GenyrisException::class)
    override fun visitPrefixSymbol(sym: PrefixSymbol) {
        emit(sym.getAbbreviatedPrintName())
    }

    companion object {
        fun HTMLEntityEncode(s: String?): String {
            val buf = StringBuffer()
            val len = (if (s == null) -1 else s.length())

            for (i in 0..<len) {
                val c: Char = s.charAt(i)
                if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0'
                    && c <= '9' || c == ' ' || c == '.' || c == '-' || c == ':' || c == '+'
                ) {
                    buf.append(c)
                } else {
                    buf.append("&#" + c.code + ";")
                }
            }
            return buf.toString()
        }
    }
}
