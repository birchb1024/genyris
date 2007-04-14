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

}
