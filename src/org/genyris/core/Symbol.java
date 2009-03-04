package org.genyris.core;

public abstract class Symbol extends ExpWithEmbeddedClasses {

	protected String _printName;

    public Symbol(String newSym) {
        _printName = newSym;
    }

	public boolean isNil() {
	    return false;
	}

	public int hashCode() {
	    return getPrintName().hashCode();
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

	public void acceptVisitor(Visitor guest) {
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