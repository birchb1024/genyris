// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core

import org.genyris.exception.GenyrisException
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.MutableIterator
import kotlin.collections.MutableList
import kotlin.collections.MutableMap

abstract class SymbolTable : Internable {
    private val _table: MutableMap<String, Symbol>

    private var _BIGNUM: SimpleSymbol? = null
    private var _BISCUIT: SimpleSymbol? = null
    private var _classes: SimpleSymbol? = null
    private var _classname: SimpleSymbol? = null
    private var _curly: SimpleSymbol? = null
    private var _dict: SimpleSymbol? = null
    private var _dictionary: SimpleSymbol? = null
    private var _dollar: SimpleSymbol? = null
    private var _dollar_at: SimpleSymbol? = null
    private var _dynamic: SimpleSymbol? = null
    private var _DYNAMICSYMBOLREF: SimpleSymbol? = null
    private var _EAGERPROC: SimpleSymbol? = null
    private var _eof: SimpleSymbol? = null
    private var _false: SimpleSymbol? = null
    private var _filename: SimpleSymbol? = null
    private var _GLOBALGRAPH: SimpleSymbol? = null
    private var _GRAPH: SimpleSymbol? = null
    private var _INDENTEDPARSER: SimpleSymbol? = null
    private var _JAVACLASS: SimpleSymbol? = null
    private var _JAVACTOR: SimpleSymbol? = null
    private var _JAVAMETHOD: SimpleSymbol? = null
    private var _JAVASTATICMETHOD: SimpleSymbol? = null
    private var _JAVAWRAPPER: SimpleSymbol? = null
    private var _lambda: SimpleSymbol? = null
    private var _lambdam: SimpleSymbol? = null
    private var _lambdaq: SimpleSymbol? = null
    private var _LAZYPROC: SimpleSymbol? = null
    private var _left: SimpleSymbol? = null
    private var _length: SimpleSymbol? = null
    private var _lineNumber: SimpleSymbol? = null
    private var _NAME: SimpleSymbol? = null
    private var NIL: Symbol = null
    private var _OBJECT: SimpleSymbol? = null
    private var _PAIR: SimpleSymbol? = null
    private var _PAIREQUAL: SimpleSymbol? = null
    private var _PAIRSOURCE: SimpleSymbol? = null
    private var _PARENPARSER: SimpleSymbol? = null
    private var _PIPE: SimpleSymbol? = null
    private var _PLING: SimpleSymbol? = null
    private var _PREDICATE: SimpleSymbol? = null
    private var _prefix: SimpleSymbol? = null
    private var _PROCEDUREMISSING: SimpleSymbol? = null
    private var _quote: SimpleSymbol? = null
    private var _READER: SimpleSymbol? = null
    private var _rest: SimpleSymbol? = null
    private var _right: SimpleSymbol? = null
    private var _self: SimpleSymbol? = null
    private val _SEMI: SimpleSymbol? = null
    private var _SIMPLESYMBOL: SimpleSymbol? = null
    private var _SOURCE: SimpleSymbol? = null
    private var _square: SimpleSymbol? = null
    private var _standardclass: SimpleSymbol? = null
    private var _STRING: SimpleSymbol? = null
    private var _subclasses: SimpleSymbol? = null
    private var _SUBCLASSOF: SimpleSymbol? = null
    private var _SUBJECT: SimpleSymbol? = null
    private var _SUBJECTS: SimpleSymbol? = null
    private var _superclasses: SimpleSymbol? = null
    private var _TAILCALL: SimpleSymbol? = null
    private var _template: SimpleSymbol? = null
    private var _thing: SimpleSymbol? = null
    private var _TRIPLE: SimpleSymbol? = null
    private var _true: SimpleSymbol? = null
    private var _TYPE: SimpleSymbol? = null
    private var _URISYMBOL: SimpleSymbol? = null
    private var _validate: SimpleSymbol? = null
    private var _vars: SimpleSymbol? = null
    private var _WRITER: SimpleSymbol? = null
    private var _XMLPARSER: SimpleSymbol? = null

    init {
        _table = HashMap<String, Symbol>()
    }

    fun init(nil: SimpleSymbol) {
        NIL = nil
        _table.put(Constants.NIL, NIL!!)
        _self = bindKeyword(Constants.SELF)
        _classes = bindKeyword(Constants.CLASSES)
        _superclasses = bindKeyword(Constants.SUPERCLASSES)
        _classname = bindKeyword(Constants.CLASSNAME)
        _left = bindKeyword(Constants.LEFT)
        _length = bindKeyword(Constants.LENGTH)
        _right = bindKeyword(Constants.RIGHT)
        _lineNumber = bindKeyword(Constants.LINENUMBER)
        _filename = bindKeyword(Constants.FILENAME)
        _dynamic = bindKeyword(Constants.DYNAMIC_SYMBOL)
        _rest = bindKeyword(Constants.REST)
        _dict = bindKeyword(Constants.DICT)
        _dictionary = bindKeyword(Constants.DICTIONARY)
        _true = bindKeyword(Constants.TRUE)
        _false = bindKeyword(Constants.FALSE)
        _standardclass = bindKeyword(Constants.STANDARDCLASS)
        _subclasses = bindKeyword(Constants.SUBCLASSES)
        _superclasses = bindKeyword(Constants.SUPERCLASSES)
        _thing = bindKeyword(Constants.THING)
        _validate = bindKeyword(Constants.VALIDATE)
        _vars = bindKeyword(Constants.VARS)

        _lambda = bindKeyword(Constants.LAMBDA)
        _lambdaq = bindKeyword(Constants.LAMBDAQ)
        _lambdam = bindKeyword(Constants.LAMBDAM)

        _eof = bindKeyword(Constants.EOF)
        _template = bindKeyword(Constants.TEMPLATE)
        _square = bindKeyword(Constants.SQUARE)
        _curly = bindKeyword(Constants.CURLY)
        _quote = bindKeyword(Constants.QUOTE)
        _dollar = bindKeyword(java.lang.String.valueOf(Constants.DOLLAR))
        _dollar_at = bindKeyword(java.lang.String.valueOf(Constants.DOLLAR_AT))
        _prefix = bindKeyword(java.lang.String.valueOf(Constants.PREFIX))

        _BIGNUM = bindKeyword(java.lang.String.valueOf(Constants.BIGNUM))
        _EAGERPROC = bindKeyword(java.lang.String.valueOf(Constants.EAGERPROCEDURE))
        _LAZYPROC = bindKeyword(java.lang.String.valueOf(Constants.LAZYPROCEDURE))
        _PAIR = bindKeyword(java.lang.String.valueOf(Constants.PAIR))
        _PAIREQUAL = bindKeyword(java.lang.String.valueOf(Constants.PAIREQUAL))
        _PAIRSOURCE = bindKeyword(java.lang.String.valueOf(Constants.PAIRESOURCE))

        _PARENPARSER = bindKeyword(java.lang.String.valueOf(Constants.PARENPARSER))
        _XMLPARSER = bindKeyword(java.lang.String.valueOf(Constants.XMLPARSER))
        _INDENTEDPARSER = bindKeyword(java.lang.String.valueOf(Constants.INDENTEDPARSER))
        _READER = bindKeyword(java.lang.String.valueOf(Constants.READER))
        _SIMPLESYMBOL = bindKeyword(java.lang.String.valueOf(Constants.SIMPLESYMBOL))
        _URISYMBOL = bindKeyword(java.lang.String.valueOf(Constants.URISYMBOL))
        _STRING = bindKeyword(java.lang.String.valueOf(Constants.STRING))
        _WRITER = bindKeyword(java.lang.String.valueOf(Constants.WRITER))

        _TRIPLE = bindKeyword(java.lang.String.valueOf(Constants.TRIPLE))
        _GRAPH = bindKeyword(java.lang.String.valueOf(Constants.GRAPH))
        _TYPE = bindKeyword(java.lang.String.valueOf(Constants.TYPE))
        _GLOBALGRAPH = bindKeyword(java.lang.String.valueOf(Constants.GLOBALGRAPH))

        _SUBCLASSOF = bindKeyword(java.lang.String.valueOf(Constants.SUBCLASSOF))
        _DYNAMICSYMBOLREF = bindKeyword(java.lang.String.valueOf(Constants.DYNAMICSYMBOLREF))
        _NAME = bindKeyword(java.lang.String.valueOf(Constants.NAME))
        _PLING = bindKeyword(java.lang.String.valueOf(Constants.PLING))
        _SOURCE = bindKeyword(java.lang.String.valueOf(Constants.SOURCE))

        _SUBJECT = bindKeyword(java.lang.String.valueOf(Constants.SUBJECT))
        _SUBJECTS = bindKeyword(java.lang.String.valueOf(Constants.SUBJECTS))
        _PREDICATE = bindKeyword(java.lang.String.valueOf(Constants.PREDICATE))
        _OBJECT = bindKeyword(java.lang.String.valueOf(Constants.OBJECT))
        _JAVAWRAPPER = bindKeyword(java.lang.String.valueOf(Constants.JAVAWRAPPER))
        _JAVACTOR = bindKeyword(java.lang.String.valueOf(Constants.JAVACTOR))
        _JAVAMETHOD = bindKeyword(java.lang.String.valueOf(Constants.JAVAMETHOD))
        _JAVASTATICMETHOD = bindKeyword(java.lang.String.valueOf(Constants.JAVASTATICMETHOD))
        _JAVACLASS = bindKeyword(java.lang.String.valueOf(Constants.JAVACLASS))
        _PIPE = bindKeyword(java.lang.String.valueOf(Constants.PIPE))
        _PROCEDUREMISSING =
            internSymbol(PrefixSymbol(Constants.PREFIX_SYSTEM, Constants.PROCEDUREMISSING, "sys")) as SimpleSymbol?
        _BISCUIT = bindKeyword(java.lang.String.valueOf(Constants.BISCUIT))
        _TAILCALL = bindKeyword(java.lang.String.valueOf(Constants.TAILCALL))
    }

    private fun bindKeyword(name: String): SimpleSymbol {
        val sym = SimpleSymbol(name)
        _table.put(name, sym)
        return sym
    }

    @kotlin.Throws(GenyrisException::class)
    fun lookupString(newSym: String?): SimpleSymbol? {
        if (_table.containsKey(newSym)) {
            return _table.get(newSym) as SimpleSymbol?
        } else {
            throw GenyrisException("symbol not found in symbol table " + newSym)
        }
    }

    override fun internString(newSym: String): Symbol {
        if (_table.containsKey(newSym)) {
            return _table.get(newSym) as Symbol
        } else {
            val sym: Symbol = Symbol.Companion.symbolFactory(newSym, false)
            _table.put(newSym, sym)
            return sym
        }
    }

    override fun internSymbol(newSym: Symbol): Symbol {
        if (_table.containsKey(newSym.getPrintName())) {
            return _table.get(newSym.getPrintName()) as Symbol?
        } else {
            _table.put(newSym.getPrintName(), newSym)
            return newSym
        }
    }

    @kotlin.Throws(GenyrisException::class)
    override fun internAny(x: Exp): Symbol {
        if (x is Symbol) {
            return internSymbol(x)
        }
        if (x is StrinG) {
            return internString(x.toString())
        }
        if (x is Bignum) {
            return internString(x.bigDecimalValue().toString())
        } else {
            throw GenyrisException("cannot intern a " + x.getClass().getName())
        }
    }

    override fun NIL(): SimpleSymbol {
        return NIL
    }

    val symbolsList: Exp
        get() {
            val head: Pair
            var tail: Pair
            val iter: MutableIterator<*> = _table.values().iterator()
            var key =
                iter.next() as Exp? // Safe to assume symbol table is never empty
            tail = Pair(key, NIL)
            head = tail
            while (iter.hasNext()) {
                key = iter.next() as Exp?
                val newItem = Pair(key, NIL)
                tail.setCdr(newItem)
                tail = newItem
            }
            return head
        }

    val symbolsAsListOfExp: MutableList<Exp?>
        get() {
            val retval = ArrayList<Exp?>()
            retval.addAll(_table.values())
            return retval
        }

    val symbolsListAsListOfStrings: MutableList<String?>
        get() {
            val retval = ArrayList<String?>()
            retval.addAll(_table.keySet())
            return retval
        }

    //
    // The following methods exist to eliminate use of internString in clients.
    // Added after performance tests.
    //
    override fun SELF(): SimpleSymbol? {
        return _self
    }

    override fun CLASSES(): SimpleSymbol? {
        return _classes
    }

    override fun CLASSNAME(): SimpleSymbol? {
        return _classname
    }

    override fun DYNAMIC_SYMBOL(): SimpleSymbol? {
        return _dynamic
    }

    override fun LEFT(): SimpleSymbol? {
        return _left
    }

    override fun LENGTH(): SimpleSymbol? {
        return _length
    }

    override fun RIGHT(): SimpleSymbol? {
        return _right
    }

    override fun SUPERCLASSES(): SimpleSymbol? {
        return _superclasses
    }

    override fun REST(): SimpleSymbol? {
        return _rest
    }

    override fun DICT(): SimpleSymbol? {
        return _dict
    }

    override fun DICTIONARY(): SimpleSymbol? {
        return _dictionary
    }

    override fun TRUE(): SimpleSymbol? {
        return _true
    }

    override fun FALSE(): SimpleSymbol? {
        return _false
    }

    override fun VARS(): SimpleSymbol? {
        return _vars
    }

    override fun STANDARDCLASS(): SimpleSymbol? {
        return _standardclass
    }

    override fun SUBCLASSES(): SimpleSymbol? {
        return _subclasses
    }

    override fun THING(): SimpleSymbol? {
        return _thing
    }

    override fun VALIDATE(): SimpleSymbol? {
        return _validate
    }

    override fun LAMBDA(): SimpleSymbol? {
        return _lambda
    }

    override fun LAMBDAM(): SimpleSymbol? {
        return _lambdam
    }

    override fun LAMBDAQ(): SimpleSymbol? {
        return _lambdaq
    }

    override fun EOF(): SimpleSymbol? {
        return _eof
    }

    override fun SQUARE(): SimpleSymbol? {
        return _square
    }

    override fun CURLY(): SimpleSymbol? {
        return _curly
    }

    override fun TEMPLATE(): SimpleSymbol? {
        return _template
    }

    override fun QUOTE(): SimpleSymbol? {
        return _quote
    }

    override fun DOLLAR(): SimpleSymbol? {
        return _dollar
    }

    override fun DOLLAR_AT(): SimpleSymbol? {
        return _dollar_at
    }

    override fun PREFIX(): SimpleSymbol? {
        return _prefix
    }

    override fun BIGNUM(): SimpleSymbol? {
        return _BIGNUM
    }

    override fun EAGERPROC(): SimpleSymbol? {
        return _EAGERPROC
    }

    override fun LAZYPROC(): SimpleSymbol? {
        return _LAZYPROC
    }

    override fun PAIR(): SimpleSymbol? {
        return _PAIR
    }

    override fun PAIREQUAL(): SimpleSymbol? {
        return _PAIREQUAL
    }

    override fun PAIRSOURCE(): SimpleSymbol? {
        return _PAIRSOURCE
    }

    override fun PARENPARSER(): SimpleSymbol? {
        return _PARENPARSER
    }

    override fun XMLPARSER(): SimpleSymbol? {
        return _XMLPARSER
    }

    override fun READER(): SimpleSymbol? {
        return _READER
    }

    override fun SIMPLESYMBOL(): SimpleSymbol? {
        return _SIMPLESYMBOL
    }

    override fun URISYMBOL(): SimpleSymbol? {
        return _URISYMBOL
    }

    override fun STRING(): SimpleSymbol? {
        return _STRING
    }

    override fun WRITER(): SimpleSymbol? {
        return _WRITER
    }

    override fun GRAPH(): SimpleSymbol? {
        return _GRAPH
    }

    override fun TRIPLE(): SimpleSymbol? {
        return _TRIPLE
    }

    override fun TYPE(): SimpleSymbol? {
        return _TYPE
    }

    override fun SUBCLASSOF(): SimpleSymbol? {
        return _SUBCLASSOF
    }

    override fun DYNAMICSYMBOLREF(): SimpleSymbol? {
        return _DYNAMICSYMBOLREF
    }

    override fun SEMI(): SimpleSymbol? {
        return _SEMI
    }

    override fun PLING(): SimpleSymbol? {
        return _PLING
    }

    override fun SOURCE(): SimpleSymbol? {
        return _SOURCE
    }

    override fun NAME(): SimpleSymbol? {
        return _NAME
    }

    override fun SUBJECT(): SimpleSymbol? {
        return _SUBJECT
    }

    override fun SUBJECTS(): SimpleSymbol? {
        return null
    }

    override fun PREDICATE(): SimpleSymbol? {
        return _PREDICATE
    }

    override fun OBJECT(): SimpleSymbol? {
        return _OBJECT
    }

    override fun JAVAWRAPPER(): SimpleSymbol? {
        return _JAVAWRAPPER
    }

    override fun JAVACTOR(): SimpleSymbol? {
        return _JAVACTOR
    }

    override fun JAVAMETHOD(): SimpleSymbol? {
        return _JAVAMETHOD
    }

    override fun JAVASTATICMETHOD(): SimpleSymbol? {
        return _JAVASTATICMETHOD
    }

    override fun JAVACLASS(): SimpleSymbol? {
        return _JAVACLASS
    }

    override fun PIPE(): SimpleSymbol? {
        return _PIPE
    }

    override fun INDENTPARSER(): SimpleSymbol? {
        return _INDENTEDPARSER
    }

    override fun PROCEDUREMISSING(): SimpleSymbol? {
        return _PROCEDUREMISSING
    }

    override fun GLOBALGRAPH(): SimpleSymbol? {
        return _GLOBALGRAPH
    }

    override fun LINENUMBER(): SimpleSymbol? {
        return _lineNumber
    }

    override fun FILENAME(): SimpleSymbol? {
        return _filename
    }

    override fun BISCUIT(): SimpleSymbol? {
        return _BISCUIT
    }

    override fun TAILCALL(): SimpleSymbol? {
        return _TAILCALL
    }
}
