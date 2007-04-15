package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.InStream;
import org.lispin.jlispin.core.Lsymbol;
import org.lispin.jlispin.core.Parser;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.interp.builtin.ConditionalFunction;
import org.lispin.jlispin.interp.builtin.ConsFunction;
import org.lispin.jlispin.interp.builtin.DefineFunction;
import org.lispin.jlispin.interp.builtin.QuoteFunction;
import org.lispin.jlispin.interp.builtin.SetFunction;

public class Interpreter {
	
	Environment _globalEnvironment;
	SymbolTable _table;
	
	public Interpreter() {
		_globalEnvironment = new Environment(null);
		_table = new SymbolTable();		
		_globalEnvironment.defineVariable(new Lsymbol("cons"), new EagerProcedure(_globalEnvironment, null, new ConsFunction()));
		_globalEnvironment.defineVariable(new Lsymbol("quote"), new LazyProcedure(_globalEnvironment, null, new QuoteFunction()));
		_globalEnvironment.defineVariable(new Lsymbol("set"), new EagerProcedure(_globalEnvironment, null, new SetFunction()));
		_globalEnvironment.defineVariable(new Lsymbol("define"), new EagerProcedure(_globalEnvironment, null, new DefineFunction()));
		_globalEnvironment.defineVariable(new Lsymbol("cond"), new LazyProcedure(_globalEnvironment, null, new ConditionalFunction()));
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
