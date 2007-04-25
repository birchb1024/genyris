package org.lispin.jlispin.io;


public class StringInStream implements InStream {

	private final char[] _value;
	private int _readPointer;
	
	public StringInStream(String astring) {
		_value = astring.toCharArray();
		_readPointer = 0;
	}

	public boolean hasData() {
		return this._readPointer <_value.length;
	}
	
	public char readNext() {
		return _value[_readPointer++];
	}

	public  void unGet(char x) throws LexException {
		throw new LexException("StringInStream: unGet not supported!");

	}

}
