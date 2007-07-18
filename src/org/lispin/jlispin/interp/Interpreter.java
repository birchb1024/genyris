package org.lispin.jlispin.interp;

import java.io.OutputStreamWriter;
import java.io.Writer;

import org.lispin.jlispin.classes.BuiltinClasses;
import org.lispin.jlispin.core.AccessException;
import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.interp.builtin.CarFunction;
import org.lispin.jlispin.interp.builtin.CdrFunction;
import org.lispin.jlispin.interp.builtin.ConditionalFunction;
import org.lispin.jlispin.interp.builtin.ConsFunction;
import org.lispin.jlispin.interp.builtin.DefFunction;
import org.lispin.jlispin.interp.builtin.DefMacroFunction;
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

public class Interpreter {
	
	Environment _globalEnvironment;
	SymbolTable _table;
	Writer _defaultOutput;
	
	public Interpreter() throws LispinException {
		_globalEnvironment = new StandardEnvironment(null);
		_defaultOutput = new OutputStreamWriter(System.out);
		_table = new SymbolTable();		
		_globalEnvironment.defineVariable(SymbolTable.NIL, SymbolTable.NIL);
		_globalEnvironment.defineVariable(SymbolTable.T, SymbolTable.T);
		_globalEnvironment.defineVariable(SymbolTable.EOF, SymbolTable.EOF);
		_globalEnvironment.defineVariable(_table.internString("lambda"), new LazyProcedure(_globalEnvironment, null, new LambdaFunction()));
		_globalEnvironment.defineVariable(_table.internString("lambdaq"), new LazyProcedure(_globalEnvironment, null, new LambdaqFunction()));
		_globalEnvironment.defineVariable(_table.internString("lambdam"), new LazyProcedure(_globalEnvironment, null, new LambdamFunction()));
		_globalEnvironment.defineVariable(_table.internString("car"), new EagerProcedure(_globalEnvironment, null, new CarFunction()));
		_globalEnvironment.defineVariable(_table.internString("cdr"), new EagerProcedure(_globalEnvironment, null, new CdrFunction()));
		_globalEnvironment.defineVariable(_table.internString("rplaca"), new EagerProcedure(_globalEnvironment, null, new ReplaceCarFunction()));
		_globalEnvironment.defineVariable(_table.internString("rplacd"), new EagerProcedure(_globalEnvironment, null, new ReplaceCdrFunction()));
		_globalEnvironment.defineVariable(_table.internString("cons"), new EagerProcedure(_globalEnvironment, null, new ConsFunction()));
		_globalEnvironment.defineVariable(_table.internString("quote"), new LazyProcedure(_globalEnvironment, null, new QuoteFunction()));
		_globalEnvironment.defineVariable(_table.internString("set"), new EagerProcedure(_globalEnvironment, null, new SetFunction()));
		_globalEnvironment.defineVariable(_table.internString("defvar"), new EagerProcedure(_globalEnvironment, null, new DefineFunction()));
		_globalEnvironment.defineVariable(_table.internString("def"), new LazyProcedure(_globalEnvironment, null, new DefFunction()));
		_globalEnvironment.defineVariable(_table.internString("defmacro"), new LazyProcedure(_globalEnvironment, null, new DefMacroFunction()));
		_globalEnvironment.defineVariable(_table.internString("cond"), new LazyProcedure(_globalEnvironment, null, new ConditionalFunction()));
		_globalEnvironment.defineVariable(_table.internString("equal"), new EagerProcedure(_globalEnvironment, null, new EqualsFunction()));
		_globalEnvironment.defineVariable(_table.internString("eq"), new EagerProcedure(_globalEnvironment, null, new EqFunction()));
		_globalEnvironment.defineVariable(_table.internString("dict"), new LazyProcedure(_globalEnvironment, null, new ObjectFunction()));
		_globalEnvironment.defineVariable(_table.internString("eval"), new LazyProcedure(_globalEnvironment, null, new EvalFunction()));
		_globalEnvironment.defineVariable(_table.internString("the"), new EagerProcedure(_globalEnvironment, null, new IdentityFunction()));
		_globalEnvironment.defineVariable(_table.internString("list"), new EagerProcedure(_globalEnvironment, null, new ListFunction()));
		_globalEnvironment.defineVariable(_table.internString("load"), new EagerProcedure(_globalEnvironment, null, new LoadFunction(this)));
		_globalEnvironment.defineVariable(_table.internString("tag"), new EagerProcedure(_globalEnvironment, null, new TagFunction()));
		_globalEnvironment.defineVariable(_table.internString("remove-tag"), new EagerProcedure(_globalEnvironment, null, new RemoveTagFunction()));
		
		BuiltinClasses.init();

		_globalEnvironment.defineVariable(_table.internString("Pair"), BuiltinClasses.PAIR);
		_globalEnvironment.defineVariable(_table.internString("Integer"), BuiltinClasses.INTEGER);
		_globalEnvironment.defineVariable(_table.internString("Double"), BuiltinClasses.DOUBLE);
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
	
}
