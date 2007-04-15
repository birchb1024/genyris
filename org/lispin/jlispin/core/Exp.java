package org.lispin.jlispin.core;

public abstract class Exp {

	public boolean equals(Object compare) {
		if( compare.getClass() == this.getClass()) {
			return this.getJavaValue().equals(((Exp)compare).getJavaValue());
		}
		else {
			return false;
		}
	}
	
	public abstract Object getJavaValue();
	
	public String toString() {
		return getJavaValue().toString();
	}

    //-----------------------
	
	public boolean isNil() {
		return this == SymbolTable.NIL;
	}
	
	protected String cdrToString() {
		return " . " + toString();
	}
	
	public Exp car() throws AccessException {
		throw new AccessException("attempt to take car of non-cons");
	}

	public Exp cdr() throws AccessException {
		throw new AccessException("attempt to take cdr of non-cons");
	}
	
	public boolean listp() {
		if( getClass() == Lcons.class )
			return true;
		else 
			return false;
	}

	public boolean isSelfEvaluating() {
		return true;
	}
	
	public int length() throws AccessException {
		Exp tmp = this;
		int count = 1;
		while((tmp = tmp.cdr()) != SymbolTable.NIL) {
			count++;
		}
		return count;
	}

	public Exp nth(int number) throws AccessException {
		Exp tmp = this;
		int count = 0;
		do {
			if( count == number ) {
				return tmp.car();
			}
			tmp = tmp.cdr();
			count++;
		} while(tmp != SymbolTable.NIL);
		throw new AccessException("nth could not find item: " + number);
	}

}
