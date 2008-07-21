// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.string;

import org.genyris.core.Lstring;
import org.genyris.core.Lsymbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.AbstractMethod;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public abstract class AbstractStringMethod extends AbstractMethod {


    public AbstractStringMethod(Interpreter interp, Lsymbol name) {
        super(interp, name);
    }

    protected Lstring getSelfString(Environment env) throws GenyrisException {
        getSelf(env);
        if (!(_self instanceof Lstring)) {
            throw new GenyrisException("Non-String passed to getSelfString: " + _self.toString());
        } else {
            Lstring theString = (Lstring) _self;
            return theString;
        }
    }
}
