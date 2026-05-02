package org.genyris.core

import org.genyris.exception.GenyrisException

interface Internable {
    fun internSymbol(newSym: Symbol): Symbol
    fun internString(newname: String): Symbol

    @kotlin.Throws(GenyrisException::class)
    fun internAny(x: Exp): Symbol
    fun BIGNUM(): SimpleSymbol?
    fun BISCUIT(): SimpleSymbol?
    fun CLASSES(): SimpleSymbol?
    fun CLASSNAME(): SimpleSymbol?
    fun CURLY(): SimpleSymbol?
    fun DICT(): SimpleSymbol?
    fun DICTIONARY(): SimpleSymbol?
    fun DOLLAR(): SimpleSymbol?
    fun DOLLAR_AT(): SimpleSymbol?
    fun DYNAMIC_SYMBOL(): SimpleSymbol?
    fun DYNAMICSYMBOLREF(): SimpleSymbol?
    fun EAGERPROC(): SimpleSymbol?
    fun EOF(): SimpleSymbol?
    fun FALSE(): SimpleSymbol?
    fun FILENAME(): SimpleSymbol?
    fun GLOBALGRAPH(): SimpleSymbol?
    fun GRAPH(): SimpleSymbol?
    fun INDENTPARSER(): SimpleSymbol?
    fun JAVACLASS(): SimpleSymbol?
    fun JAVACTOR(): SimpleSymbol?
    fun JAVAMETHOD(): SimpleSymbol?
    fun JAVASTATICMETHOD(): SimpleSymbol?
    fun JAVAWRAPPER(): SimpleSymbol?
    fun LAMBDA(): SimpleSymbol?
    fun LAMBDAM(): SimpleSymbol?
    fun LAMBDAQ(): SimpleSymbol?
    fun LAZYPROC(): SimpleSymbol?
    fun LEFT(): SimpleSymbol?
    fun LENGTH(): SimpleSymbol?
    fun LINENUMBER(): SimpleSymbol?
    fun NAME(): SimpleSymbol?
    fun NIL(): SimpleSymbol?
    fun OBJECT(): SimpleSymbol?
    fun PAIR(): SimpleSymbol?
    fun PAIREQUAL(): SimpleSymbol?
    fun PAIRSOURCE(): SimpleSymbol?
    fun PARENPARSER(): SimpleSymbol?
    fun PIPE(): SimpleSymbol?
    fun PLING(): SimpleSymbol?
    fun PREDICATE(): SimpleSymbol?
    fun PREFIX(): SimpleSymbol?
    fun PROCEDUREMISSING(): SimpleSymbol?
    fun QUOTE(): SimpleSymbol?
    fun READER(): SimpleSymbol?
    fun REST(): SimpleSymbol?
    fun RIGHT(): SimpleSymbol?
    fun SELF(): SimpleSymbol?
    fun SEMI(): SimpleSymbol?
    fun SIMPLESYMBOL(): SimpleSymbol?
    fun SOURCE(): SimpleSymbol?
    fun SQUARE(): SimpleSymbol?
    fun STANDARDCLASS(): SimpleSymbol?
    fun STRING(): SimpleSymbol?
    fun SUBCLASSES(): SimpleSymbol?
    fun SUBCLASSOF(): SimpleSymbol?
    fun SUBJECT(): SimpleSymbol?
    fun SUBJECTS(): SimpleSymbol?
    fun SUPERCLASSES(): SimpleSymbol?
    fun TAILCALL(): SimpleSymbol?
    fun TEMPLATE(): SimpleSymbol?
    fun THING(): SimpleSymbol?
    fun TRIPLE(): SimpleSymbol?
    fun TRUE(): SimpleSymbol?
    fun TYPE(): SimpleSymbol?
    fun URISYMBOL(): SimpleSymbol?
    fun VALIDATE(): SimpleSymbol?
    fun VARS(): SimpleSymbol?
    fun WRITER(): SimpleSymbol?
    fun XMLPARSER(): SimpleSymbol?
}
