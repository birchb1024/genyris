// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io;

import org.genyris.exception.GenyrisException;


public interface InStreamEOF {

    public static final int EOF = -1;

    int getChar()  throws LexException;                // returns EOF on end of file.

    void close()  throws GenyrisException ;

	void resetAfterError();

}
