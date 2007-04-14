package org.lispin.jlispin.interp;

import java.util.HashMap;
import java.util.Map;

import org.lispin.jlispin.core.AccessException;
import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lsymbol;
import org.lispin.jlispin.core.SymbolTable;

public class Environment {
	
	Map _frame; // Exp, Exp
	Environment _parent;
	
	public Environment(Environment parent) {
		_parent = parent;
		_frame = new HashMap();
	}
	public Environment(Environment parent, Map bindings) {
		_parent = parent;
		_frame = bindings;
	}

	public Exp lookupVariableValue(Exp symbol) throws UnboundException {
		if( _frame.containsKey(symbol) ) {
			return (Exp)_frame.get(symbol);
		}
		else if(_parent == null) {
			throw new UnboundException("unbound: " + symbol.toString());
		}
		else {
			return _parent.lookupVariableValue(symbol);
		}
	}
	
	public void setVariableValue(Exp symbol, Exp valu) throws UnboundException {
		if( _frame.containsKey(symbol) ) {
			_frame.put(symbol, valu);
		}
		else if(_parent == null) {
			throw new UnboundException("unbound: " + symbol.toString());
		}
		else {
			setVariableValue(symbol, valu);
		}
	}

	public void defineVariable(Exp symbol, Exp valu) {
			_frame.put(symbol, valu);
	}
	
	private static boolean isFirstSymbol(Exp exp, Exp sym) throws AccessException {
		if( exp.listp() ) {
			return exp.car().equals(sym) ;
		}
		else 
			return false;
	}
	
	public Exp eval(Exp expression) throws Exception {
		if( expression.isSelfEvaluating()) {
			return expression;
		}
		else if( expression.getClass() == Lsymbol.class) {
			return lookupVariableValue(expression);
		}
		else if( isFirstSymbol(expression, SymbolTable.lambdaq) ) {
			return new Procedure(this, expression);
		}
		else if( isFirstSymbol(expression, SymbolTable.lambda) ) {
			return new Procedure(this, expression);
		}
		else if( expression.listp() ) { 
			Exp proc = eval(expression.car());
			Exp[] arguments = this.evalExpressionList(expression.cdr());
			return apply(proc, arguments );
		}

		else 
			return SymbolTable.NIL;
	}
	private Exp apply(Exp proc, Exp[] arguments) throws Exception { // TODO 
		if( BuiltInProcedure.class.isInstance(proc) ) {
			return ((BuiltInProcedure)proc).apply(this, arguments);
		}
		else if(Procedure.class.isInstance(proc)) {
			Map bindings = new HashMap();
			for( int i=0 ; i< arguments.length ; i++ ) {
				bindings.put(((Procedure) proc).getArgument(i), arguments[i]);
			}
			Environment newEnv = new Environment(this, bindings);
			return evalSequence(((Procedure) proc).getBody() , newEnv);
		}
		else {
			throw new Exception("don't know how to apply kind of this prcedure");
		}
	}
	private static Exp evalSequence(Exp body, Environment env) throws Exception {
		if( body.cdr() == SymbolTable.NIL) {
			return env.eval(body.car());
		}
		else {
			env.eval(body.car());
			return evalSequence(body.cdr() ,env);
		}
	}
	private Exp[] evalExpressionList(Exp exp) throws Exception {
		int i = 0;
		Exp[] result = new Exp[exp.length()];
		result[i] = eval(exp.car());
		while( (exp = exp.cdr()) != SymbolTable.NIL) {
			i++;
			result[i] = eval(exp.car());
		}
		return result;
	}

}
