package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.interp.builtin.CarFunction;
import org.lispin.jlispin.interp.builtin.CdrFunction;
import org.lispin.jlispin.interp.builtin.ConditionalFunction;
import org.lispin.jlispin.interp.builtin.ConsFunction;
import org.lispin.jlispin.interp.builtin.DefineFunction;
import org.lispin.jlispin.interp.builtin.EqFunction;
import org.lispin.jlispin.interp.builtin.EqualsFunction;
import org.lispin.jlispin.interp.builtin.DictFunction;
import org.lispin.jlispin.interp.builtin.QuoteFunction;
import org.lispin.jlispin.interp.builtin.ReplaceCarFunction;
import org.lispin.jlispin.interp.builtin.ReplaceCdrFunction;
import org.lispin.jlispin.interp.builtin.SetFunction;
import org.lispin.jlispin.io.InStream;
import org.lispin.jlispin.io.Parser;

public class Interpreter {
	
	Environment _globalEnvironment;
	SymbolTable _table;
	
	public Interpreter() throws LispinException {
		_globalEnvironment = new StandardEnvironment(null);
		_table = new SymbolTable();		
		_globalEnvironment.defineVariable(_table.internString("closure"), new EagerProcedure(_globalEnvironment, null, new ClosureFunction()));
		_globalEnvironment.defineVariable(_table.internString("car"), new EagerProcedure(_globalEnvironment, null, new CarFunction()));
		_globalEnvironment.defineVariable(_table.internString("cdr"), new EagerProcedure(_globalEnvironment, null, new CdrFunction()));
		_globalEnvironment.defineVariable(_table.internString("rplaca"), new EagerProcedure(_globalEnvironment, null, new ReplaceCarFunction()));
		_globalEnvironment.defineVariable(_table.internString("rplacd"), new EagerProcedure(_globalEnvironment, null, new ReplaceCdrFunction()));
		_globalEnvironment.defineVariable(_table.internString("cons"), new EagerProcedure(_globalEnvironment, null, new ConsFunction()));
		_globalEnvironment.defineVariable(_table.internString("quote"), new LazyProcedure(_globalEnvironment, null, new QuoteFunction()));
		_globalEnvironment.defineVariable(_table.internString("set"), new EagerProcedure(_globalEnvironment, null, new SetFunction()));
		_globalEnvironment.defineVariable(_table.internString("defvar"), new EagerProcedure(_globalEnvironment, null, new DefineFunction()));
		_globalEnvironment.defineVariable(_table.internString("cond"), new LazyProcedure(_globalEnvironment, null, new ConditionalFunction()));
		_globalEnvironment.defineVariable(_table.internString("equal"), new EagerProcedure(_globalEnvironment, null, new EqualsFunction()));
		_globalEnvironment.defineVariable(_table.internString("eq"), new EagerProcedure(_globalEnvironment, null, new EqFunction()));
		_globalEnvironment.defineVariable(_table.internString("dict"), new LazyProcedure(_globalEnvironment, null, new DictFunction()));
		_globalEnvironment.defineVariable(SymbolTable.NIL, SymbolTable.NIL);
		_globalEnvironment.defineVariable(SymbolTable.T, SymbolTable.T);
		_globalEnvironment.defineVariable(SymbolTable.EOF, SymbolTable.EOF);

	}

	public Parser newParser(InStream input) {
		return new Parser(_table, input);
	}

	public Exp eval(Exp expression) throws LispinException {
		return _globalEnvironment.eval(expression);
	}
	
	
}
