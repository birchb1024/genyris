package org.lispin.jlispin.core;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.lispin.jlispin.format.BasicFormatter;

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
		StringWriter out = new StringWriter();
		acceptVisitor(new BasicFormatter(out));
		return  out.getBuffer().toString();
	}
	
	public abstract void acceptVisitor(Visitor guest);

    //-----------------------
	
	public boolean isNil() {
		return this == SymbolTable.NIL;
	}
		
	public Exp car() throws AccessException {
		throw new AccessException("attempt to take car of non-cons");
	}

	public Exp setCar(Exp exp) throws AccessException {
		throw new AccessException("attempt to set car of non-cons");
	}
	public Exp cdr() throws AccessException {
		throw new AccessException("attempt to take cdr of non-cons");
	}
	public Exp setCdr(Exp exp) throws AccessException {
		throw new AccessException("attempt to set car of non-cons");
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
		int count = 0;
		
		while(tmp != SymbolTable.NIL) {
			tmp = tmp.cdr();
			count++;
		}
		return count;
	}

	public Exp nth(int number) throws AccessException {
		if(this == SymbolTable.NIL) {
			throw new AccessException("nth called on nil.");			
		}
		Exp tmp = this;
		int count = 0;
		while(tmp != SymbolTable.NIL) {
			if( count == number ) {
				return tmp.car();
			}			
			tmp = tmp.cdr();
			count++;
		}
		throw new AccessException("nth could not find item: " + number);
	}

	public void printSpaces(int level, PrintWriter output) {
		for( int i=0;i<level;i++) 
			output.print(".");
	}

	public void print(int level, PrintWriter output) {
		printSpaces(level,output);
		output.print("Exp:print: " + this.toString());
	}

}
