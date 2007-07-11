package org.lispin.jlispin.core;

import java.io.StringWriter;

import org.lispin.jlispin.format.BasicFormatter;

public abstract class Exp implements Classifiable {

	public abstract Object getJavaValue();
	public abstract void acceptVisitor(Visitor guest);
		

	public Exp car() throws AccessException {
		throw new AccessException("attempt to take car of non-cons");
	}
	
	public Exp cdr() throws AccessException {
		throw new AccessException("attempt to take cdr of non-cons");
	}
	
	public Exp setCar(Exp exp) throws AccessException {
		throw new AccessException("attempt to set car of non-cons");
	}
	
	public Exp setCdr(Exp exp) throws AccessException {
		throw new AccessException("attempt to set car of non-cons");
	}

	public int hashCode() {
		return getJavaValue().hashCode();
	}

	public boolean equals(Object compare) {
		if (compare.getClass() != this.getClass())
			return false;
		else
			return this.getJavaValue().equals(((Exp) compare).getJavaValue());
	}

	public String toString() {
		StringWriter out = new StringWriter();
		acceptVisitor(new BasicFormatter(out));
		return out.getBuffer().toString();
	}

	public boolean isNil() {
		return this == SymbolTable.NIL;
	}


	public boolean listp() {
		return (this.getClass() == Lcons.class);
	}

	public boolean isSelfEvaluating() {
		return true;
	}

	public int length() throws AccessException {
		Exp tmp = this;
		int count = 0;

		while (tmp != SymbolTable.NIL) {
			tmp = tmp.cdr();
			count++;
		}
		return count;
	}

	public Exp nth(int number) throws AccessException {
		if (this == SymbolTable.NIL) {
			throw new AccessException("nth called on nil.");
		}
		Exp tmp = this;
		int count = 0;
		while (tmp != SymbolTable.NIL) {
			if (count == number) {
				return tmp.car();
			}
			tmp = tmp.cdr();
			count++;
		}
		throw new AccessException("nth could not find item: " + number);
	}

	public Exp last() throws AccessException {
		if (this == SymbolTable.NIL)
			return SymbolTable.NIL;
		Exp tmp = this;
		while (tmp.cdr() != SymbolTable.NIL) {
			tmp = tmp.cdr();
		}
		return tmp.car();
	}

}
