package org.genyris.format;

import java.io.IOException;

import org.genyris.classification.ClassWrapper;
import org.genyris.core.Bignum;
import org.genyris.core.Exp;
import org.genyris.core.URISymbol;
import org.genyris.core.Lcons;
import org.genyris.core.Ldouble;
import org.genyris.core.Linteger;
import org.genyris.core.Lobject;
import org.genyris.core.Lstring;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.Visitor;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.EagerProcedure;
import org.genyris.interp.Interpreter;
import org.genyris.interp.LazyProcedure;
import org.genyris.interp.SpecialEnvironment;
import org.genyris.interp.StandardEnvironment;
import org.genyris.interp.UnboundException;

public interface Formatter extends Visitor {

    public abstract void visitLobject(Lobject frame) throws GenyrisException;

    public abstract void visitEagerProc(EagerProcedure proc) throws GenyrisException;

    public abstract void visitLazyProc(LazyProcedure proc) throws GenyrisException;

    public abstract void visitLcons(Lcons cons) throws GenyrisException;

    public abstract void visitLdouble(Ldouble dub) throws GenyrisException;

    public abstract void visitLinteger(Linteger lint) throws GenyrisException;

    public abstract void visitBignum(Bignum bignum) throws GenyrisException;

    public abstract void visitLstring(Lstring lst) throws GenyrisException;

    public abstract void visitSimpleSymbol(SimpleSymbol sym) throws GenyrisException;
    public abstract void visitFullyQualifiedSymbol(URISymbol sym) throws GenyrisException;

    public abstract void visitStandardEnvironment(StandardEnvironment env) throws GenyrisException;

    public abstract void visitSpecialEnvironment(SpecialEnvironment env) throws GenyrisException;

    public abstract void visitClassWrapper(ClassWrapper klass) throws GenyrisException;

    public abstract void printClassNames(Exp item, Interpreter interp)  throws AccessException, IOException, UnboundException, GenyrisException ;

}