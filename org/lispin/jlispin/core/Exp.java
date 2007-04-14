package org.lispin.jlispin.core;

public abstract class Exp {
	
	public boolean equals(Object compare) {
		if( compare.getClass() == this.getClass()) {
			return this.getValue() == ((Exp)compare).getValue();
		}
		else {
			return false;
		}
	}
	
	public abstract Object getValue();

}
