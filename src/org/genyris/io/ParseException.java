// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io;

import org.genyris.exception.GenyrisException;

public class ParseException extends GenyrisException {

    private static final long serialVersionUID = 3268672144858986389L;

        public ParseException(String string) {
            super(string);
        }

    }
