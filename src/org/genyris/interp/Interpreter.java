// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.genyris.core.Constants;
import org.genyris.core.Dictionary;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.NilSymbol;
import org.genyris.core.Pair;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.StandardClass;
import org.genyris.core.StrinG;
import org.genyris.core.Symbol;
import org.genyris.core.SymbolTable;
import org.genyris.dl.Graph;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.io.InStream;
import org.genyris.io.NullWriter;
import org.genyris.io.Parser;
import org.genyris.io.StdioInStream;
import org.genyris.io.readerstream.ReaderStream;
import org.genyris.io.writerstream.WriterStream;
import org.genyris.load.LoadFunction;
import org.genyris.load.SourceLoader;

public class Interpreter {
    private static final String BIND_FUNCTIONS_AND_METHODS = "bindFunctionsAndMethods";

    StandardEnvironment _globalEnvironment;
    SymbolTable _table;
    Graph _globalDescriptions;

    Writer _defaultOutput;

    public NilSymbol NIL;
	private Exp _debugStack;

    
    
    public Interpreter() throws GenyrisException {
        NIL = new NilSymbol();
        _debugStack = NIL;
        _table = new SymbolTable();
        _table.init(NIL);
        _globalEnvironment = new StandardEnvironment(this.getSymbolTable(), NIL);
        _globalDescriptions = new Graph();
        Dictionary SYMBOL = new Dictionary(_globalEnvironment);
        _defaultOutput = new OutputStreamWriter(System.out);
        {
            // Circular references between symbols and classnames require manual
            // bootstrap here:
            SYMBOL.defineVariableRaw(_table.CLASSNAME(), _table.SIMPLESYMBOL());
        }
        defineConstantSymbols();

        standardClassInit(_globalEnvironment);

        LoadFunction.bindFunctionsAndMethods(this);

        ClassloaderFunctions.bindFunctionsAndMethods(this);

        bindAllJavaFunctionsFromScript();
        _debugStack = NIL;
        
    }

    private void defineConstantSymbols() throws GenyrisException {
        _globalEnvironment.defineLexicalVariable(NIL, NIL);
        _globalEnvironment.defineVariable(_table.TRUE(), _table.TRUE());
        _globalEnvironment.defineVariable(_table.FALSE(), _table.FALSE());
        _globalEnvironment.defineVariable(_table.EOF(), _table.EOF());
        _globalEnvironment.defineVariable(
                _table.internString(Constants.STDOUT), new WriterStream(
                        new PrintWriter(System.out)));
        _globalEnvironment.defineVariable(
                _table.internString(Constants.STDERR), new WriterStream(
                        new PrintWriter(System.err)));
        _globalEnvironment.defineVariable(_table.internString(Constants.STDIN),
                new ReaderStream(new StdioInStream()));
        _globalEnvironment.defineVariable(_table.DESCRIPTIONS(), _globalDescriptions);
    }

    public void bindGlobalProcedureInstance(ApplicableFunction proc)
            throws GenyrisException {
        bindProcedure(_globalEnvironment, proc);
    }

    private void bindProcedure(Environment env, ApplicableFunction proc)
            throws GenyrisException {
        Symbol nameSymbol = _table.internString(proc.getName());

        if (proc.isEager()) {
            env.defineVariable(nameSymbol, new EagerProcedure(env, NIL,
                    (ApplicableFunction) proc));
        } else {
            env.defineVariable(nameSymbol, new LazyProcedure(env, NIL,
                    (ApplicableFunction) proc));
        }
    }

    public void bindMethodInstance(String className, ApplicableFunction proc)
            throws UnboundException, GenyrisException {
        Dictionary stringClass = (Dictionary) _globalEnvironment
                .lookupVariableValue(_table.internString(className));
        SimpleSymbol nameSymbol = _table.internString(proc.getName());
            stringClass.defineVariableRaw(nameSymbol, new EagerProcedure(
                    stringClass, NIL, (ApplicableFunction) proc));

    }

    public Exp bindAllJavaFunctionsFromScript() throws GenyrisException {
        return SourceLoader.loadScriptFromClasspath(this.getGlobalEnv(), this.getSymbolTable(),
                "org/genyris/load/boot/bind-compiled-functions.g", (Writer) new NullWriter());
    }

    public Exp init(boolean verbose) throws GenyrisException {
        return SourceLoader.loadScriptFromClasspath(this.getGlobalEnv(), this.getSymbolTable(),
                "org/genyris/load/boot/init.g", verbose ? _defaultOutput
                        : (Writer) new NullWriter());
    }

    public Parser newParser(InStream input) {
        return new Parser(_table, input);
    }

    public Exp evalInGlobalEnvironment(Exp expression) throws GenyrisException {
        return expression.eval(_globalEnvironment);
    }

    public Writer getDefaultOutputWriter() {
        return _defaultOutput;
    }

    public Environment getGlobalEnv() {
        return _globalEnvironment;
    }

    public Internable getSymbolTable() {
        return _table;
    }

    public SimpleSymbol intern(String name) {
        return _table.internString(name);
    }

    public SimpleSymbol intern(SimpleSymbol name) {
        return _table.internSymbol(name);
    }

    public Exp getSymbolsList() {
        return _table.getSymbolsList();
    }

    public Exp lookupGlobalFromString(String var) throws GenyrisException {
        return _globalEnvironment.lookupVariableValue(_table.internString(var));
    }

    public void loadClassByName(String classname) throws GenyrisException {
        try {
            Class toInitialise = Class.forName(classname);
            Method binder = findMethod(BIND_FUNCTIONS_AND_METHODS, toInitialise);
            if (binder == null) {
                throw new GenyrisException("Method not found: " + BIND_FUNCTIONS_AND_METHODS);
            }
            binder.invoke(null, new Object[] { this });

        } catch (ClassNotFoundException e) {
            throw new GenyrisException("ClassNotFoundException: "
                    + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new GenyrisException("IllegalArgumentException: "
                    + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new GenyrisException("IllegalAccessException: "
                    + e.getMessage());
        } catch (InvocationTargetException e) {
            throw new GenyrisException("InvocationTargetException: "
                    + e.getMessage());
        }

    }

    private static Method findMethod(String name, final Class clazz) {
        Method methods[] = clazz.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(name)) {
                return methods[i];
            }
        }
        return null;

    }
    
	public static void standardClassInit(Environment env) throws GenyrisException {
		StandardClass standardClassDict;
		Internable table = env.getSymbolTable();
		SimpleSymbol CLASSNAME = table.CLASSNAME();
		{
			standardClassDict = new StandardClass(CLASSNAME, table.STANDARDCLASS(), env);
			standardClassDict.defineVariableRaw(env.getSymbolTable().SUBCLASSES(), table.NIL());
			standardClassDict.defineVariableRaw(env.getSymbolTable().SUPERCLASSES(), table.NIL());
		}
		env.defineVariable(table.STANDARDCLASS(), standardClassDict);

		StandardClass THING = StandardClass.mkClass("Thing", env, null);
		StandardClass builtin = StandardClass.mkClass("Builtin", env, THING);
		StandardClass dictionary = StandardClass.mkClass(Constants.DICTIONARY, env, builtin);
		standardClassDict.addSuperClass(dictionary);
		StandardClass.mkClass(Constants.BIGNUM, env, builtin);
		StandardClass.mkClass(Constants.STRING, env, builtin);
		StandardClass.mkClass(Constants.FILE, env, builtin);
		StandardClass.mkClass(Constants.READER, env, builtin);
		StandardClass.mkClass(Constants.WRITER, env, builtin);
		StandardClass.mkClass(Constants.PIPE, env, builtin);
		StandardClass.mkClass(Constants.SYSTEM, env, builtin);
		StandardClass abstractParser = StandardClass.mkClass(Constants.ABSTRACTPARSER, env, builtin);
		StandardClass.mkClass(Constants.INDENTEDPARSER, env, abstractParser);
		StandardClass.mkClass(Constants.PARENPARSER, env, abstractParser);
		StandardClass.mkClass(Constants.STRINGFORMATSTREAM, env, builtin);
		StandardClass.mkClass(Constants.TRIPLE, env, builtin);
		StandardClass.mkClass(Constants.GRAPH, env, builtin);
		StandardClass.mkClass(Constants.JAVA, env, builtin);
		StandardClass.mkClass(Constants.JAVAWRAPPER, env, builtin);
		StandardClass.mkClass(Constants.JAVACLASS, env, builtin);

		StandardClass symbol = StandardClass.mkClass(Constants.SYMBOL, env, builtin);
		StandardClass closure = StandardClass.mkClass(Constants.CLOSURE, env, builtin);
		StandardClass pair = StandardClass.mkClass(Constants.PAIR, env, builtin);
		StandardClass.mkClass(Constants.PAIREQUAL, env, builtin);
		StandardClass.mkClass(Constants.PRINTWITHEQ, env, pair);
		StandardClass.mkClass(Constants.SIMPLESYMBOL, env, symbol);
		StandardClass.mkClass(Constants.URISYMBOL, env, symbol);
		StandardClass.mkClass(Constants.EAGERPROCEDURE, env, closure);
		StandardClass.mkClass(Constants.LAZYPROCEDURE, env, closure);
		StandardClass.mkClass(Constants.JAVACTOR, env, closure);
		StandardClass.mkClass(Constants.JAVAMETHOD, env, closure);
		StandardClass.mkClass(Constants.JAVASTATICMETHOD, env, closure);
		StandardClass.mkClass(Constants.LISTOFLINES, env, pair);
		StandardClass.mkClass(Constants.DYNAMICSYMBOLREF, env, symbol);
	}

	public void debugStackPush(Closure proc) {
		_debugStack = new Pair(new StrinG(proc.toString()), _debugStack);		
	}
	public void debugStackPop(Closure proc) {
		try {
			_debugStack = _debugStack.cdr();
		} catch (AccessException ignore) {
		}
	}

	public Exp getDebugBackTrace() {
		return _debugStack;
	}
    
	public Exp resetDebugBackTrace() {
		return _debugStack = NIL;
	}
    
}
