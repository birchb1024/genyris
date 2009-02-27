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
import org.genyris.classes.BuiltinClasses;
import org.genyris.classification.IsInstanceFunction;
import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Lobject;
import org.genyris.core.Lsymbol;
import org.genyris.core.NilSymbol;
import org.genyris.core.SymbolTable;
import org.genyris.exception.GenyrisException;
import org.genyris.format.DisplayFunction;
import org.genyris.format.WriteFunction;
import org.genyris.format.PrintFunction;
import org.genyris.interp.builtin.ApplyFunction;
import org.genyris.interp.builtin.BackquoteFunction;
import org.genyris.interp.builtin.BoundFunction;
import org.genyris.interp.builtin.CarFunction;
import org.genyris.interp.builtin.CdrFunction;
import org.genyris.interp.builtin.ConditionalFunction;
import org.genyris.interp.builtin.ConsFunction;
import org.genyris.interp.builtin.DefFunction;
import org.genyris.interp.builtin.DefMacroFunction;
import org.genyris.interp.builtin.DefineClassFunction;
import org.genyris.interp.builtin.DefineFunction;
import org.genyris.interp.builtin.DynamicSymbolValueFunction;
import org.genyris.interp.builtin.EqFunction;
import org.genyris.interp.builtin.EqualsFunction;
import org.genyris.interp.builtin.EvalFunction;
import org.genyris.interp.builtin.IdentityFunction;
import org.genyris.interp.builtin.LambdaFunction;
import org.genyris.interp.builtin.LambdamFunction;
import org.genyris.interp.builtin.LambdaqFunction;
import org.genyris.interp.builtin.LengthFunction;
import org.genyris.interp.builtin.ListFunction;
import org.genyris.interp.builtin.LoadFunction;
import org.genyris.interp.builtin.ObjectFunction;
import org.genyris.interp.builtin.QuoteFunction;
import org.genyris.interp.builtin.RaiseFunction;
import org.genyris.interp.builtin.RemoveTagFunction;
import org.genyris.interp.builtin.ReplaceCarFunction;
import org.genyris.interp.builtin.ReplaceCdrFunction;
import org.genyris.interp.builtin.ReverseFunction;
import org.genyris.interp.builtin.SetFunction;
import org.genyris.interp.builtin.SymListFunction;
import org.genyris.interp.builtin.SymbolValueFunction;
import org.genyris.interp.builtin.TagFunction;
import org.genyris.interp.builtin.WhileFunction;
import org.genyris.io.InStream;
import org.genyris.io.NullWriter;
import org.genyris.io.Parser;
import org.genyris.io.ReadFunction;
import org.genyris.io.StdioInStream;
import org.genyris.io.StringFormatStream;
import org.genyris.io.file.Gfile;
import org.genyris.io.parser.StreamParser;
import org.genyris.io.readerstream.ReaderStream;
import org.genyris.io.sound.Sound.PlayMethod;
import org.genyris.io.writerstream.WriterStream;
import org.genyris.io.writerstream.WriterStream.CloseMethod;
import org.genyris.io.writerstream.WriterStream.FlushMethod;
import org.genyris.io.writerstream.WriterStream.FormatMethod;
import org.genyris.load.IncludeFunction;
import org.genyris.load.SourceLoader;
import org.genyris.logic.AndFunction;
import org.genyris.logic.NotFunction;
import org.genyris.logic.OrFunction;
import org.genyris.math.DivideFunction;
import org.genyris.math.GreaterThanFunction;
import org.genyris.math.LessThanFunction;
import org.genyris.math.MinusFunction;
import org.genyris.math.MultiplyFunction;
import org.genyris.math.PlusFunction;
import org.genyris.math.PowerFunction;
import org.genyris.math.RemainderFunction;
import org.genyris.string.ConcatMethod;
import org.genyris.string.MatchMethod;
import org.genyris.string.SplitMethod;
import org.genyris.string.LengthMethod;
import org.genyris.system.ExecMethod;
import org.genyris.test.JunitRunnerFunction;
import org.genyris.web.HTTPgetFunction;
import org.genyris.web.KillHTTPDFunction;
import org.genyris.web.SpawnHTTPDFunction;

public class Interpreter {
    StandardEnvironment      _globalEnvironment;
    SymbolTable      _table;
    Writer           _defaultOutput;
    public NilSymbol NIL;
    private Lsymbol  TRUE;

    public Interpreter() throws GenyrisException {
        NIL = new NilSymbol();
        _table = new SymbolTable(this);
        _table.init(NIL);
        _globalEnvironment = new StandardEnvironment(this, NIL);
        NIL.init(_globalEnvironment);
        _table.initEnvironment(_globalEnvironment);
        Lobject SYMBOL = new Lobject(_globalEnvironment);
        _defaultOutput = new OutputStreamWriter(System.out);
        {
            // Circular references between symbols and classnames require manual bootstrap here:
            SYMBOL.defineVariableRaw(_table.internString(Constants.CLASSNAME),
                    _table.internString(Constants.SYMBOL));
        }
        _globalEnvironment.defineVariable(NIL, NIL);
        TRUE = _table.internString("true");
        _globalEnvironment.defineVariable(TRUE, TRUE);
        _globalEnvironment.defineVariable(_table.internString(Constants.EOF),
                _table.internString(Constants.EOF));
        _globalEnvironment.defineVariable(_table.internString(Constants.STDOUT),
                new WriterStream(new PrintWriter(System.out)));
        _globalEnvironment.defineVariable(_table.internString(Constants.STDIN),
                new ReaderStream(new StdioInStream()));
        BuiltinClasses.init(_globalEnvironment);
        
        getSymbolTable().addprefix("sys", Constants.ARGS + "system#");
        getSymbolTable().addprefix("web", Constants.ARGS + "web#");
        
        bindEagerProcedure("is-instance?", IsInstanceFunction.class);
        bindLazyProcedure(Constants.LAMBDA, LambdaFunction.class);
        bindLazyProcedure(Constants.LAMBDAQ, LambdaqFunction.class);
        bindLazyProcedure(Constants.LAMBDAM, LambdamFunction.class);
        bindLazyProcedure(Constants.TEMPLATE, BackquoteFunction.class);
        bindEagerProcedure("car", CarFunction.class);
        bindEagerProcedure("cdr", CdrFunction.class);
        bindEagerProcedure("rplaca", ReplaceCarFunction.class);
        bindEagerProcedure("rplacd", ReplaceCdrFunction.class);
        bindEagerProcedure("cons", ConsFunction.class);
        bindLazyProcedure("quote", QuoteFunction.class);
        bindEagerProcedure("set", SetFunction.class);
        bindEagerProcedure("defvar", DefineFunction.class);
        bindLazyProcedure("def", DefFunction.class);
        bindLazyProcedure("defmacro", DefMacroFunction.class);
        bindLazyProcedure("class", DefineClassFunction.class);
        bindLazyProcedure("cond", ConditionalFunction.class);
        bindLazyProcedure("while", WhileFunction.class);
        bindEagerProcedure("equal?", EqualsFunction.class);
        bindEagerProcedure("eq?", EqFunction.class);
        bindLazyProcedure("dict", ObjectFunction.class);
        bindEagerProcedure("eval", EvalFunction.class);
        bindEagerProcedure("apply", ApplyFunction.class);
        bindEagerProcedure("symbol-value", SymbolValueFunction.class);
        bindLazyProcedure("dynamic-symbol-value", DynamicSymbolValueFunction.class);
        bindEagerProcedure("the", IdentityFunction.class);
        bindEagerProcedure("list", ListFunction.class);
        bindEagerProcedure("reverse", ReverseFunction.class);
        bindEagerProcedure("length", LengthFunction.class);
        bindEagerProcedure("load", LoadFunction.class);
        bindEagerProcedure("include", IncludeFunction.class);
        bindEagerProcedure("print", PrintFunction.class);
        bindEagerProcedure("display", DisplayFunction.class);
        bindEagerProcedure("write", WriteFunction.class);
        bindEagerProcedure("read", ReadFunction.class);
        bindEagerProcedure("tag", TagFunction.class);
        bindEagerProcedure("remove-tag", RemoveTagFunction.class);
        bindEagerProcedure("+", PlusFunction.class);
        bindEagerProcedure("-", MinusFunction.class);
        bindEagerProcedure("*", MultiplyFunction.class);
        bindEagerProcedure("/", DivideFunction.class);
        bindEagerProcedure("%", RemainderFunction.class);
        bindEagerProcedure(">", GreaterThanFunction.class);
        bindEagerProcedure("<", LessThanFunction.class);
        bindEagerProcedure("power", PowerFunction.class);
        bindLazyProcedure("or", OrFunction.class);
        bindLazyProcedure("and", AndFunction.class);
        bindEagerProcedure("not", NotFunction.class);
        bindLazyProcedure("bound?", BoundFunction.class);
        bindEagerProcedure("raise", RaiseFunction.class);
        bindEagerProcedure("self-test-runner", JunitRunnerFunction.class);
        bindEagerProcedure("symlist", SymListFunction.class);

        bindEagerProcedure("web.serve", SpawnHTTPDFunction.class);
        bindEagerProcedure("web.kill", KillHTTPDFunction.class);
        bindEagerProcedure("web.get", HTTPgetFunction.class);
              
        bindMethod("String", Constants.SPLIT, SplitMethod.class);
        bindMethod("String", Constants.CONCAT, ConcatMethod.class);
        bindMethod("String", Constants.MATCH, MatchMethod.class);
        bindMethod("String", Constants.LENGTH, LengthMethod.class);
        bindMethod("File", "static-open", Gfile.FileOpenMethod.class);
        bindMethod(Constants.WRITER, "format", FormatMethod.class);
        bindMethod(Constants.WRITER, "close", CloseMethod.class);
        bindMethod(Constants.WRITER, "flush", FlushMethod.class);
        bindMethod(Constants.READER, "hasData", ReaderStream.HasDataMethod.class);
        bindMethod(Constants.READER, "read", ReaderStream.ReadMethod.class);
        bindMethod(Constants.READER, "close", ReaderStream.CloseMethod.class);
        bindMethod(Constants.PARENPARSER, "new", StreamParser.NewMethod.class);
        bindMethod(Constants.PARENPARSER, "read", StreamParser.ReadMethod.class);
        bindMethod(Constants.PARENPARSER, "close", StreamParser.CloseMethod.class);
        bindMethod("StringFormatStream", "new", StringFormatStream.NewMethod.class);
        bindMethod("System", "exec", ExecMethod.class);
        bindMethod("Sound", "play", PlayMethod.class);


    }

    private void bindEagerProcedure(String name, Class class1) throws GenyrisException {
        bindProcedure(_globalEnvironment, true, name, class1);
    }

    private void bindLazyProcedure(String name, Class class1) throws GenyrisException {
        bindProcedure(_globalEnvironment, false, name, class1);
    }

    private void bindProcedure(Environment env, boolean type, String name, Class class1)
            throws GenyrisException {
        //
        // Method uses reflection to locate and call the constructor.
        //
        Lsymbol nameSymbol = _table.internString(name);
        Class[] paramTypes = new Class[]{Interpreter.class, Lsymbol.class};
        try {
            Constructor ctor = class1.getConstructor(paramTypes);
            Object[] args = new Object[]{this, nameSymbol};
            Object proc = ctor.newInstance(args);
            if (type) {
                env.defineVariable(nameSymbol, new EagerProcedure(env,
                        null,
                        (ApplicableFunction)proc));
            } else {
                env.defineVariable(nameSymbol, new LazyProcedure(env,
                        null,
                        (ApplicableFunction)proc));
            }
        }
        catch (InvocationTargetException e) {
            throw new RuntimeException(e.getMessage());
        }
        catch (SecurityException e) {
            throw new RuntimeException(e.getMessage());
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage());
        }
        catch (InstantiationException e) {
            throw new RuntimeException(e.getMessage());
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void bindMethod(String className, String methodName, Class class1)
            throws UnboundException, GenyrisException {
        Lobject stringClass = (Lobject)_globalEnvironment.lookupVariableValue(_table.internString(className));
        //
        // Method uses reflection to locate and call the constructor.
        //
        Lsymbol nameSymbol = _table.internString(methodName);
        Class[] paramTypes = new Class[]{Interpreter.class, Lsymbol.class};
        try {
            Constructor ctor = class1.getConstructor(paramTypes);
            Object[] args = new Object[]{this, nameSymbol};
            Object proc = ctor.newInstance(args);
            stringClass.defineVariableRaw(nameSymbol, new EagerProcedure(stringClass,
                        null,
                        (ApplicableFunction)proc));

        } // TODO DRY
        catch (InvocationTargetException e) {
            throw new RuntimeException(e.getMessage());
        }
        catch (SecurityException e) {
            throw new RuntimeException(e.getMessage());
        }
        catch (NoSuchMethodException e) {
            throw new RuntimeException(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage());
        }
        catch (InstantiationException e) {
            throw new RuntimeException(e.getMessage());
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Exp init(boolean verbose) throws GenyrisException {
        return SourceLoader.loadScriptFromClasspath(this, "org/genyris/load/boot/init.lin", verbose
                ? _defaultOutput
                : (Writer)new NullWriter());
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

    public Lsymbol getNil() {
        return NIL;
    }

    public Lsymbol getTrue() {
        return TRUE;
    }

    public Environment getGlobalEnv() {
        return _globalEnvironment;
    }

    public SymbolTable getSymbolTable() {
        return _table;
    }

    public Exp lookupGlobalFromString(String var) throws GenyrisException {
        return _globalEnvironment.lookupVariableValue(_table.internString(var));
    }
}
