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

import org.genyris.classes.BuiltinClasses;
import org.genyris.core.Constants;
import org.genyris.core.Dictionary;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.NilSymbol;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.Symbol;
import org.genyris.core.SymbolTable;
import org.genyris.dl.TripleSet;
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
    TripleSet _globalDescriptions;

    Writer _defaultOutput;

    public NilSymbol NIL;

    public Interpreter() throws GenyrisException {
        NIL = new NilSymbol();
        _table = new SymbolTable();
        _table.init(NIL);
        _globalEnvironment = new StandardEnvironment(this.getSymbolTable(), NIL);
        _globalDescriptions = new TripleSet();
        Dictionary SYMBOL = new Dictionary(_globalEnvironment);
        _defaultOutput = new OutputStreamWriter(System.out);
        {
            // Circular references between symbols and classnames require manual
            // bootstrap here:
            SYMBOL.defineVariableRaw(_table.CLASSNAME(), _table.SIMPLESYMBOL());
        }
        defineConstantSymbols();

        BuiltinClasses.init(_globalEnvironment);

        LoadFunction.bindFunctionsAndMethods(this);

        ClassloaderFunctions.bindFunctionsAndMethods(this);

        bindAllJavaFunctionsFromScript();
    }

    private void defineConstantSymbols() throws GenyrisException {
        _globalEnvironment.defineVariable(NIL, NIL);
        _globalEnvironment.defineVariable(_table.TRUE(), _table.TRUE());
        _globalEnvironment.defineVariable(_table.FALSE(), _table.FALSE());
        _globalEnvironment.defineVariable(_table.EOF(), _table.EOF());
        _globalEnvironment.defineVariable(
                _table.internString(Constants.STDOUT), new WriterStream(
                        new PrintWriter(System.out)));
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
            env.defineVariable(nameSymbol, new EagerProcedure(env, null,
                    (ApplicableFunction) proc));
        } else {
            env.defineVariable(nameSymbol, new LazyProcedure(env, null,
                    (ApplicableFunction) proc));
        }
    }

    public void bindMethodInstance(String className, ApplicableFunction proc)
            throws UnboundException, GenyrisException {
        Dictionary stringClass = (Dictionary) _globalEnvironment
                .lookupVariableValue(_table.internString(className));
             Symbol nameSymbol = _table.internString(proc.getName());
            stringClass.defineVariableRaw(nameSymbol, new EagerProcedure(
                    stringClass, null, (ApplicableFunction) proc));

    }

    public Exp bindAllJavaFunctionsFromScript() throws GenyrisException {
        return SourceLoader.loadScriptFromClasspath(this,
                "org/genyris/load/boot/bind-compiled-functions.lin", (Writer) new NullWriter());
    }

    public Exp init(boolean verbose) throws GenyrisException {
        return SourceLoader.loadScriptFromClasspath(this,
                "org/genyris/load/boot/init.lin", verbose ? _defaultOutput
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

    public Symbol intern(String name) {
        return _table.internString(name);
    }

    public Symbol intern(SimpleSymbol name) {
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
                throw new GenyrisException("Method not found: "
                        + BIND_FUNCTIONS_AND_METHODS);
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
}
