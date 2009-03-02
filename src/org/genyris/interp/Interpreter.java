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
import org.genyris.interp.builtin.InternFunction;
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

        bindGlobalProcedure(IsInstanceFunction.class);
        bindGlobalProcedure(LambdaFunction.class);
        bindGlobalProcedure(LambdaqFunction.class);
        bindGlobalProcedure(LambdamFunction.class);
        bindGlobalProcedure(BackquoteFunction.class);

        bindGlobalProcedure(CarFunction.class);
        bindGlobalProcedure(CdrFunction.class);
        bindGlobalProcedure(ReplaceCarFunction.class);
        bindGlobalProcedure(ReplaceCdrFunction.class);
        bindGlobalProcedure(ConsFunction.class);
        bindGlobalProcedure(QuoteFunction.class);
        bindGlobalProcedure(SetFunction.class);
        bindGlobalProcedure(DefineFunction.class);
        bindGlobalProcedure(DefFunction.class);
        bindGlobalProcedure(DefMacroFunction.class);
        bindGlobalProcedure(DefineClassFunction.class);
        bindGlobalProcedure(ConditionalFunction.class);
        bindGlobalProcedure(WhileFunction.class);
        bindGlobalProcedure(EqualsFunction.class);
        bindGlobalProcedure(EqFunction.class);
        bindGlobalProcedure(ObjectFunction.class);
        bindGlobalProcedure(EvalFunction.class);
        bindGlobalProcedure(ApplyFunction.class);
        bindGlobalProcedure(SymbolValueFunction.class);
        bindGlobalProcedure(DynamicSymbolValueFunction.class);
        bindGlobalProcedure(IdentityFunction.class);
        bindGlobalProcedure(ListFunction.class);
        bindGlobalProcedure(ReverseFunction.class);
        bindGlobalProcedure(LengthFunction.class);
        bindGlobalProcedure(LoadFunction.class);
        bindGlobalProcedure(IncludeFunction.class);
        bindGlobalProcedure(PrintFunction.class);
        bindGlobalProcedure(DisplayFunction.class);
        bindGlobalProcedure(WriteFunction.class);
        bindGlobalProcedure(ReadFunction.class);
        bindGlobalProcedure(TagFunction.class);
        bindGlobalProcedure(RemoveTagFunction.class);
        bindGlobalProcedure(PlusFunction.class);
        bindGlobalProcedure(MinusFunction.class);
        bindGlobalProcedure(MultiplyFunction.class);
        bindGlobalProcedure(DivideFunction.class);
        bindGlobalProcedure(RemainderFunction.class);
        bindGlobalProcedure(GreaterThanFunction.class);
        bindGlobalProcedure(LessThanFunction.class);
        bindGlobalProcedure(PowerFunction.class);
        bindGlobalProcedure(OrFunction.class);
        bindGlobalProcedure(AndFunction.class);
        bindGlobalProcedure(NotFunction.class);
        bindGlobalProcedure(BoundFunction.class);
        bindGlobalProcedure(RaiseFunction.class);
        bindGlobalProcedure(JunitRunnerFunction.class);
        bindGlobalProcedure(SymListFunction.class);
        bindGlobalProcedure(InternFunction.class);

        bindGlobalProcedure(SpawnHTTPDFunction.class);
        bindGlobalProcedure(KillHTTPDFunction.class);
        bindGlobalProcedure(HTTPgetFunction.class);

        bindMethod("String", SplitMethod.class);
        bindMethod("String", ConcatMethod.class);
        bindMethod("String", MatchMethod.class);
        bindMethod("String", LengthMethod.class);
        bindMethod("File", Gfile.FileOpenMethod.class);
        bindMethod(Constants.WRITER, FormatMethod.class);
        bindMethod(Constants.WRITER, CloseMethod.class);
        bindMethod(Constants.WRITER, FlushMethod.class);
        bindMethod(Constants.READER, ReaderStream.HasDataMethod.class);
        bindMethod(Constants.READER, ReaderStream.ReadMethod.class);
        bindMethod(Constants.READER, ReaderStream.CloseMethod.class);
        bindMethod(Constants.PARENPARSER, StreamParser.NewMethod.class);
        bindMethod(Constants.PARENPARSER, StreamParser.ReadMethod.class);
        bindMethod(Constants.PARENPARSER, StreamParser.CloseMethod.class);
        bindMethod("StringFormatStream", StringFormatStream.NewMethod.class);
        bindMethod("System", ExecMethod.class);
        bindMethod("Sound", PlayMethod.class);


    }

    private void bindGlobalProcedure(Class class1) throws GenyrisException {
        bindProcedure(_globalEnvironment, class1);
    }

    private void bindProcedure(Environment env, Class class1)
            throws GenyrisException {
        //
        // Method uses reflection to locate and call the constructor.
        //
        Class[] paramTypes = new Class[]{Interpreter.class};
        try {
            Constructor ctor = class1.getConstructor(paramTypes);
            Method getNameMethod = class1.getMethod("getStaticName", (Class[])null);
            String staticName = (String)getNameMethod.invoke(null,(Object[])null);
            Method isEagerMethod = class1.getMethod("isEager", (Class[])null);
            boolean isEager = ((Boolean)isEagerMethod.invoke(null,(Object[])null)).booleanValue();
            Lsymbol  nameSymbol = _table.internString(staticName);
            Object[] args = new Object[]{this};
            Object proc = ctor.newInstance(args);
            if (isEager) {
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

    private void bindMethod(String className, Class class1)
            throws UnboundException, GenyrisException {
        Lobject stringClass = (Lobject)_globalEnvironment.lookupVariableValue(_table.internString(className));
        //
        // Method uses reflection to locate and call the constructor.
        //
        Class[] paramTypes = new Class[]{Interpreter.class};
        try {
            Method getNameMethod = class1.getMethod("getStaticName", (Class[])null);
            String staticName = (String)getNameMethod.invoke(null,(Object[])null);
            Lsymbol nameSymbol = _table.internString(staticName);
            Constructor ctor = class1.getConstructor(paramTypes);
            Object[] args = new Object[]{this};
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
