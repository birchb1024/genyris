package org.lispin.jlispin.core;

public class ConvertEofInStream implements InStream {
	
	private int _nextByte;
	private InStreamEOF _input;

	
	public ConvertEofInStream(InStreamEOF in) {
		_input = in;
		_nextByte = (char)-1;
	}
	public void unGet(int x) throws LexException {
		;
	}
	
	public int getChar() throws LexException {
		if( _nextByte == InStreamEOF.EOF)
			throw new LexException("hasData() not called prior to lgetc().");
		int result = _nextByte;
		_nextByte = (char)InStreamEOF.EOF;
		return result;
	}
	
	public boolean hasData() {
		try {
			_nextByte = _input.getChar();
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
