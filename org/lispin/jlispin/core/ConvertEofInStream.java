package org.lispin.jlispin.core;

public class ConvertEofInStream implements InStream {
	
	private int _nextByte;
	private InStreamEOF _input;

	
	public ConvertEofInStream(InStreamEOF in) {
		_input = in;
		_nextByte = InStreamEOF.EOF;
	}
	public void unGet(char x) throws LexException {
		;
	}
	
	public char getChar() {
		char result = (char)_nextByte;
		_nextByte = InStreamEOF.EOF;
		return result;
	}
	
	public boolean hasData() {
		try {
			if( _nextByte != InStreamEOF.EOF ) {
				return true;
			}
			_nextByte = _input.getChar();
			System.out.print((char)_nextByte);
			if( _nextByte == InStreamEOF.EOF)
				return false;
			else
				return true;
		} 
		catch (LexException e) {
			return false;
		}

	}
	
}
