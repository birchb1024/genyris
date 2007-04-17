package org.lispin.jlispin.core;

import java.io.IOException;

public class StdioInStream implements InStream {
	
	private int _nextByte;

	public void unGet(char x) throws LexException {
		;
	}
	
	
	public char getChar() {
		return (char)_nextByte;
	}
	
	public boolean hasData() {
		try {
			_nextByte = System.in.read();
		}
		catch (IOException e) {
			return false;
		}	
		if( _nextByte == -1 ) {
			return false;
		}
		else {
			return true;
		}
	}
	
}
