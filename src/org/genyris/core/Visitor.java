// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;

import org.genyris.classification.ClassWrapper;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.EagerProcedure;
import org.genyris.interp.LazyProcedure;

public interface Visitor {
	public void visitLcons(Lcons cons) throws GenyrisException;

	public void visitLdouble(Ldouble dub) throws GenyrisException;

	public void visitLinteger(Linteger lint) throws GenyrisException;

	public void visitLstring(Lstring lst) throws GenyrisException;

	public void visitLazyProc(LazyProcedure lproc) throws GenyrisException;

	public void visitEagerProc(EagerProcedure eproc) throws GenyrisException;

	public void visitLobject(Lobject frame) throws GenyrisException;

	public void visitBignum(Bignum bignum) throws GenyrisException;

	public void visitClassWrapper(ClassWrapper klass) throws GenyrisException;

	public void visitExpWithEmbeddedClasses(ExpWithEmbeddedClasses exp)
			throws GenyrisException;

	public void visitSimpleSymbol(SimpleSymbol simpleSymbol)
			throws GenyrisException;

	public void visitFullyQualifiedSymbol(URISymbol sym)
			throws GenyrisException;
}
