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


public class ReaderInStream implements InStream {

    private int _nextByte;
    private PushbackReader _reader;

    public ReaderInStream(Reader reader) {
        _reader = new PushbackReader(reader);
    }


    public void unGet(char x) throws LexException {
        char[] charArray = new char[1];
        charArray[0] = x;
        try {
            _reader.unread(charArray);
        }
        catch (IOException e) {
            throw new LexException(e.getMessage());
        }
    }


    public char readNext() {
        return (char)_nextByte;
    }

    public boolean hasData() {
        try {
            _nextByte = _reader.read();
        }
        catch (IOException e) {
            return false;
        }
        if( _nextByte == -1 ) {
            return false;
        }
        else {
            return true;
        }
    }


    public void close() throws GenyrisException {
        try {
            _reader.close();
        }
        catch (IOException e) {
           throw new GenyrisException(e.getMessage());
        }
    }

}
