package org.genyris.io;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;


public class ReaderInStream implements InStream {
	
	private int _nextByte;
	private PushbackReader _reader;

	public ReaderInStream(Reader reader) {
		_reader = new PushbackReader(reader);
	}


	public void unGet(char x) throws LexException {
		char[] charArray = new char[1];
		charArray[0] = x;
		try {
			_reader.unread(charArray);
		}
		catch (IOException e) {
			throw new LexException(e.getMessage());
		}
	}
	
	
	public char readNext() {
		return (char)_nextByte;
	}
	
	public boolean hasData() {
		try {
			_nextByte = _reader.read();
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
