// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import java.io.OutputStreamWriter;
import java.io.Writer;

import org.genyris.classes.BuiltinClasses;
import org.genyris.core.AccessException;
import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Lobject;
import org.genyris.core.Lsymbol;
import org.genyris.core.NilSymbol;
import org.genyris.core.SymbolTable;
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
import org.genyris.interp.builtin.ListFunction;
import org.genyris.interp.builtin.LoadFunction;
import org.genyris.interp.builtin.ObjectFunction;
import org.genyris.interp.builtin.QuoteFunction;
import org.genyris.interp.builtin.RemoveTagFunction;
import org.genyris.interp.builtin.ReplaceCarFunction;
import org.genyris.interp.builtin.ReplaceCdrFunction;
import org.genyris.interp.builtin.SetFunction;
import org.genyris.interp.builtin.TagFunction;
import org.genyris.io.InStream;
import org.genyris.io.NullWriter;
import org.genyris.io.Parser;
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
        BuiltinClasses.SYMBOL = new Lobject(_globalEnvironment);
        NIL.addClass(BuiltinClasses.SYMBOL);
        _defaultOutput = new OutputStreamWriter(System.out);
        // Begin Circular references between symbols and classnames require manual bootstrap here:

        _table.init(NIL);
        BuiltinClasses.SYMBOL.defineVariable(_table.internString(Constants.CLASSNAME), _table.internString(Constants.SYMBOL));
        // End manual bootstrap

        _globalEnvironment.defineVariable(NIL, NIL);

        TRUE = _table.internString("true");
        _globalEnvironment.defineVariable(TRUE, TRUE);
        _globalEnvironment.defineVariable(_table.internString(Constants.EOF), _table.internString(Constants.EOF));
        // TODO all these constructors need to be replaced with a factory and singletons:
        _globalEnvironment.defineVariable(_table.internString(Constants.LAMBDA), new LazyProcedure(_globalEnvironment, null, new LambdaFunction(this)));
        _globalEnvironment.defineVariable(_table.internString(Constants.LAMBDAQ), new LazyProcedure(_globalEnvironment, null, new LambdaqFunction(this)));
        _globalEnvironment.defineVariable(_table.internString(Constants.LAMBDAM), new LazyProcedure(_globalEnvironment, null, new LambdamFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("backquote"), new LazyProcedure(_globalEnvironment, null, new BackquoteFunction(this)));
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
        _globalEnvironment.defineVariable(_table.internString("equal"), new EagerProcedure(_globalEnvironment, null, new EqualsFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("eq"), new EagerProcedure(_globalEnvironment, null, new EqFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("dict"), new LazyProcedure(_globalEnvironment, null, new ObjectFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("eval"), new LazyProcedure(_globalEnvironment, null, new EvalFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("the"), new EagerProcedure(_globalEnvironment, null, new IdentityFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("list"), new EagerProcedure(_globalEnvironment, null, new ListFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("load"), new EagerProcedure(_globalEnvironment, null, new LoadFunction(this)));
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


        BuiltinClasses.init(_globalEnvironment);

        _globalEnvironment.defineVariable(_table.internString("StandardClass"), BuiltinClasses.STANDARDCLASS);
        _globalEnvironment.defineVariable(_table.internString("Thing"), BuiltinClasses.THING);
        _globalEnvironment.defineVariable(_table.internString("Object"), BuiltinClasses.OBJECT);
        _globalEnvironment.defineVariable(_table.internString("Pair"), BuiltinClasses.PAIR);
        _globalEnvironment.defineVariable(_table.internString("Bignum"), BuiltinClasses.BIGNUM);
        _globalEnvironment.defineVariable(_table.internString("String"), BuiltinClasses.STRING);
        _globalEnvironment.defineVariable(_table.internString("Symbol"), BuiltinClasses.SYMBOL);

    }

    public void init(boolean verbose)  throws GenyrisException {
        SourceLoader.loadScriptFromClasspath(this, "boot/init.lin", verbose? _defaultOutput: (Writer)new NullWriter());
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

    public Exp getTrue() {
        return TRUE;
    }

    public Environment getGlobalEnv() {
        return _globalEnvironment;
    }

    public SymbolTable getSymbolTable() {
        return _table;
    }
}
