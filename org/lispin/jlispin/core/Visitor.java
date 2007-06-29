package org.lispin.jlispin.core;

import org.lispin.jlispin.interp.CallableEnvironment;
import org.lispin.jlispin.interp.EagerProcedure;
import org.lispin.jlispin.interp.LazyProcedure;
import org.lispin.jlispin.interp.StandardEnvironment;

public interface Visitor {
	public void visitLcons(Lcons cons);

	public void visitLdouble(Ldouble dub);

	public void visitLinteger(Linteger lint);

	public void visitLstring(Lstring lst);

	public void visitLsymbol(Lsymbol lsym);

	public void visitLazyProc(LazyProcedure lproc);

	public void visitEagerProc(EagerProcedure eproc);

	public void visitDict(Dict frame);

	public void visitStandardEnvironment(StandardEnvironment environment);

	public void visitCallableEnvironment(CallableEnvironment environment);
}
