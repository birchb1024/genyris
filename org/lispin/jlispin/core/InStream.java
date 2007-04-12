package org.lispin.jlispin.core;

public interface InStream {

	void lungetc(char x) throws LexException;
	char lgetc();

}
