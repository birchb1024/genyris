package org.lispin.jlispin.interp;

import java.util.HashMap;

import org.lispin.jlispin.core.AccessException;
import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lsymbol;
import org.lispin.jlispin.core.SymbolTable;

public class MagicEnvironment  extends StandardEnvironment {
	
	private Exp _it;
	private Environment _dynamicEnv;
	public MagicEnvironment(Environment runtime, Exp theObject) {
		super(null, new HashMap());
		_it = theObject;
		_dynamicEnv = runtime;
	}

	public Exp lookupVariableShallow(Exp symbol) throws UnboundException {
		if(symbol == SymbolTable.classes) {
			return _it.getClasses();
		}
		else if(symbol == SymbolTable.self) {
			return _it;
		}
		throw new UnboundException("unbound symbol " + symbol.toString());
	}
	
	public Exp lookupInThisClassAndSuperClasses(Exp symbol) throws UnboundException {
		throw new UnboundException("unbound symbol " + symbol.toString());		
	}

	private Exp lookupInClasses(Exp symbol) throws UnboundException {
		Exp classes = _it.getClasses();
		while( classes != SymbolTable.NIL) {
			try {
				Environment klass = (Environment)(classes.car());
				try {
					return (Exp)klass.lookupInThisClassAndSuperClasses(symbol);
				} catch (UnboundException e) {
					;
				} finally {
					classes = classes.cdr();
				}
			}
			catch (AccessException e) {
				throw new UnboundException("bad classes list in object");
			}
		}
		throw new UnboundException("unbound symbol: " + symbol.toString());
	}
	
	public Exp lookupVariableValue(Exp exp) throws UnboundException {
		Lsymbol symbol = (Lsymbol) exp;
		if(symbol == SymbolTable.classes) {
			return _it.getClasses();
		}
		else if(symbol == SymbolTable.self) {
			return _it;
		}
		if(symbol.isMember()) {
			return lookupInClasses(symbol);
		}
		return _dynamicEnv.lookupVariableValue(symbol);
	}
	
	public void defineVariable(Exp symbol, Exp valu) {
		Lsymbol sym = (Lsymbol) symbol;
		if( sym.isMember()) {
			; // TODO silent - maybe throw an exception here?
		} else {
			super.defineVariable(symbol, valu);
		}
	}


	public void setVariableValue(Exp symbol, Exp valu) throws UnboundException {
		Lsymbol sym = (Lsymbol) symbol;
		if( sym.isMember()) {
			; // TODO silent - maybe throw an exception here?
		} else {
			super.setVariableValue(symbol, valu);
		}
	}

}
