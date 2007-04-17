package org.lispin.jlispin.core;


public interface InStream {

	void unGet(char x) throws LexException;
	char getChar();
	public boolean hasData();

}
