// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.genyris.classes.BuiltinClasses;
import org.genyris.classification.IsInstanceFunction;
import org.genyris.core.Constants;
import org.genyris.core.Dictionary;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.NilSymbol;
import org.genyris.core.Symbol;
import org.genyris.core.SymbolTable;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.builtin.BuiltinFunction;
import org.genyris.io.InStream;
import org.genyris.io.NullWriter;
import org.genyris.io.Parser;
import org.genyris.io.ReadFunction;
import org.genyris.io.StdioInStream;
import org.genyris.io.readerstream.ReaderStream;
import org.genyris.io.writerstream.WriterStream;
import org.genyris.load.IncludeFunction;
import org.genyris.load.LoadFunction;
import org.genyris.load.SourceLoader;

public class Interpreter {
    private static final String BIND_FUNCTIONS_AND_METHODS = "bindFunctionsAndMethods";

    StandardEnvironment _globalEnvironment;

    SymbolTable _table;

    Writer _defaultOutput;

    public NilSymbol NIL;

    public Interpreter() throws GenyrisException {
        NIL = new NilSymbol();
        _table = new SymbolTable();
        _table.init(NIL);
        _globalEnvironment = new StandardEnvironment(this.getSymbolTable(), NIL);
        Dictionary SYMBOL = new Dictionary(_globalEnvironment);
        _defaultOutput = new OutputStreamWriter(System.out);
        {
            // Circular references between symbols and classnames require manual
            // bootstrap here:
            SYMBOL.defineVariableRaw(_table.CLASSNAME(), _table.SIMPLESYMBOL());
        }
        defineConstantSymbols();

        BuiltinClasses.init(_globalEnvironment);

        bindAllGlobalFunctions();

        ClassloaderFunctions.bindFunctionsAndMethods(this);

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
    }

    private void bindAllGlobalFunctions() throws GenyrisException {

        BuiltinFunction.bindFunctionsAndMethods(this);

        bindGlobalProcedure(ReadFunction.class);
        bindGlobalProcedure(LoadFunction.class);
        bindGlobalProcedure(IncludeFunction.class);
        bindGlobalProcedure(IsInstanceFunction.class);

    }

    public void bindGlobalProcedure(Class class1) throws GenyrisException {
        bindProcedure(_globalEnvironment, class1);
    }

    private void bindProcedure(Environment env, Class class1)
            throws GenyrisException {
        //
        // Method uses reflection to locate and call the constructor.
        //
        Class[] paramTypes = new Class[] { Interpreter.class };
        try {
            Constructor ctor = class1.getConstructor(paramTypes);
            Object[] args = new Object[] { this };
            ApplicableFunction proc = (ApplicableFunction) ctor
                    .newInstance(args);
            Symbol nameSymbol = _table.internString(proc.getName());

            if (proc.isEager()) {
                env.defineVariable(nameSymbol, new EagerProcedure(env, null,
                        (ApplicableFunction) proc));
            } else {
                env.defineVariable(nameSymbol, new LazyProcedure(env, null,
                        (ApplicableFunction) proc));
            }

        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getMessage());
        } catch (SecurityException e) {
            throw new RuntimeException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage());
        } catch (InstantiationException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void bindMethod(String className, Class class1)
            throws UnboundException, GenyrisException {
        Dictionary stringClass = (Dictionary) _globalEnvironment
                .lookupVariableValue(_table.internString(className));
        //
        // Method uses reflection to locate and call the constructor.
        //
        Class[] paramTypes = new Class[] { Interpreter.class };
        try {
            Constructor ctor = class1.getConstructor(paramTypes);
            Object[] args = new Object[] { this };
            ApplicableFunction proc = (ApplicableFunction) ctor
                    .newInstance(args);
            Symbol nameSymbol = _table.internString(proc.getName());
            stringClass.defineVariableRaw(nameSymbol, new EagerProcedure(
                    stringClass, null, (ApplicableFunction) proc));

        } // TODO DRY
        catch (InvocationTargetException e) {
            throw new RuntimeException(e.getMessage());
        } catch (SecurityException e) {
            throw new RuntimeException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage());
        } catch (InstantiationException e) {
            throw new RuntimeException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
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
        return Evaluator.eval(_globalEnvironment, expression);
    }

    public Writer getDefaultOutputWriter() {
        return _defaultOutput;
    }

    // public Symbol getNil() {
    // return NIL;
    // }

    public Environment getGlobalEnv() {
        return _globalEnvironment;
    }

    public Internable getSymbolTable() {
        return _table;
    }

    public Symbol intern(String name) {
        return _table.internString(name);
    }

    public Symbol intern(Symbol name) {
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
            if(binder == null) {
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
}
