// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io.parser;

import org.genyris.core.Atom;
import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;
import org.genyris.io.InStream;
import org.genyris.io.Parser;

public abstract class StreamParser extends Atom {
	protected InStream _input;
    protected Parser _parser;

    public Exp eval(Environment env) throws GenyrisException {
		return this;
	}
    public void close() throws GenyrisException {
        _input.close();
    }


}
