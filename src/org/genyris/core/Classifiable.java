// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;

import org.genyris.exception.AccessException;
import org.genyris.interp.Environment;

public interface Classifiable {

    public Exp getClasses(Environment env);
    public void addClass(Exp klass);
    public void removeClass(Exp klass);
    public boolean isTaggedWith(Lobject klass);
    public void setClasses(Exp classList, Exp NIL) throws AccessException;
	public Symbol getBuiltinClassSymbol(Internable table);

}
