// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.format;

import java.io.IOException;
import java.io.Writer;

import org.genyris.core.Bignum;
import org.genyris.core.Dictionary;
import org.genyris.core.DynamicSymbol;
import org.genyris.core.Exp;
import org.genyris.core.NilSymbol;
import org.genyris.core.Pair;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.StandardClass;
import org.genyris.core.StrinG;
import org.genyris.core.URISymbol;
import org.genyris.core.Visitor;
import org.genyris.dl.Triple;
import org.genyris.dl.Graph;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.DynamicEnvironment;
import org.genyris.interp.EagerProcedure;
import org.genyris.interp.Interpreter;
import org.genyris.interp.LazyProcedure;
import org.genyris.interp.StandardEnvironment;
import org.genyris.io.pipe.Pipe;
import org.genyris.java.JavaWrapper;

public abstract class AbstractFormatter implements Visitor, Formatter {

	protected Writer _output;

	public AbstractFormatter(Writer out) {
		_output = out;
	}

	public abstract void visitDictionary(Dictionary frame)
			throws GenyrisException;

	public void visitStandardClass(StandardClass klass) throws GenyrisException {
		write(klass.toString());
	}

	public abstract void visitEagerProc(EagerProcedure proc)
			throws GenyrisException;

	public abstract void visitLazyProc(LazyProcedure proc)
			throws GenyrisException;

	public abstract void visitPair(Pair cons) throws GenyrisException;

	public abstract void visitBignum(Bignum bignum) throws GenyrisException;

	public abstract void visitStrinG(StrinG lst) throws GenyrisException;

	public void visitSimpleSymbol(SimpleSymbol sym) throws GenyrisException {
		write(sym.toString());
	}

	public void visitDynamicSymbol(DynamicSymbol sym) throws GenyrisException {
		write(sym.toString());
	}
	public void visitPipe(Pipe pipe) throws GenyrisException {
		write(pipe.toString());
	}

	public void visitFullyQualifiedSymbol(URISymbol sym)
			throws GenyrisException {
		write(sym.toString());
	}

	public void visitStandardEnvironment(StandardEnvironment env)
			throws GenyrisException {

		write(env.toString());
	}

	public void visitDynamicEnvironment(DynamicEnvironment env)
			throws GenyrisException {
		write(env.toString());
	}

	public void printClassNames(Exp result, Interpreter interp)
			throws GenyrisException {
		Exp klasses = result.getClasses(interp.getGlobalEnv());
		while (!(klasses instanceof NilSymbol)) {
			Dictionary klass = (Dictionary) klasses.car();
			write(" "
					+ klass.lookupVariableShallow(
							interp.getSymbolTable().CLASSNAME()).toString());
			klasses = klasses.cdr();
		}
	}

	private void handleIO(IOException e) throws GenyrisException {
		throw new GenyrisException(this.getClass().getName() + " write: "
				+ e.getMessage());
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

	public void visitGraph(Graph ts) throws GenyrisException {
		write(ts.toString());
	}

	public void visitJavaWrapper(JavaWrapper javaWrapper)
			throws GenyrisException {
		write("[" + javaWrapper.getValue().getClass().getName() + " "
				+ javaWrapper.getValue().toString() + "]");

	}

}
