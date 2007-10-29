// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io;


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
            _readPointer = -1;        }

        private boolean bufferEmpty() {
            return _readPointer < 0;
        }
        private boolean bufferFull() {
            return _readPointer >= _ungetChars.length - 1 ;
        }

        public boolean hasData() throws LexException {
            return !bufferEmpty() || _input.hasData();
        }

        public char readNext() {
            if( bufferEmpty() ) {
                return _input.readNext();
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

