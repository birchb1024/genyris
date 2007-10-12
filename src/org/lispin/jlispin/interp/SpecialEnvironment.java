package org.lispin.jlispin.interp;

import java.util.Map;
import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lsymbol;

public class SpecialEnvironment extends StandardEnvironment {
	
	private Environment _object;	

	public SpecialEnvironment(Environment runtime , Map bindings, Environment object)
	{
		super(runtime, bindings);
		_object = object;
	}
	
	public void defineVariable(Exp symbol, Exp valu)  throws LispinException 
    {
        if(! (symbol instanceof Lsymbol) ) {
            throw new LispinException("cannot define non-symbol: " + symbol.toString());            
        }

		Lsymbol sym = (Lsymbol) symbol;
		if( sym.isMember()) {
			_object.defineVariable(symbol, valu);
		} else {
			super.defineVariable(symbol, valu);
		}
	}

	public Exp lookupVariableValue(Exp symbol) throws UnboundException {
		Lsymbol sym = (Lsymbol) symbol;
		if( sym.isMember()) {
			return _object.lookupVariableValue(symbol);
		} else {
			return super.lookupVariableValue(symbol);
		}
	}

	public void setVariableValue(Exp symbol, Exp valu) throws UnboundException {
		Lsymbol sym = (Lsymbol) symbol;
		if( sym.isMember()) {
			_object.setVariableValue(symbol, valu);
		} else {
			super.setVariableValue(symbol, valu);
		}
	}
	public String toString() {
		return "<SpecialEnvironment>";
	}

	public Object getJavaValue() {
		return this;
	}

}
