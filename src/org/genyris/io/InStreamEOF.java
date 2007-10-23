package org.genyris.io;


public interface InStreamEOF {
	
	public static final int EOF = -1;
	
	int getChar()  throws LexException;				// returns EOF on end of file.

}
