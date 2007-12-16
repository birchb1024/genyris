package org.genyris.format;

import java.io.IOException;

import org.genyris.classification.ClassWrapper;
import org.genyris.core.Bignum;
import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.genyris.core.Ldouble;
import org.genyris.core.Linteger;
import org.genyris.core.Lobject;
import org.genyris.core.Lstring;
import org.genyris.core.Lsymbol;
import org.genyris.core.Visitor;
import org.genyris.exception.AccessException;
import org.genyris.interp.EagerProcedure;
import org.genyris.interp.Interpreter;
import org.genyris.interp.LazyProcedure;
import org.genyris.interp.SpecialEnvironment;
import org.genyris.interp.StandardEnvironment;
import org.genyris.interp.UnboundException;

public interface Formatter extends Visitor {

    public abstract void visitLobject(Lobject frame);

    public abstract void visitEagerProc(EagerProcedure proc);

    public abstract void visitLazyProc(LazyProcedure proc);

    public abstract void visitLcons(Lcons cons);

    public abstract void visitLdouble(Ldouble dub);

    public abstract void visitLinteger(Linteger lint);

    public abstract void visitBignum(Bignum bignum);

    public abstract void visitLstring(Lstring lst);

    public abstract void visitLsymbol(Lsymbol lsym);

    public abstract void visitStandardEnvironment(StandardEnvironment env);

    public abstract void visitSpecialEnvironment(SpecialEnvironment env);

    public abstract void visitClassWrapper(ClassWrapper klass);

    public abstract void printClassNames(Exp item, Interpreter interp)  throws AccessException, IOException, UnboundException ;

}