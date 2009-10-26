// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;

public interface Closure { // TODO rename this to "Applyable" 

    public abstract Exp[] computeArguments(Environment env, Exp exp) throws GenyrisException;

    public abstract Exp applyFunction(Environment environment, Exp[] arguments) throws GenyrisException;
    
    public abstract String toString();
    
}