package org.lispin.jlispin.interp;

import java.util.HashMap;
import java.util.Map;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lcons;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.core.Visitor;

public class CallableEnvironment extends Exp implements Environment, Closure {
	
	private Environment _delegate;
	
	public CallableEnvironment(Environment parent) {
		_delegate = parent;
	}

	public void acceptVisitor(Visitor guest) {
		guest.visitCallableEnvironment(this);		
	}
	public Object getJavaValue() {
		return toString();
	}
	public Exp applyFunction(Environment environment, Exp[] arguments) throws LispinException {
		Map bindings = new HashMap();
		bindings.put(SymbolTable.self, this);
		SpecialEnvironment newEnv = new SpecialEnvironment(environment, bindings, _delegate); 
		return Evaluator.evalSequence(newEnv, arguments[0]);
	}
	public Exp[] computeArguments(Environment env, Exp exp) throws LispinException {
		Exp[] result = new Exp[1];
		result[0] = exp; // same as &rest
		return result;
	}

	public String toString() {
		return "<CallableEnvironment" + _delegate.toString() + ">";
	}

	public void defineVariable(Exp symbol, Exp valu) {
		_delegate.defineVariable(symbol, valu);
		
	}

	public Exp lookupVariableValue(Exp symbol) throws UnboundException {
		return _delegate.lookupVariableValue(symbol);
	}

	public void setVariableValue(Exp symbol, Exp valu) throws UnboundException {
		setVariableValue(symbol, valu);		
	}

	public Exp lookupVariableShallow(Exp symbol) throws UnboundException {
		return _delegate.lookupVariableShallow(symbol);
	}

	public Exp lookupInThisClassAndSuperClasses(Exp symbol) throws UnboundException {
		return _delegate.lookupInThisClassAndSuperClasses(symbol);
	}

	public void addClass(Exp klass) {
		try {
			Exp classes = _delegate.lookupVariableShallow(SymbolTable.classes);
			_delegate.setVariableValue(SymbolTable.classes, new Lcons (klass, classes));
		}
		catch (UnboundException e) {
			_delegate.defineVariable(SymbolTable.classes, klass);
		}
	}

	public Exp getClasses() {
		try {
			return _delegate.lookupVariableShallow(SymbolTable.classes);
		}
		catch (UnboundException e) {
			return SymbolTable.NIL;
		}
	}

	public void removeClass(Exp klass) {
		; // TODO
	}
	
}
