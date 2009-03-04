// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;

import org.genyris.classification.ClassWrapper;
import org.genyris.interp.EagerProcedure;
import org.genyris.interp.LazyProcedure;

public interface Visitor {
    public void visitLcons(Lcons cons);

    public void visitLdouble(Ldouble dub);

    public void visitLinteger(Linteger lint);

    public void visitLstring(Lstring lst);

    public void visitSymbol(Symbol sym);

    public void visitLazyProc(LazyProcedure lproc);

    public void visitEagerProc(EagerProcedure eproc);

    public void visitLobject(Lobject frame);

    public void visitBignum(Bignum bignum);

    public void visitClassWrapper(ClassWrapper klass);

    public void visitExpWithEmbeddedClasses(ExpWithEmbeddedClasses exp);
}
