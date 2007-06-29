package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.AccessException;
import org.lispin.jlispin.core.Exp;
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
		// TODO create new special env and env for self, apply body
		return null;
	}
	public Exp[] computeArguments(Environment env, Exp exp) throws LispinException {
		// TODO same as quote &rest
		return null;
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
		return 1;
	}

//	public void defineVariable(Exp symbol, Exp valu) {
//		_delegate.defineVariable(symbol, valu);
//	}
//
//	public Exp eval(Exp expression) throws UnboundException, AccessException, LispinException {
//		return _delegate.eval(expression);
//	}
//
//	public Exp evalSequence(Exp body) throws LispinException {
//		return _delegate.evalSequence(body);
//	}
//
//	public Exp lookupVariableValue(Exp symbol) throws UnboundException {
//		return _delegate.lookupVariableValue(symbol);
//	}
//
//	public void setVariableValue(Exp symbol, Exp valu) throws UnboundException {
//		_delegate.setVariableValue(symbol, valu);
//	}

	public String toString() {
		return "<CallableEnvironment>";
	}
	

}
