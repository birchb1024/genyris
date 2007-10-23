package org.genyris.interp;

import java.util.HashMap;
import java.util.Map;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Lsymbol;
import org.genyris.core.NilSymbol;

// TODO Break this into a Root environment and a Standard Env....
public class StandardEnvironment implements Environment {
	
	Map _frame; // Exp, Exp
	Environment _parent; 
	protected Lsymbol NIL;
	protected Exp _self, __self, _classes, _superclasses, _classname;
	private Interpreter _interpreter;
	
	
	public StandardEnvironment(NilSymbol nil) {
		_parent = null;
		_frame = new HashMap();
		NIL = nil;
		_interpreter = null;
	}

	public StandardEnvironment(Interpreter interp, NilSymbol nil) {
		_parent = null;
		_frame = new HashMap();
		NIL = nil;
		_interpreter = interp;
		
		_self = interp.getSymbolTable().internString(Constants.SELF);
		__self = interp.getSymbolTable().internString(Constants._SELF);// TODO DRY
		_classes = interp.getSymbolTable().internString(Constants.CLASSES);
		_superclasses = interp.getSymbolTable().internString(Constants.SUPERCLASSES);
		_classname = interp.getSymbolTable().internString(Constants.CLASSNAME);
	}

	private void init() {
		NIL = _parent.getNil();
		_self = _parent.internString(Constants.SELF);
		__self = _parent.internString(Constants._SELF);// TODO DRY
		_classes = _parent.internString(Constants.CLASSES);
		_superclasses = _parent.internString(Constants.SUPERCLASSES);
		_classname = _parent.internString(Constants.CLASSNAME);		
	}
	public StandardEnvironment(Environment parent) {
		_parent = parent;
		_frame = new HashMap();
		init();
	}
	public StandardEnvironment(Environment parent, Map bindings) {
		_parent = parent;
		_frame = bindings;
		init();
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

	public void defineVariable(Exp symbol, Exp valu) throws GenyrisException {
        if(! (symbol instanceof Lsymbol) ) {
			throw new GenyrisException("cannot define non-symbol: " + symbol.toString());            
        }
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

	public Lsymbol getNil() {
		return NIL;
	}

	public Interpreter getInterpreter() {
		if( _parent != null )
			return _parent.getInterpreter();
		else
			return _interpreter;
	}

	public Exp internString(String symbolName) {
		if(_interpreter == null) {
			return _parent.internString(symbolName);
		}
		else {
			return _interpreter.getSymbolTable().internString(symbolName);
		}
	}

}
