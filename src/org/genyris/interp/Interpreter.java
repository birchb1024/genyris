// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import java.io.OutputStreamWriter;
import java.io.Writer;

import org.genyris.classes.BuiltinClasses;
import org.genyris.classification.IsInstanceFunction;
import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Lobject;
import org.genyris.core.Lsymbol;
import org.genyris.core.NilSymbol;
import org.genyris.core.SymbolTable;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
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
import org.genyris.interp.builtin.SymbolValueFunction;
import org.genyris.interp.builtin.TagFunction;
import org.genyris.io.InStream;
import org.genyris.io.NullWriter;
import org.genyris.io.Parser;
import org.genyris.io.ReadFunction;
import org.genyris.java.JavaClassForName;
import org.genyris.load.IncludeFunction;
import org.genyris.load.SourceLoader;
import org.genyris.logic.AndFunction;
import org.genyris.logic.OrFunction;
import org.genyris.math.DivideFunction;
import org.genyris.math.GreaterThanFunction;
import org.genyris.math.LessThanFunction;
import org.genyris.math.MinusFunction;
import org.genyris.math.MultiplyFunction;
import org.genyris.math.PlusFunction;
import org.genyris.math.RemainderFunction;
import org.genyris.test.JunitRunnerFunction;
import org.genyris.string.AbstractMethod;
import org.genyris.string.ConcatMethod;
import org.genyris.string.MatchMethod;
import org.genyris.string.SplitMethod;

public class Interpreter {

    Environment _globalEnvironment;
    SymbolTable _table;
    Writer _defaultOutput;
    public NilSymbol NIL;
    private Lsymbol TRUE;

    public Interpreter() throws GenyrisException {
        NIL = new NilSymbol();
        _table = new SymbolTable();
        _globalEnvironment = new StandardEnvironment(this, NIL);
        Lobject SYMBOL = new Lobject(_globalEnvironment);
        _defaultOutput = new OutputStreamWriter(System.out);
        {
            // Circular references between symbols and classnames require manual bootstrap here:
            _table.init(NIL);
            SYMBOL.defineVariable(_table.internString(Constants.CLASSNAME), _table.internString(Constants.SYMBOL));
        }

        _globalEnvironment.defineVariable(NIL, NIL);

        TRUE = _table.internString("true");
        _globalEnvironment.defineVariable(TRUE, TRUE);
        _globalEnvironment.defineVariable(_table.internString(Constants.EOF), _table.internString(Constants.EOF));

        BuiltinClasses.init(_globalEnvironment);

        // TODO all these constructors need to be replaced with a factory and singletons:
        _globalEnvironment.defineVariable(_table.internString(Constants.LAMBDA), new LazyProcedure(_globalEnvironment, null, new LambdaFunction(this)));
        _globalEnvironment.defineVariable(_table.internString(Constants.LAMBDAQ), new LazyProcedure(_globalEnvironment, null, new LambdaqFunction(this)));
        _globalEnvironment.defineVariable(_table.internString(Constants.LAMBDAM), new LazyProcedure(_globalEnvironment, null, new LambdamFunction(this)));
        _globalEnvironment.defineVariable(_table.internString(Constants.TEMPLATE), new LazyProcedure(_globalEnvironment, null, new BackquoteFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("car"), new EagerProcedure(_globalEnvironment, null, new CarFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("cdr"), new EagerProcedure(_globalEnvironment, null, new CdrFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("rplaca"), new EagerProcedure(_globalEnvironment, null, new ReplaceCarFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("rplacd"), new EagerProcedure(_globalEnvironment, null, new ReplaceCdrFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("cons"), new EagerProcedure(_globalEnvironment, null, new ConsFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("quote"), new LazyProcedure(_globalEnvironment, null, new QuoteFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("set"), new EagerProcedure(_globalEnvironment, null, new SetFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("defvar"), new EagerProcedure(_globalEnvironment, null, new DefineFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("def"), new LazyProcedure(_globalEnvironment, null, new DefFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("defmacro"), new LazyProcedure(_globalEnvironment, null, new DefMacroFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("class"), new LazyProcedure(_globalEnvironment, null, new DefineClassFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("cond"), new LazyProcedure(_globalEnvironment, null, new ConditionalFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("equal?"), new EagerProcedure(_globalEnvironment, null, new EqualsFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("eq?"), new EagerProcedure(_globalEnvironment, null, new EqFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("dict"), new LazyProcedure(_globalEnvironment, null, new ObjectFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("eval"), new EagerProcedure(_globalEnvironment, null, new EvalFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("apply"), new EagerProcedure(_globalEnvironment, null, new ApplyFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("symbol-value"), new EagerProcedure(_globalEnvironment, null, new SymbolValueFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("the"), new EagerProcedure(_globalEnvironment, null, new IdentityFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("list"), new EagerProcedure(_globalEnvironment, null, new ListFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("reverse"), new EagerProcedure(_globalEnvironment, null, new ReverseFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("length"), new EagerProcedure(_globalEnvironment, null, new LengthFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("load"), new EagerProcedure(_globalEnvironment, null, new LoadFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("include"), new EagerProcedure(_globalEnvironment, null, new IncludeFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("print"), new EagerProcedure(_globalEnvironment, null, new PrintFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("read"), new EagerProcedure(_globalEnvironment, null, new ReadFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("tag"), new EagerProcedure(_globalEnvironment, null, new TagFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("remove-tag"), new EagerProcedure(_globalEnvironment, null, new RemoveTagFunction(this)));

        _globalEnvironment.defineVariable(_table.internString("+"), new EagerProcedure(_globalEnvironment, null, new PlusFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("-"), new EagerProcedure(_globalEnvironment, null, new MinusFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("*"), new EagerProcedure(_globalEnvironment, null, new MultiplyFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("/"), new EagerProcedure(_globalEnvironment, null, new DivideFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("%"), new EagerProcedure(_globalEnvironment, null, new RemainderFunction(this)));
        _globalEnvironment.defineVariable(_table.internString(">"), new EagerProcedure(_globalEnvironment, null, new GreaterThanFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("<"), new EagerProcedure(_globalEnvironment, null, new LessThanFunction(this)));

        _globalEnvironment.defineVariable(_table.internString("or"), new LazyProcedure(_globalEnvironment, null, new OrFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("and"), new LazyProcedure(_globalEnvironment, null, new AndFunction(this)));

        _globalEnvironment.defineVariable(_table.internString("bound?"), new LazyProcedure(_globalEnvironment, null, new BoundFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("is-instance?"), new EagerProcedure(_globalEnvironment, null, new IsInstanceFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("raise"), new EagerProcedure(_globalEnvironment, null, new RaiseFunction(this)));

        _globalEnvironment.defineVariable(_table.internString("java-class"), new EagerProcedure(_globalEnvironment, null, new JavaClassForName(this)));
        _globalEnvironment.defineVariable(_table.internString("self-test-runner"), new EagerProcedure(_globalEnvironment, null, new JunitRunnerFunction(this)));
        
        bindMethod("String", Constants.SPLIT, new SplitMethod(this));
        bindMethod("String", Constants.CONCAT, new ConcatMethod(this));
        bindMethod("String", Constants.MATCH, new MatchMethod(this));

    }

	private void bindMethod(String className, String methodName, AbstractMethod method) throws UnboundException, GenyrisException {
		Lobject stringClass = (Lobject)_globalEnvironment.lookupVariableValue( _table.internString(className));
        stringClass.defineVariable(_table.internString(methodName), new EagerProcedure(stringClass, null, method));
	}

    public Exp init(boolean verbose)  throws GenyrisException {
        return SourceLoader.loadScriptFromClasspath(this, "org/genyris/load/boot/init.lin", verbose? _defaultOutput: (Writer)new NullWriter());
    }

    public Parser newParser(InStream input) {
        return new Parser(_table, input);
    }

    public Exp evalInGlobalEnvironment(Exp expression) throws UnboundException, AccessException, GenyrisException {
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
}
