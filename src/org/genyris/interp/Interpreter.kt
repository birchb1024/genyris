// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp

import org.genyris.core.*
import org.genyris.core.Dictionary
import org.genyris.dl.AbstractGraph
import org.genyris.dl.GraphHashSimple
import org.genyris.exception.AccessException
import org.genyris.exception.GenyrisException
import org.genyris.io.*
import org.genyris.io.readerstream.ReaderStream
import org.genyris.io.writerstream.WriterStream
import org.genyris.load.LoadFunction
import org.genyris.load.SourceLoader
import java.io.*
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*
import java.util.function.Predicate

class Interpreter @kotlin.jvm.JvmOverloads constructor(
    val input: InStream? = StdioInStream.Companion.knew(),
    out: OutputStream = System.out
) {
    var _globalEnvironment: StandardEnvironment
    var _table: SymbolTable
    var _theGlobalGraph: AbstractGraph?

    var defaultOutputWriter: Writer?

    var NIL: NilSymbol?

    var debugBackTrace: Stack<Closure?> // TODO make a single stack for these two
        private set
    var debugArgsBackTrace: Stack<Array<Exp?>?> // TODO make a single stack for these two
        private set

    private val _prefixesUsed: MutableMap<String?, String?> // collect all prefixes - used

    // for output.
    init {
        _prefixesUsed = HashMap<String?, String?>()
        NIL = NilSymbol()
        this.debugBackTrace = Stack<Closure?>()
        this.debugArgsBackTrace = Stack<Array<Exp?>?>()
        _table = SymbolTable()
        _table.init(NIL)
        _globalEnvironment = StandardEnvironment(this.symbolTable, NIL)
        _theGlobalGraph = GraphHashSimple()
        val SYMBOL = Dictionary(_globalEnvironment)
        this.defaultOutputWriter = OutputStreamWriter(out)
        run {
            // Circular references between symbols and classnames require manual
            // bootstrap here:
            SYMBOL.defineVariableRaw(_table.CLASSNAME(), _table.SIMPLESYMBOL())
        }
        _globalEnvironment.defineVariable(
            _table.internString(Constants.STDIN),
            ReaderStream(input)
        )
        defineConstantSymbols()

        standardClassInit(_globalEnvironment)

        LoadFunction.Companion.bindFunctionsAndMethods(this)

        ClassloaderFunctions.Companion.bindFunctionsAndMethods(this)

        bindAllJavaFunctionsFromScript()
    }

    @kotlin.Throws(GenyrisException::class)
    private fun defineConstantSymbols() {
        _globalEnvironment.defineLexicalVariable(NIL, NIL)
        _globalEnvironment.defineVariable(_table.TRUE(), _table.TRUE())
        _globalEnvironment.defineVariable(_table.FALSE(), _table.FALSE())
        _globalEnvironment.defineVariable(_table.EOF(), _table.EOF())
        _globalEnvironment.defineVariable(
            _table.internString(Constants.STDOUT),
            WriterStream(PrintWriter(System.out))
        )
        _globalEnvironment.defineVariable(
            _table.internString(Constants.STDERR),
            WriterStream(PrintWriter(System.err))
        )
        _globalEnvironment.defineVariable(_table.GLOBALGRAPH(), _theGlobalGraph)
    }

    @kotlin.Throws(GenyrisException::class)
    fun bindGlobalProcedureInstance(proc: ApplicableFunction) {
        bindProcedure(_globalEnvironment, proc)
    }

    @kotlin.Throws(GenyrisException::class)
    private fun bindProcedure(env: Environment, proc: ApplicableFunction) {
        var sym: Symbol? = null
        if (proc._nameSymbol != null) {
            sym = _table.internSymbol(proc._nameSymbol)
        } else {
            sym = _table.internString(proc.getName())
        }

        if (proc.isEager()) {
            env.defineVariable(
                sym, EagerProcedure(
                    env, NIL,
                    proc
                )
            )
        } else {
            env.defineVariable(
                sym, LazyProcedure(
                    env, NIL,
                    proc
                )
            )
        }
    }

    @kotlin.Throws(UnboundException::class, GenyrisException::class)
    fun bindMethodInstance(className: String?, proc: ApplicableFunction) {
        val stringClass = _globalEnvironment
            .lookupVariableValue(_table.internString(className)) as Dictionary
        val nameSymbol = _table.internString(proc.getName())
        stringClass.defineVariableRaw(
            nameSymbol, EagerProcedure(
                stringClass, NIL,
                proc
            )
        )
    }

    @kotlin.Throws(GenyrisException::class)
    fun bindAllJavaFunctionsFromScript(): Exp {
        return SourceLoader.loadScriptFromClasspath(
            this.globalEnv,
            this.symbolTable, "org/genyris/load/boot/bind-compiled-functions.g",
            NullWriter() as Writer
        )
    }

    @kotlin.Throws(GenyrisException::class)
    fun init(verbose: Boolean, scriptDirectoryPath: String?): Exp {
        val scriptDirVar = intern(PrefixSymbol(Constants.PREFIX_SYSTEM, "script-directory", "sys"))
        this.globalEnv.defineVariable(scriptDirVar, StrinG(scriptDirectoryPath))
        return initAux(verbose)
    }

    @kotlin.Throws(GenyrisException::class)
    fun initAux(verbose: Boolean): Exp {
        val nullW: Writer = NullWriter()
        val retval = SourceLoader.loadScriptFromClasspath(
            this.globalEnv, this.symbolTable, "org/genyris/load/boot/init.g",
            if (verbose) this.defaultOutputWriter else nullW
        )
        try {
            nullW.close()
        } catch (ignored: IOException) {
        }
        return retval
    }

    fun newParser(input: InStream?): Parser {
        return ParserSource(_table, input)
    }

    @kotlin.Throws(GenyrisException::class)
    fun evalInGlobalEnvironment(expression: Exp): Exp? {
        return expression.evalCatchOverFlow(_globalEnvironment)
    }

    val globalEnv: Environment
        get() = _globalEnvironment

    val symbolTable: Internable
        get() = _table

    fun intern(name: String?): Symbol? {
        return _table.internString(name)
    }

    fun intern(name: Symbol): Symbol? {
        return _table.internSymbol(name)
    }

    val symbolsList: Exp?
        get() = _table.getSymbolsList()

    @kotlin.Throws(GenyrisException::class)
    fun lookupGlobalFromString(`var`: String?): Exp? {
        return _globalEnvironment.lookupVariableValue(_table.internString(`var`))
    }

    @kotlin.Throws(GenyrisException::class)
    fun loadClassByName(classname: String?) {
        try {
            val toInitialise = Class.forName(classname)
            val binder: Method? = findMethod(BIND_FUNCTIONS_AND_METHODS, toInitialise)
            if (binder == null) {
                throw GenyrisException(
                    "Method not found: "
                            + BIND_FUNCTIONS_AND_METHODS
                )
            }
            binder.invoke(null, *arrayOf<Any>(this))
        } catch (e: ClassNotFoundException) {
            throw GenyrisException("ClassNotFoundException: " + e.getMessage())
        } catch (e: IllegalArgumentException) {
            throw GenyrisException("IllegalArgumentException: " + e.getMessage())
        } catch (e: IllegalAccessException) {
            throw GenyrisException("IllegalAccessException: " + e.getMessage())
        } catch (e: InvocationTargetException) {
            throw GenyrisException("InvocationTargetException: " + e.getMessage())
        }
    }

    fun debugStackPush(proc: Closure?, args: Array<Exp?>) {
        debugBackTrace.push(proc)
        debugArgsBackTrace.push(args.clone())
    }

    fun debugStackPop() {
        if (debugBackTrace.empty() || debugArgsBackTrace.empty()) {
            return
        }
        debugBackTrace.pop()
        debugArgsBackTrace.pop()
    }

    fun resetDebugBackTrace() {
        this.debugBackTrace = Stack<Closure?>()
        this.debugArgsBackTrace = Stack<Array<Exp?>?>()
    }

    @kotlin.Throws(GenyrisException::class)
    fun evalStringInGlobalEnvironment(script: String): Exp? {
        var truncatedScript: String? = script
        if (script.length() > 9) truncatedScript = script.substring(0, 10) + "..."
        val `is`: InStream = UngettableInStream(
            ConvertEofInStream(
                IndentStream(
                    UngettableInStream(ReaderInStream(StringReader(script), truncatedScript)), true
                )
            )
        )

        val parser = newParser(`is`)
        parser.setUsualPrefixes(this)

        val expression = parser.read()
        val result = evalInGlobalEnvironment(expression)
        return result
    }

    fun applyPrefixes(
        prefixTable: MutableMap<String?, String?>,
        symbols: MutableList<String>
    ): MutableList<String?> {
        val retval = ArrayList<String?>()
        for (s in symbols) {
            var item_result: String? = s
            for (entry in prefixTable.entrySet()) {
                val key: String? = entry.getKey()
                val value: String = entry.getValue()
                if (s.startsWith(value)) {
                    item_result = s.replace(value, key + ":")
                    break
                }
            }
            retval.add(item_result)
        }
        return retval
    }

    fun getBoundSymbolsAsListOfStrings(env: Environment?): MutableList<String?> {
        //
        //
        //
        var env = env
        val boringProperties = arrayOf<String?>(".classes", ".name", ".self", ".source", ".vars")
        if (env == null) {
            env = this._globalEnvironment
        }
        val symbols = _table.getSymbolsAsListOfExp()
        val retval = ArrayList<String>()
        for (se in symbols) {
            val s = se as Symbol
            if (env.isBound(s as Symbol?)) {
                val name = s.getPrintName()
                retval.add(name)
                try {
                    var varList = env.lookupVariableValue(s).dir(_table)
                    while (varList !== NIL) {
                        val the_symbol = (varList.car() as Symbol).getPrintName()
                        val boringPropertiesStream = Arrays.stream<String?>(boringProperties)
                        if (!boringPropertiesStream.anyMatch(Predicate { anObject: String? -> the_symbol.equals(anObject) })) {
                            retval.add(name + the_symbol)
                        }
                        varList = varList.cdr()
                    }
                } catch (e: UnboundException) {
                    System.out.println(e.getMessage())
                } catch (e: AccessException) {
                    System.out.println(e.getMessage())
                }
            }
        }
        return applyPrefixes(_prefixesUsed, retval)
    }

    @kotlin.Throws(AccessException::class)
    private fun compareName(`var`: Symbol, name: String?): Boolean {
        return `var`.getPrintName() == name
    }

    fun collectPrefix(pre: String?, full: String?) {
        _prefixesUsed.put(pre, full)
    }

    val debugBackTraceAsList: Exp?
        get() {
            var retval: Exp? = NIL
            val S: Int = Math.min(debugArgsBackTrace.size(), debugBackTrace.size())
            for (i in 0..<S) {
                val frame = debugBackTrace.get(i)!!.getPrintableFrame(NIL)
                val args = debugArgsBackTrace.get(i)!!
                retval = Pair(Pair(frame, arrayToList(args)), retval)
            }
            return retval
        }

    fun arrayToList(args: Array<Exp?>): Exp? {
        var retval: Exp? = NIL
        for (i in args.indices.reversed()) {
            retval = Pair(args[i], retval)
        }
        return retval
    }


    companion object {
        private const val BIND_FUNCTIONS_AND_METHODS = "bindFunctionsAndMethods"

        private fun findMethod(name: String?, clazz: Class<*>): Method? {
            val methods = clazz.getMethods()
            for (i in methods.indices) {
                if (methods[i].getName() == name) {
                    return methods[i]
                }
            }
            return null
        }

        @kotlin.Throws(GenyrisException::class)
        fun standardClassInit(env: Environment) {
            val standardClassDict: StandardClass?
            val table = env.getSymbolTable()
            val CLASSNAME = table.CLASSNAME()
            run {
                standardClassDict = StandardClass(CLASSNAME, table.STANDARDCLASS(), env)
                standardClassDict.defineVariableRaw(
                    env.getSymbolTable().SUBCLASSES(),
                    table.NIL()
                )
                standardClassDict.defineVariableRaw(
                    env.getSymbolTable().SUPERCLASSES(),
                    table.NIL()
                )
            }
            env.defineVariable(table.STANDARDCLASS(), standardClassDict)

            val THING: StandardClass = StandardClass.Companion.mkClass("Thing", env, null)
            val builtin: StandardClass = StandardClass.Companion.mkClass("Builtin", env, THING)
            val dictionary: StandardClass = StandardClass.Companion.mkClass(
                Constants.DICTIONARY, env,
                builtin
            )
            standardClassDict!!.addSuperClass(dictionary)
            StandardClass.Companion.mkClass(Constants.BIGNUM, env, builtin)
            StandardClass.Companion.mkClass(Constants.BISCUIT, env, builtin)
            StandardClass.Companion.mkClass(Constants.STRING, env, builtin)
            StandardClass.Companion.mkClass(Constants.FILE, env, builtin)
            StandardClass.Companion.mkClass(Constants.READER, env, builtin)
            StandardClass.Companion.mkClass(Constants.WRITER, env, builtin)
            StandardClass.Companion.mkClass(Constants.PIPE, env, builtin)
            StandardClass.Companion.mkClass(Constants.SYSTEM, env, builtin)
            StandardClass.Companion.mkClass(Constants.OS, env, builtin)
            val abstractParser: StandardClass = StandardClass.Companion.mkClass(
                Constants.ABSTRACTPARSER,
                env, builtin
            )
            StandardClass.Companion.mkClass(Constants.INDENTEDPARSER, env, abstractParser)
            StandardClass.Companion.mkClass(Constants.PARENPARSER, env, abstractParser)
            StandardClass.Companion.mkClass(Constants.XMLPARSER, env, abstractParser)
            StandardClass.Companion.mkClass(Constants.STRINGFORMATSTREAM, env, builtin)
            StandardClass.Companion.mkClass(Constants.TRIPLE, env, builtin)
            StandardClass.Companion.mkClass(Constants.GRAPH, env, builtin)
            StandardClass.Companion.mkClass(Constants.JAVA, env, builtin)
            StandardClass.Companion.mkClass(Constants.JAVAWRAPPER, env, builtin)
            StandardClass.Companion.mkClass(Constants.JAVACLASS, env, builtin)
            StandardClass.Companion.mkClass(Constants.TAILCALL, env, builtin)

            val symbol: StandardClass = StandardClass.Companion.mkClass(Constants.SYMBOL, env, builtin)
            val closure: StandardClass = StandardClass.Companion.mkClass(Constants.CLOSURE, env, builtin)
            val pair: StandardClass = StandardClass.Companion.mkClass(Constants.PAIR, env, builtin)
            StandardClass.Companion.mkClass(Constants.PAIREQUAL, env, pair)
            StandardClass.Companion.mkClass(Constants.PAIRESOURCE, env, pair)
            StandardClass.Companion.mkClass(Constants.PRINTWITHEQ, env, pair)
            StandardClass.Companion.mkClass(Constants.SIMPLESYMBOL, env, symbol)
            StandardClass.Companion.mkClass(Constants.URISYMBOL, env, symbol)
            StandardClass.Companion.mkClass(Constants.EAGERPROCEDURE, env, closure)
            StandardClass.Companion.mkClass(Constants.LAZYPROCEDURE, env, closure)
            StandardClass.Companion.mkClass(Constants.JAVACTOR, env, closure)
            StandardClass.Companion.mkClass(Constants.JAVAMETHOD, env, closure)
            StandardClass.Companion.mkClass(Constants.JAVASTATICMETHOD, env, closure)
            StandardClass.Companion.mkClass(Constants.LISTOFLINES, env, pair)
            StandardClass.Companion.mkClass(Constants.DYNAMICSYMBOLREF, env, symbol)
        }
    }
}
