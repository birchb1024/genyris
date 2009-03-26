package org.genyris.core;

import java.net.URI;
import java.net.URISyntaxException;

import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;
import org.genyris.interp.UnboundException;

public abstract class Symbol extends Atom {

    public static SimpleSymbol symbolFactory(String name, boolean escaped) {
        try {
            URI uri = new URI(name);
            if(uri.isAbsolute())
                return new URISymbol(name);
        } catch (URISyntaxException e) { }
        return (escaped?new EscapedSymbol(name):new SimpleSymbol(name));
    }

    public boolean isNil() {
        return false;
    }

    public abstract String getPrintName();

    public String toString() {
        return getPrintName();
    }

    public abstract Exp lookupVariableValue(Environment env) throws UnboundException;

    public abstract void setVariableValue(Environment env, Exp valu) throws UnboundException;

    public abstract void defineVariable(Environment env, Exp valu) throws GenyrisException;

    public abstract int compareTo(Object arg0);

	public Exp eval(Environment env) throws GenyrisException {
		return env.lookupVariableValue(this);
    }


}