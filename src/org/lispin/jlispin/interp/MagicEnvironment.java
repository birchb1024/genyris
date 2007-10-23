package org.lispin.jlispin.interp;

import java.util.HashMap;

import org.genyris.core.AccessException;
import org.genyris.core.Exp;
import org.genyris.core.Lsymbol;

public class MagicEnvironment  extends StandardEnvironment {
	
	private Exp _it;
	public MagicEnvironment(Environment runtime, Exp theObject) {
		super(runtime, new HashMap());
		_it = theObject;
	}

	public Exp lookupVariableShallow(Exp symbol) throws UnboundException {
		if(symbol == _classes) {
			return _it.getClasses(NIL);
		}
		else if(symbol == _self) {
			return _it;
		}
		throw new UnboundException("unbound symbol " + symbol.toString());
	}
	
	public Exp lookupInThisClassAndSuperClasses(Exp symbol) throws UnboundException {
		throw new UnboundException("unbound symbol " + symbol.toString());		
	}

	private Exp lookupInClasses(Exp symbol) throws UnboundException {
		Exp classes = _it.getClasses(NIL);
		while( classes != NIL) {
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
		if(symbol == _classes) {
			return _it.getClasses(NIL);
		}
		else if(symbol == _self || symbol == __self) {
			return _it;
		}
		if(symbol.isMember()) {
			return lookupInClasses(symbol);
		}
		return super.lookupVariableValue(symbol);
	}
	
	public void defineVariable(Exp symbol, Exp valu)  throws LispinException 
    {
        if(! (symbol instanceof Lsymbol) ) {
            throw new LispinException("cannot define non-symbol: " + symbol.toString());            
        }

		Lsymbol sym = (Lsymbol) symbol;
		if( sym.isMember()) {
            throw new LispinException("cannot define non-member: " + symbol.toString());            
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
