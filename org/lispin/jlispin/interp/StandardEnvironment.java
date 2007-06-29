package org.lispin.jlispin.interp;

import java.util.HashMap;
import java.util.Map;

import org.lispin.jlispin.core.AccessException;
import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lsymbol;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.core.Visitor;

public class StandardEnvironment extends Environment {
	
	Map _frame; // Exp, Exp
	Environment _parent;
	
	public StandardEnvironment(Environment parent) {
		_parent = parent;
		_frame = new HashMap();
	}
	public StandardEnvironment(Environment parent, Map bindings) {
		_parent = parent;
		_frame = bindings;
	}

	public void acceptVisitor(Visitor guest) {
		guest.visitStandardEnvironment(this);
		
	}
	public Object getJavaValue() {
		return "<StandardEnvironment>";
	}
	
	public Exp lookupVariableValue(Exp symbol) throws UnboundException {
		if( _frame.containsKey(symbol) ) {
			return (Exp)_frame.get(symbol);
		}
		else if(_parent == null) {
			throw new UnboundException("unbound variable: " + symbol.toString());
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
			_parent.setVariableValue(symbol, valu);
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
	
	public Exp eval(Exp expression) throws UnboundException, AccessException, LispinException {
		if( expression.isSelfEvaluating()) {
			return expression;
		}
		else if( expression.getClass() == Lsymbol.class) {
			return lookupVariableValue(expression);
		}
		else if( isFirstSymbol(expression, SymbolTable.lambdam) ) { 
			return new LazyProcedure(this, expression, new MacroFunction());
		}
		else if( isFirstSymbol(expression, SymbolTable.lambdaq) ) { 
			return new LazyProcedure(this, expression, new ClassicFunction());
		}
		else if( isFirstSymbol(expression, SymbolTable.lambda) ) { 
			return new EagerProcedure(this, expression,  new ClassicFunction());
		}
		else if( expression.listp() ) { 
			AbstractClosure proc = (AbstractClosure) eval(expression.car());
			Exp[] arguments = proc.computeArguments(this, expression.cdr());
			return proc.applyFunction(this, arguments );
		}

		else 
			return SymbolTable.NIL;
	}

	public Exp evalSequence(Exp body) throws LispinException {
		if( body.cdr() == SymbolTable.NIL) {
			return this.eval(body.car());
		}
		else {
			this.eval(body.car());
			return this.evalSequence(body.cdr());
		}
	}
	
	public String toString() {
		if( _parent != null ) {
			return _parent.toString() + "/" + _frame.toString();
		}
		else {
			return "/" + _frame.toString();			
		}
	}

}
