package org.lispin.jlispin.io;


public class ConvertEofInStream implements InStream {
	
	private boolean _haveSavedByte;
	private int _nextByte;
	private InStreamEOF _input;

	
	public ConvertEofInStream(InStreamEOF in) {
		_input = in;
		_nextByte = (char)-1;
		_haveSavedByte = false;
	}
	public void unGet(char x) throws LexException {
		throw new LexException("unGet() not implemented in ConvertEofInStream!");
	}
	
	public char readNext() {
		char result = (char)_nextByte;
		_haveSavedByte = false;
		return result;
	}
	
	public boolean hasData() throws LexException {

		if(_haveSavedByte)
			return _nextByte != InStreamEOF.EOF;
		else {
			_nextByte = _input.getChar();
			_haveSavedByte = true;
			return _nextByte != InStreamEOF.EOF;
		} 
	}
	
}
