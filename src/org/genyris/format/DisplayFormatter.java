// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.format;

import java.io.IOException;
import java.io.Writer;
import org.genyris.core.Lstring;
import org.genyris.core.Lsymbol;

public class DisplayFormatter extends BasicFormatter {
    public DisplayFormatter(Writer out, Lsymbol nil) {
        super(out, nil);
    }

    public void visitLstring(Lstring lst) {
        try {
            _output.write(lst.getJavaValue().toString());
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
