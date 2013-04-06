// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io;

import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;


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
    public void close() throws GenyrisException {
        _input.close();
    }
	public void resetAfterError() {
        _nextByte = (char)-1;
        _haveSavedByte = false;
		_input.resetAfterError();
	}
    public void withinExpression(Environment env) {
        _input.withinExpression(env);
    }

    public void beginningExpression() {
        _input.beginningExpression();
    }

}
