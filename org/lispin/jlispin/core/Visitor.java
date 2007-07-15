package org.lispin.jlispin.core;

import org.lispin.jlispin.interp.EagerProcedure;
import org.lispin.jlispin.interp.LazyProcedure;

public interface Visitor {
	public void visitLcons(Lcons cons);

	public void visitLdouble(Ldouble dub);

	public void visitLinteger(Linteger lint);

	public void visitLstring(Lstring lst);

	public void visitLsymbol(Lsymbol lsym);

	public void visitLazyProc(LazyProcedure lproc);

	public void visitEagerProc(EagerProcedure eproc);

	public void visitLobject(Lobject frame);

}
