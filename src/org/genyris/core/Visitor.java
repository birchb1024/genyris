package org.genyris.core;

import org.genyris.classification.ClassWrapper;
import org.genyris.interp.EagerProcedure;
import org.genyris.interp.LazyProcedure;

public interface Visitor {
	public void visitLcons(Lcons cons);

	public void visitLdouble(Ldouble dub);

	public void visitLinteger(Linteger lint);

	public void visitLstring(Lstring lst);

	public void visitLsymbol(Lsymbol lsym);

	public void visitLazyProc(LazyProcedure lproc);

	public void visitEagerProc(EagerProcedure eproc);

	public void visitLobject(Lobject frame);

	public void visitBignum(Bignum bignum);

    public void visitClassWrapper(ClassWrapper klass);
}
