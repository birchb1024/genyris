package org.genyris.io;



public interface InStream {

	void unGet(char x) throws LexException;
	char readNext();
	public boolean hasData() throws LexException;

}
