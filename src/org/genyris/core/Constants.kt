// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core


object Constants {
    // Lexical marks
    const val COMMENTCHAR: Char = '#'
    const val LISPCOMMENTCHAR: Char = ';'
    const val BQUOTECHAR: Char = '`'
    const val QUOTECHAR: Char = '^'
    const val DOLLARCHAR: Char = '$'
    const val ATCHAR: Char = '@'
    const val CDRCHAR: Char = '='
    const val LISPCDRCHAR: Char = '.'
    const val LISPDYNACHAR: Char = '$'
    const val DYNAMICSCOPECHAR2: Char = '.'
    const val SYMBOLESCAPE: Char = '|'

    // Language Keywords
    const val NIL: String = "nil"
    const val CLASSNAME: String = "classname"
    const val CLASSES: String = "classes"
    const val SUPERCLASSES: String = "superclasses"
    const val SUBCLASSES: String = "subclasses"
    const val SELF: String = "self"
    const val VALIDATE: String = "valid?"
    const val LAMBDA: String = "lambda"
    const val LAMBDAQ: String = "lambdaq"
    const val LAMBDAM: String = "lambdam"
    const val DICT: String = "dict"
    const val DICTIONARY: String = "Dictionary"
    const val DOLLAR: String = "dollar"
    const val DOLLAR_AT: String = "dollar-at"
    const val REST: String = "&rest"
    const val PRINTWITHEQ: String = "PRINTWITHEQ"
    const val STANDARDCLASS: String = "StandardClass"
    const val EOF: String = "EOF"
    const val TEMPLATE: String = "template"
    const val SQUARE: String = "squareBracket"
    const val CURLY: String = "curlyBracket"

    const val THING: String = "Thing"
    const val SIMPLESYMBOL: String = "SimpleSymbol"
    const val URISYMBOL: String = "URISymbol"
    const val LAZYPROCEDURE: String = "LazyProcedure"
    const val EAGERPROCEDURE: String = "EagerProcedure"
    const val BIGNUM: String = "Bignum"
    const val PAIR: String = "Pair"
    const val PAIREQUAL: String = "PairEqual"
    const val PAIRESOURCE: String = "PairSource"
    const val STRING: String = "String"
    const val VARS: String = "vars"
    const val TRUE: String = "true"
    const val CLOSURE: String = "Closure"
    const val LEFT: String = "left"
    const val RIGHT: String = "right"
    const val LINENUMBER: String = "line-number"
    const val FILENAME: String = "filename"

    const val SLICE: String = "slice"
    const val JOIN: String = "join"
    const val SPLIT: String = "split"
    const val TOINTS: String = "toInts"
    const val FROMJSON: String = "fromJSON"
    const val TOJSON: String = "toJSON"
    const val FROMINTS: String = "fromInts"
    const val TOLOWERCASE: String = "toLowerCase"
    const val CONCAT: String = "+"
    const val MATCH: String = "match"
    const val REGEX: String = "regex"
    const val LENGTH: String = "length"
    const val FILE: String = "File"
    const val WRITER: String = "Writer"
    const val PIPE: String = "Pipe"
    const val SYSTEM: String = "System"
    const val OS: String = "os"
    const val EXEC: String = "exec"
    const val LISTOFLINES: String = "ListOfLines"
    const val READER: String = "Reader"
    const val STRINGFORMATSTREAM: String = "StringFormatStream"
    const val STDOUT: String = "stdout"
    const val STDERR: String = "stderr"
    const val ABSTRACTPARSER: String = "AbstractParser"
    const val INDENTEDPARSER: String = "IndentedParser"
    const val PARENPARSER: String = "ParenParser"
    const val XMLPARSER: String = "XMLParser"
    const val STDIN: String = "stdin"
    const val PREFIX: String = "@ns"
    const val DYNAMIC_SYMBOL: String = "dynamic-symbol-value"
    const val GENYRIS: String = "http://www.genyris.org/lang/"
    const val ARGV: String = "argv"
    const val SCRIPTDIR: String = "script-directory"
    val WEB: String = GENYRIS + "web#"
    const val SYMBOL: String = "Symbol"
    const val FALSE: String = "false"
    const val QUOTE: String = "quote"
    const val BACKQUOTE: String = "backquote"
    const val TRIPLE: String = "Triple"
    const val GRAPH: String = "Graph"
    const val TYPE: String = "type"

    const val PREFIX_UTIL: String = "http://www.genyris.org/lang/utilities#"
    const val PREFIX_WEB: String = "http://www.genyris.org/lang/web#"
    const val PREFIX_SYNTAX: String = "http://www.genyris.org/lang/syntax#"
    const val PREFIX_SYSTEM: String = "http://www.genyris.org/lang/system#"
    const val PREFIX_TYPES: String = "http://www.genyris.org/lang/types#"
    const val PREFIX_TASK: String = "http://www.genyris.org/lang/task#"
    const val PREFIX_DATE: String = "http://www.genyris.org/lang/date#"
    const val PREFIX_JAVA: String = "http://www.genyris.org/lang/java#"

    const val SUBCLASSOF: String = "subClassOf"
    const val DYNAMICSYMBOLREF: String = "DynamicSymbolRef"
    const val FORMAT: String = "format"
    const val NAME: String = "name"
    const val PLING: String = "pling"
    const val SOURCE: String = "source"
    const val SUBJECT: String = "subject"
    const val SUBJECTS: String = "subjects"
    const val PREDICATE: String = "predicate"
    const val OBJECT: String = "object"
    const val JAVA: String = "Java"
    const val JAVAWRAPPER: String = "JavaWrapper"
    const val JAVACTOR: String = "JavaConstructor"
    const val JAVAMETHOD: String = "JavaMethod"
    const val JAVASTATICMETHOD: String = "JavaStaticMethod"
    val JAVACLASS: String = PREFIX_JAVA + "class"
    const val PROCEDUREMISSING: String = "procedure-missing"
    const val GLOBALGRAPH: String = "*global-graph*"
    const val ATFILE: String = "@FILE"
    const val ATLINE: String = "@LINE"
    const val BISCUIT: String = "Biscuit"
    const val TAILCALL: String = "TailCall"
}
