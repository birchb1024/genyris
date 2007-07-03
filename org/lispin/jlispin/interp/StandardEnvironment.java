package org.lispin.jlispin.interp;

import java.util.HashMap;
import java.util.Map;

import org.lispin.jlispin.core.Exp;
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
	public Exp lookupVariableShallow(Exp symbol) throws UnboundException {
		if( _frame.containsKey(symbol) ) {
			return (Exp)_frame.get(symbol);
		} else { 
			throw new UnboundException("frame does not contain key: " + symbol.toString());
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
	
	
	
	public String toString() {
		if( _parent != null ) {
			return _parent.toString() + "/" + _frame.toString();
		}
		else {
			return "/" + _frame.toString();			
		}
	}
	public Exp lookupInThisClassAndSuperClasses(Exp symbol) throws UnboundException {
		throw new UnboundException("lookupInSuperClasses not implemented for standard environments.");
	}

}
