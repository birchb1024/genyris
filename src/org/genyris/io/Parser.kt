// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io

import org.genyris.core.*
import org.genyris.exception.GenyrisException
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import java.net.URI
import java.net.URISyntaxException

open class Parser @kotlin.jvm.JvmOverloads constructor(
    var _table: Internable,
    stream: InStream?,
    dynaChar: Char = Constants.DYNAMICSCOPECHAR2,
    cdrCharacter: Char = Constants.CDRCHAR,
    commentChar: Char = Constants.COMMENTCHAR
) {
    protected var _lexer: Lex

    private var cursym: Exp? = null

    protected var NIL: Exp
    protected var SEMI: Exp?
    protected var PLING: Exp?

    private val _prefix: Exp?

    private var pushback: Exp? = null

    init {
        _lexer = Lex(stream, _table, dynaChar, cdrCharacter, commentChar)
        NIL = _table.NIL()
        SEMI = _table.SEMI()
        PLING = _table.PLING()
        _prefix = _table.PREFIX()
    }

    protected open fun cons(l: Exp?, r: Exp?, line: Int): Exp {
        return Pair(l, r)
    }

    fun parseError(msg: String?): ParseException {
        return ParseException(msg, _lexer.getFilename(), _lexer.getLineNumber())
    }

    @kotlin.Throws(GenyrisException::class)
    fun nextsym() {
        if (pushback == null) {
            cursym = _lexer.nextToken()
        } else {
            cursym = pushback
            pushback = null
        }
    }

    @kotlin.Throws(ParseException::class)
    fun pushbacksym(token: Exp) {
        if (pushback == null) {
            pushback = token
        } else {
            throw parseError("Attempt to pushback to many: " + token.toString())
        }
    }

    @kotlin.Throws(GenyrisException::class)
    private fun readAux(env: Environment?): Exp {
        var retval = NIL
        _lexer.beginningExpression()
        nextsym()
        _lexer.withinExpression(env)
        if (cursym == _lexer.EOF_TOKEN) {
            retval = _table.EOF()
        } else {
            retval = parseExpression()
        }
        _lexer.beginningExpression()
        return (retval)
    }

    @kotlin.Throws(GenyrisException::class)
    open fun read(env: Environment?): Exp {
        var input = NIL
        try {
            input = readAux(env)
            while (namespaceDeclaration(input)) {
                input = readAux(env)
            }
        } catch (e: ParseException) {
            _lexer.resetAfterError()
            throw e
        }
        return input
    }

    @kotlin.Throws(GenyrisException::class)
    fun read(): Exp {
        return read(null)
    }

    fun isURI(X: StrinG): Boolean {
        try {
            val uri = URI(X.toString())
            return uri.isAbsolute()
        } catch (e: URISyntaxException) {
        }
        return false
    }

    @kotlin.Throws(GenyrisException::class)
    private fun namespaceDeclaration(input: Exp): Boolean {
        // process parser orders like @ns foo 'http://genyris.org/foo'
        if (!input.isPair()) {
            return false
        }
        if (input.car() !== _prefix) {
            return false
        }
        val namespace = input.cdr().car()
        if (namespace !is Symbol) {
            throw parseError("namespace not a symbol: " + namespace)
        }
        if (input.cdr().cdr() === NIL) {
            throw parseError("namespace declaration missing prefix " + input)
        }
        val prefix = input.cdr().cdr().car()
        if (prefix !is StrinG) {
            throw parseError("namespace prefix isn't a string: " + prefix)
        }
        if (!isURI(prefix)) {
            throw parseError("namespace prefix is not a URL: " + prefix)
        }
        _lexer.addprefix(
            namespace.getPrintName(),  // TODO notify the interpreter a new prefix is in use with _interp.collectPrefix(prefix, expansion);
            prefix.toString()
        ) //   - means changing Parser ctor signatures..
        return true
    }

    @kotlin.Throws(GenyrisException::class)
    private fun collectSemis(tree: Exp?): Exp? {
        var tree = tree
        val startline = _lexer.getLineNumber()
        var old = cursym
        nextsym()
        if (cursym !is Symbol) {
            throw parseError("Bad indirection: " + cursym.toString())
        } else if (cursym === _lexer.DYNAMIC_TOKEN) {
            throw parseError("Bad indirection: " + cursym.toString())
        }
        tree = cons(
            tree, cons(
                DynamicSymbol(cursym as SimpleSymbol), NIL, startline
            ), startline
        ) // TODO bad cast
        old = cursym
        nextsym()
        if (cursym === _lexer.SEMI_TOKEN) {
            return collectSemis(tree)
        } else {
            pushbacksym(cursym!!)
            cursym = old
        }
        return tree
    }

    @kotlin.Throws(GenyrisException::class)
    private fun collectPlings(tree: Exp?): Exp? {
        var tree = tree
        val startline = _lexer.getLineNumber()
        var old = cursym
        nextsym()
        val rhs = parseExpression()
        tree = cons(PLING, cons(tree, cons(rhs, NIL, startline), startline), startline) // TODO bad cast
        old = cursym
        nextsym()
        if (cursym === _lexer.PLING_TOKEN) {
            return collectPlings(tree)
        } else {
            pushbacksym(cursym!!)
            cursym = old
        }
        return tree
    }

    @kotlin.Throws(GenyrisException::class)
    fun parseList(lhs: Exp?): Exp {
        var startLine = _lexer.getLineNumber()
        var tree: Exp
        nextsym()
        if (cursym == _lexer.RIGHT_PAREN_TOKEN) {
            tree = NIL
        } else if (cursym == _lexer.RIGHT_SQUARE_TOKEN) {
            tree = NIL
        } else if (cursym == _lexer.RIGHT_CURLY_TOKEN) {
            tree = NIL
        } else if (cursym == _lexer.CDR_TOKEN) {
            return _lexer.CDR_TOKEN
        } else {
            startLine = _lexer.getLineNumber()
            tree = parseExpression()
            val old = cursym
            nextsym()
            if (cursym == _lexer.PLING_TOKEN) {
                val plings = collectPlings(tree)
                var restOfList = parseList(plings)
                if (restOfList === _lexer.CDR_TOKEN) {
                    nextsym()
                    restOfList = parseExpression()
                    nextsym()
                    tree = PairEquals(plings, restOfList)
                    return tree
                }
                tree = cons(plings, restOfList, startLine)
                return tree
            } else if (cursym == _lexer.SEMI_TOKEN) {
                val plings = collectSemis(tree)
                var restOfList = parseList(plings)
                if (restOfList === _lexer.CDR_TOKEN) {
                    nextsym()
                    restOfList = parseExpression()
                    nextsym()
                    tree = PairEquals(plings, restOfList)
                    return tree
                }
                tree = cons(plings, restOfList, startLine)
                return tree
            } else {
                pushbacksym(cursym!!)
                cursym = old
            }
            var restOfList = parseList(tree)
            if (restOfList === _lexer.CDR_TOKEN) {
                nextsym()
                restOfList = parseExpression()
                nextsym()
                tree = PairEquals(tree, restOfList)
                return tree
            }
            tree = cons(tree, restOfList, startLine)
        }
        return tree
    }

    @kotlin.Throws(GenyrisException::class)
    fun parseExpression(): Exp {
        val startline = _lexer.getLineNumber()
        var tree = NIL
        if (cursym == _lexer.PLING_TOKEN) {
            throw parseError("unexpected !")
        }
        if (cursym == _lexer.SEMI_TOKEN) {
            throw parseError("unexpected ;")
        }
        if (cursym == _lexer.CDR_TOKEN) {
            throw parseError("unexpected =")
        }
        if (cursym == _lexer.EOF_TOKEN) {
            throw parseError("unexpected End of File")
        }
        if (cursym == _lexer.RIGHT_PAREN_TOKEN) {
            throw parseError("unexpected right paren")
        }
        if (cursym == _lexer.LEFT_PAREN_TOKEN) {
            tree = parseList(NIL)
            if (cursym != _lexer.RIGHT_PAREN_TOKEN) {
                throw parseError(
                    "missing right paren - found: "
                            + cursym
                )
            }
        } else if (cursym == _lexer.LEFT_SQUARE_TOKEN) {
            tree = parseList(NIL)
            tree = cons(_table.SQUARE(), tree, startline)
            if (cursym != _lexer.RIGHT_SQUARE_TOKEN) {
                throw parseError(
                    "missing right square brace - found: "
                            + cursym
                )
            }
        } else if (cursym == _lexer.LEFT_CURLY_TOKEN) { // TOD DRY - SQUARE
            tree = parseList(NIL)
            tree = cons(_table.CURLY(), tree, startline)
            if (cursym != _lexer.RIGHT_CURLY_TOKEN) {
                throw parseError(
                    "missing right curly brace - found: "
                            + cursym
                )
            }
        } else if (cursym === _lexer.BACKQUOTE_TOKEN) {
            nextsym()
            tree = cons(_table.TEMPLATE(), cons(parseExpression(), NIL, startline), startline)
        } else if (cursym === _lexer.QUOTE_TOKEN) {
            nextsym()
            tree = cons(_table.QUOTE(), cons(parseExpression(), NIL, startline), startline)
        } else if (cursym === _lexer.DOLLAR_TOKEN) {
            nextsym()
            tree = cons(_table.DOLLAR(), cons(parseExpression(), NIL, startline), startline)
        } else if (cursym === _lexer.DOLLAR_AT_TOKEN) {
            nextsym()
            tree = cons(_table.DOLLAR_AT(), cons(parseExpression(), NIL, startline), startline)
        } else if (cursym === _lexer.DYNAMIC_TOKEN) {
            nextsym()
            if (cursym is SimpleSymbol) {
                tree = DynamicSymbol(cursym as SimpleSymbol)
            } else {
                throw parseError(
                    "period found before non-symbol: "
                            + cursym
                )
            }
        } else {
            tree = cursym
        }
        return tree
    }

    @kotlin.Throws(GenyrisException::class)
    fun addPrefix(interp: Interpreter, prefix: String?, expansion: String?) {
        interp.collectPrefix(prefix, expansion)
        _lexer.addprefix(prefix, expansion)
    }

    @kotlin.Throws(LexException::class)
    fun resetAfterError() {
        _lexer.resetAfterError()
    }

    @kotlin.Throws(GenyrisException::class)
    fun setUsualPrefixes(interp: Interpreter) {
        addPrefix(interp, "u", Constants.PREFIX_UTIL)
        addPrefix(interp, "web", Constants.PREFIX_WEB)
        addPrefix(interp, "syn", Constants.PREFIX_SYNTAX)
        addPrefix(interp, "sys", Constants.PREFIX_SYSTEM)
        addPrefix(interp, "task", Constants.PREFIX_TASK)
        addPrefix(interp, "types", Constants.PREFIX_TYPES)
        addPrefix(interp, "date", Constants.PREFIX_DATE)
        addPrefix(interp, "java", Constants.PREFIX_JAVA)
    }

    companion object {
        @kotlin.Throws(GenyrisException::class)
        fun parseSingleExpressionFromString(
            table: Internable?,
            script: String
        ): Exp {
            val input: InStream = UngettableInStream(StringInStream(script))
            val parser: Parser = ParserSource(table, input)
            val expression = parser.read()
            return expression
        }
    }
}
