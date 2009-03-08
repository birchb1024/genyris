// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.format;

import java.io.Writer;

import org.genyris.core.Lstring;
import org.genyris.exception.GenyrisException;

public class DisplayFormatter extends BasicFormatter {
    public DisplayFormatter(Writer out) {
        super(out);
    }

    public void visitLstring(Lstring lst) throws GenyrisException {
        write(lst.getJavaValue().toString());
    }
}
