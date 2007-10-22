package org.lispin.jlispin.interp;

import java.io.OutputStreamWriter;
import java.io.Writer;

import org.lispin.jlispin.classes.BuiltinClasses;
import org.lispin.jlispin.core.AccessException;
import org.lispin.jlispin.core.Constants;
import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lobject;
import org.lispin.jlispin.core.Lsymbol;
import org.lispin.jlispin.core.NilSymbol;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.interp.builtin.BackquoteFunction;
import org.lispin.jlispin.interp.builtin.CarFunction;
import org.lispin.jlispin.interp.builtin.CdrFunction;
import org.lispin.jlispin.interp.builtin.ConditionalFunction;
import org.lispin.jlispin.interp.builtin.ConsFunction;
import org.lispin.jlispin.interp.builtin.DefFunction;
import org.lispin.jlispin.interp.builtin.DefMacroFunction;
import org.lispin.jlispin.interp.builtin.DefineClassFunction;
import org.lispin.jlispin.interp.builtin.DefineFunction;
import org.lispin.jlispin.interp.builtin.ObjectFunction;
import org.lispin.jlispin.interp.builtin.EqFunction;
import org.lispin.jlispin.interp.builtin.EqualsFunction;
import org.lispin.jlispin.interp.builtin.EvalFunction;
import org.lispin.jlispin.interp.builtin.IdentityFunction;
import org.lispin.jlispin.interp.builtin.LambdaFunction;
import org.lispin.jlispin.interp.builtin.LambdamFunction;
import org.lispin.jlispin.interp.builtin.LambdaqFunction;
import org.lispin.jlispin.interp.builtin.ListFunction;
import org.lispin.jlispin.interp.builtin.LoadFunction;
import org.lispin.jlispin.interp.builtin.QuoteFunction;
import org.lispin.jlispin.interp.builtin.RemoveTagFunction;
import org.lispin.jlispin.interp.builtin.ReplaceCarFunction;
import org.lispin.jlispin.interp.builtin.ReplaceCdrFunction;
import org.lispin.jlispin.interp.builtin.SetFunction;
import org.lispin.jlispin.interp.builtin.TagFunction;
import org.lispin.jlispin.io.InStream;
import org.lispin.jlispin.io.NullWriter;
import org.lispin.jlispin.io.Parser;
import org.lispin.jlispin.load.SourceLoader;
import org.lispin.jlispin.logic.AndFunction;
import org.lispin.jlispin.logic.OrFunction;
import org.lispin.jlispin.math.DivideFunction;
import org.lispin.jlispin.math.GreaterThanFunction;
import org.lispin.jlispin.math.LessThanFunction;
import org.lispin.jlispin.math.MinusFunction;
import org.lispin.jlispin.math.MultiplyFunction;
import org.lispin.jlispin.math.PlusFunction;
import org.lispin.jlispin.math.RemainderFunction;

public class Interpreter {

	Environment _globalEnvironment;
	SymbolTable _table;
	Writer _defaultOutput;
	public NilSymbol NIL;
	private Lsymbol TRUE;

	public Interpreter() throws LispinException {
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

		_globalEnvironment.defineVariable(_table.internString("or"), new EagerProcedure(_globalEnvironment, null, new OrFunction(this)));
        _globalEnvironment.defineVariable(_table.internString("and"), new EagerProcedure(_globalEnvironment, null, new AndFunction(this)));

		BuiltinClasses.init(_globalEnvironment);

        _globalEnvironment.defineVariable(_table.internString("StandardClass"), BuiltinClasses.STANDARDCLASS);
        _globalEnvironment.defineVariable(_table.internString("Thing"), BuiltinClasses.THING);
		_globalEnvironment.defineVariable(_table.internString("Object"), BuiltinClasses.OBJECT);
		_globalEnvironment.defineVariable(_table.internString("Pair"), BuiltinClasses.PAIR);
		_globalEnvironment.defineVariable(_table.internString("Bignum"), BuiltinClasses.BIGNUM);
		_globalEnvironment.defineVariable(_table.internString("String"), BuiltinClasses.STRING);
		_globalEnvironment.defineVariable(_table.internString("Symbol"), BuiltinClasses.SYMBOL);

	}

	public void init(boolean verbose)  throws LispinException {
		SourceLoader.loadScriptFromClasspath(this, "boot/init.lin", verbose? _defaultOutput: (Writer)new NullWriter());
	}

	public Parser newParser(InStream input) {
		return new Parser(_table, input);
	}

	public Exp evalInGlobalEnvironment(Exp expression) throws UnboundException, AccessException, LispinException {
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
