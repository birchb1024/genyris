package org.genyris.core;

import java.net.MalformedURLException;
import java.net.URL;

import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.UnboundException;

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

	public void acceptVisitor(Visitor guest) throws GenyrisException {
		guest.visitSymbol(this);
	}

	public String toString() {
		return getPrintName();
	}

	public abstract String getBuiltinClassName();

	public int compareTo(Object arg0) {
		return ((SimpleSymbol) arg0)._printName.compareTo(this._printName);
	}

	public static Symbol realSymbol(Exp dynamicOrReal, Symbol DYNAMIC)
			throws UnboundException {
		Symbol retval = realSymbolOrNull(dynamicOrReal, DYNAMIC);
		if (retval == null) {
			throw new UnboundException("Bad dynamic symbol: "
					+ dynamicOrReal.toString());
		} else {
			return retval;
		}
	}

	public static boolean isDynamic(Exp dynamicOrReal, Symbol DYNAMIC) throws UnboundException {
		if (dynamicOrReal.listp()) {
			try {
				if (dynamicOrReal.car() == DYNAMIC) {
					Exp possibleSymbol = dynamicOrReal.cdr().car();
					if (possibleSymbol instanceof Symbol) {
						return true;
					}
				}
			} catch (AccessException next) {
				throw new UnboundException("Bad dynamic symbol: " + dynamicOrReal);
			}
			return false;

		} else {
			return false;
		}
	}

	private static Symbol realSymbolOrNull(Exp dynamicOrReal, Symbol DYNAMIC)
			throws UnboundException {
		if (Symbol.isDynamic(dynamicOrReal, DYNAMIC)) {
			try {
				return (Symbol) dynamicOrReal.cdr().car();
			} catch (AccessException e) {
				return null;
			}
		} else if (dynamicOrReal instanceof Symbol) {
			return (Symbol) dynamicOrReal;
		} else {
			return null;
		}
	}

}