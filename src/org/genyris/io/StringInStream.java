// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io;

import org.genyris.exception.GenyrisException;


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

    public void close() throws GenyrisException {}

}
