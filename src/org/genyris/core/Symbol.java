package org.genyris.core;

import java.net.MalformedURLException;
import java.net.URL;

import org.genyris.exception.GenyrisException;

public abstract class Symbol extends ExpWithEmbeddedClasses {

	public static Symbol symbolFactory(String name) {
		try {
			new URL(name);
			return new FullyQualifiedSymbol(name);
		} catch (MalformedURLException e) {
			return new SimpleSymbol(name);
		}
	}
	protected String _printName;

    public Symbol(String newSym) {
        _printName = newSym;
    }

	public boolean isNil() {
	    return false;
	}

	public int hashCode() {
	    return _printName.hashCode();
	}

    public boolean deepEquals(Object compare) {
        return equals(compare);
    }
	public boolean equals(Object compare) {
	    return this == compare;
	}

	public String getPrintName() {
	    return _printName;
	}

	public Object getJavaValue() {
	    return getPrintName();
	}

	public boolean isSelfEvaluating() {
	    return false;
	}

	public void acceptVisitor(Visitor guest) throws GenyrisException  {
	    guest.visitSymbol(this);
	}

	public String toString() {
	    return getPrintName();
	}

	public abstract String getBuiltinClassName();

	public int compareTo(Object arg0) {
	    return ((SimpleSymbol) arg0)._printName.compareTo(this._printName);
	}

}