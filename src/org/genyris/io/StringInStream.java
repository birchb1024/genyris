// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io;

import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;


public class StringInStream implements InStream {

    private final char[] _value;
    private int _readPointer;
    private int _lineCount;

    public StringInStream(String astring) {
        _value = astring.toCharArray();
        _readPointer = 0;
        _lineCount = 1;
    }

    public boolean hasData() {
        return this._readPointer <_value.length;
    }

    public char readNext() {
        if( _value[_readPointer] == '\n' ) {
            _lineCount++;
        }
        return _value[_readPointer++];
    }

    public  void unGet(char x) throws LexException {
        throw new LexException("PipeInStream: unGet not supported!");

    }

    public void close() throws GenyrisException {}

	public void resetAfterError() {
	}
	
    public void withinExpression(Environment env) {
    }

    public void beginningExpression() {
    }

    public int getLineNumber() {
        return this._lineCount;
    }
    public String getFilename() {
        return "StringInStream " + hashCode();
    }

}
