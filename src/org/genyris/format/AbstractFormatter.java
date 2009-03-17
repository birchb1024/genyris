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
import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.genyris.core.Lobject;
import org.genyris.core.Lstring;
import org.genyris.core.NilSymbol;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.URISymbol;
import org.genyris.core.Visitor;
import org.genyris.dl.Triple;
import org.genyris.dl.TripleSet;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.EagerProcedure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.LazyProcedure;
import org.genyris.interp.SpecialEnvironment;
import org.genyris.interp.StandardEnvironment;

public abstract class AbstractFormatter implements Visitor, Formatter {

	protected Writer _output;

	public AbstractFormatter(Writer out) {
		_output = out;
	}

	public abstract void visitLobject(Lobject frame) throws GenyrisException;

	public abstract void visitEagerProc(EagerProcedure proc)
			throws GenyrisException;

	public abstract void visitLazyProc(LazyProcedure proc)
			throws GenyrisException;

	public abstract void visitLcons(Lcons cons) throws GenyrisException;

	public abstract void visitBignum(Bignum bignum) throws GenyrisException;

	public abstract void visitLstring(Lstring lst) throws GenyrisException;

    public void visitSimpleSymbol(SimpleSymbol sym) throws GenyrisException {
        write(sym.toString());
    }

    public void visitFullyQualifiedSymbol(URISymbol sym) throws GenyrisException {
        write(sym.toString());
    } 


	public void visitStandardEnvironment(StandardEnvironment env)
			throws GenyrisException {

		write(env.getJavaValue().toString());
	}

	public void visitSpecialEnvironment(SpecialEnvironment env)
			throws GenyrisException {
		write(env.getJavaValue().toString());
	}

	public void visitClassWrapper(ClassWrapper klass) throws GenyrisException {
		write(klass.toString());
	}

	public void printClassNames(Exp result, Interpreter interp)
			throws GenyrisException {
		Exp klasses = result.getClasses(interp.getGlobalEnv());
		while (!(klasses instanceof NilSymbol)) {
			Environment klass = (Environment) klasses.car();
			write(" "
					+ klass.lookupVariableShallow(interp.getSymbolTable().CLASSNAME()).toString());
			klasses = klasses.cdr();
		}
	}

	private void handleIO(IOException e) throws GenyrisException {
		throw new GenyrisException(this.getClass().getName() + " write: "+ e.getMessage());
	}
	protected void write(char ch) throws GenyrisException {
		try {
			_output.write(ch);
		} catch (IOException e) {
			handleIO(e);
		}
	}

	public void write(String str) throws GenyrisException {
		try {
			_output.write(str);
		} catch (IOException e) {
			handleIO(e);
		}
	}
    public void visitTriple(Triple triple) throws GenyrisException {
        Formatter basic = new BasicFormatter(_output);
        write("(triple ");
        triple.subject.acceptVisitor(basic);
        write(" ");
        triple.predicate.acceptVisitor(basic);
        write(" ");
        triple.object.acceptVisitor(basic);
        write(")");
    }
    public void visitTripleSet(TripleSet ts) throws GenyrisException {
        write(ts.toString());       
    }

}
