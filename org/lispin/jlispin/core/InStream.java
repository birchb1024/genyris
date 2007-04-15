package org.lispin.jlispin.core;


public interface InStream {

	void unGet(char x) throws LexException;
	char lgetc();
	public boolean hasData();

}
