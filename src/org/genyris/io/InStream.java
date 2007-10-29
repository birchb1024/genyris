// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io;



public interface InStream {

    void unGet(char x) throws LexException;
    char readNext();
    public boolean hasData() throws LexException;

}
