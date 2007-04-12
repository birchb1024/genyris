package org.lispin.jlispin.core;

public class UngettableInStream implements InStream {

		private InStream _input;
		private boolean _ungetCharPresent; 
		private char _ungetChar; 	
		
		public UngettableInStream(InStream aStream) {
			_input = aStream;
			_ungetCharPresent = false;
		}
		
		public char lgetc() {
			if( _ungetCharPresent ) {
				_ungetCharPresent = false;
				return _ungetChar;
			}
			else {
				return _input.lgetc();
			}
		}

		public void lungetc(char x) throws LexException {
			if( _ungetCharPresent ) {
				throw new LexException("TODO");
			}
			else {
				_ungetCharPresent = true;
				_ungetChar = x;
			}
		}
}

