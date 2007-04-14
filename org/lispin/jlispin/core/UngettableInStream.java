package org.lispin.jlispin.core;

public class UngettableInStream implements InStream {

		private InStream _input;
		private char[] _ungetChars; 	
		private int _readPointer;
		

		public UngettableInStream(InStream aStream, int size) {
			_input = aStream;
			_ungetChars = new char[size];
			_readPointer = -1;
		}
		
		public UngettableInStream(InStream aStream) {
			_input = aStream;
			_ungetChars = new char[10];
			_readPointer = -1;		}

		private boolean bufferEmpty() {
			return _readPointer < 0;
		}
		private boolean bufferFull() {
			return _readPointer >= _ungetChars.length - 1 ;
		}

		public boolean hasData() {
			return !bufferEmpty() || _input.hasData();
		}
		
		public char lgetc() {
			if( bufferEmpty() ) {
				return _input.lgetc();
			}
			else {
				return _ungetChars[_readPointer--];
			}
		}

		public void unGet(char x) throws LexException {
			if( bufferFull() ) {
				throw new LexException("too many characters pushed back on ungettable stream");
			}
			else {
				_readPointer++;
				_ungetChars[_readPointer] = x;
			}
		}
}

