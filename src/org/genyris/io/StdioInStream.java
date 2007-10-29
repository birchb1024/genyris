// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io;

import java.io.IOException;


public class StdioInStream implements InStream {

    private int _nextByte;

    public void unGet(char x) throws LexException {
        ;
    }


    public char readNext() {
        return (char)_nextByte;
    }

    public boolean hasData() {
        try {
            _nextByte = System.in.read();
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

}
