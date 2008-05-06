// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.format;

import java.io.IOException;
import java.io.Writer;

import org.genyris.classification.ClassWrapper;
import org.genyris.core.Bignum;
import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.genyris.core.Ldouble;
import org.genyris.core.Linteger;
import org.genyris.core.Lobject;
import org.genyris.core.Lstring;
import org.genyris.core.Lsymbol;
import org.genyris.core.NilSymbol;
import org.genyris.core.Visitor;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.EagerProcedure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.LazyProcedure;
import org.genyris.interp.SpecialEnvironment;
import org.genyris.interp.StandardEnvironment;
import org.genyris.interp.UnboundException;

public abstract class AbstractFormatter implements Visitor, Formatter {

    protected Writer _output;

    public AbstractFormatter(Writer out) {
        _output = out;
    }

    public abstract void visitLobject(Lobject frame);

    public abstract void visitEagerProc(EagerProcedure proc);
    public abstract void visitLazyProc(LazyProcedure proc);
    public abstract void visitLcons(Lcons cons);
    public abstract void visitLdouble(Ldouble dub);
    public abstract void visitLinteger(Linteger lint);
    public abstract void visitBignum(Bignum bignum);
    public abstract void visitLstring(Lstring lst);
    public abstract void visitLsymbol(Lsymbol lsym);

    public void visitStandardEnvironment(StandardEnvironment env) {
        try {
            _output.write(env.getJavaValue().toString());
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void visitSpecialEnvironment(SpecialEnvironment env) {
        try {
            _output.write(env.getJavaValue().toString());
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void visitClassWrapper(ClassWrapper klass) {
        try {
            _output.write(klass.toString());
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void printClassNames(Exp result, Interpreter interp) throws IOException, GenyrisException {
        Exp klasses = result.getClasses(interp.getGlobalEnv());
        while(!(klasses instanceof NilSymbol)){
            Environment klass = (Environment) klasses.car();
            _output.write(" " + klass.lookupVariableShallow(interp.getSymbolTable().internString(Constants.CLASSNAME)).toString());
            klasses = klasses.cdr();
        }
    }


}
