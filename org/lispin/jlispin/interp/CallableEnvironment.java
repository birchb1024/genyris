package org.lispin.jlispin.interp;

import java.util.HashMap;
import java.util.Map;

import org.lispin.jlispin.core.AccessException;
import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.core.Visitor;

public class CallableEnvironment extends Exp implements Closure {
	
	private Environment _delegate;
	
	public CallableEnvironment(Environment parent) {
		_delegate = parent;
	}

	public void acceptVisitor(Visitor guest) {
		guest.visitCallableEnvironment(this);
		
	}
	public Object getJavaValue() {
		return "<CallableEnvironment>";
	}
	public Exp applyFunction(Environment environment, Exp[] arguments) throws LispinException {
		Map bindings = new HashMap();
		bindings.put(SymbolTable.self, this);
		Environment newEnv = new SpecialEnvironment(environment, bindings, _delegate); 
		return newEnv.evalSequence(arguments[0]);
	}
	public Exp[] computeArguments(Environment env, Exp exp) throws LispinException {
		Exp[] result = new Exp[1];
		result[0] = exp; // same as &rest
		return result;
	}
	public Exp getArgumentOrNIL(int index) throws AccessException {
		return null;
	}
	public Exp getBody() throws AccessException {
		// TODO return &rest argument?
		return null;
	}
	public Environment getEnv() {
		// TODO Auto-generated method stub
		return null;
	}
	public Exp getLastArgumentOrNIL(int i) throws AccessException {
		// TODO Auto-generated method stub
		return null;
	}
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	public int getNumberOfRequiredArguments() throws AccessException {
		return 0;
	}

	public String toString() {
		return "<CallableEnvironment>";
	}
	

}
