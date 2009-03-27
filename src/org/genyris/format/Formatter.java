package org.genyris.format;

import java.io.IOException;

import org.genyris.core.Bignum;
import org.genyris.core.Dictionary;
import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.StandardClass;
import org.genyris.core.StrinG;
import org.genyris.core.URISymbol;
import org.genyris.core.Visitor;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.DynamicEnvironment;
import org.genyris.interp.EagerProcedure;
import org.genyris.interp.Interpreter;
import org.genyris.interp.LazyProcedure;
import org.genyris.interp.StandardEnvironment;
import org.genyris.interp.UnboundException;

public interface Formatter extends Visitor {

    public abstract void visitDictionary(Dictionary frame) throws GenyrisException;

    public abstract void visitEagerProc(EagerProcedure proc) throws GenyrisException;

    public abstract void visitLazyProc(LazyProcedure proc) throws GenyrisException;

    public abstract void visitPair(Pair cons) throws GenyrisException;

    public abstract void visitBignum(Bignum bignum) throws GenyrisException;

    public abstract void visitStrinG(StrinG lst) throws GenyrisException;

    public abstract void visitSimpleSymbol(SimpleSymbol sym) throws GenyrisException;
    public abstract void visitFullyQualifiedSymbol(URISymbol sym) throws GenyrisException;

    public abstract void visitStandardEnvironment(StandardEnvironment env) throws GenyrisException;

    public abstract void visitDynamicEnvironment(DynamicEnvironment env) throws GenyrisException;

    public abstract void visitStandardClass(StandardClass klass) throws GenyrisException;

    public abstract void printClassNames(Exp item, Interpreter interp)  throws AccessException, IOException, UnboundException, GenyrisException ;

}