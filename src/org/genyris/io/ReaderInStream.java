// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;

public class ReaderInStream implements InStream {

    private int _nextByte;
    private boolean _haveChar;
    private PushbackReader _reader;
    private int _lineCount;

    public ReaderInStream(Reader reader) {
        _reader = new PushbackReader(reader);
        _haveChar = false;
        _lineCount = 1;
    }

    public Reader getReader() {
        return _reader;
    }

    public void unGet(char x) throws LexException {
        char[] charArray = new char[1];
        charArray[0] = x;
        try {
            _reader.unread(charArray);
        } catch (IOException e) {
            throw new LexException(e.getMessage());
        }
    }

    public char readNext() {
        _haveChar = false;
        if (_nextByte == '\n') {
            _lineCount++;
        }
        return (char) _nextByte;
    }

    public boolean hasData() {
        try {
            if (_haveChar) {
                return true;
            }
            _nextByte = _reader.read();
        } catch (IOException e) {
            return false;
        }
        if (_nextByte == -1) {
            _haveChar = false;
            return false;
        } else {
            _haveChar = true;
            return true;
        }
    }

    public void close() throws GenyrisException {
        try {
            _reader.close();
        } catch (IOException e) {
            throw new GenyrisException(e.getMessage());
        }
    }

    public void resetAfterError() {
        _haveChar = false;
    }

    public void withinExpression(Environment env) {
    }

    public void beginningExpression() {
    }

    public int getLineNumber() {
        return _lineCount;
    }

}
