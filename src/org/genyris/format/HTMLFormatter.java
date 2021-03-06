// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.format;

import java.io.Writer;

import org.genyris.core.Bignum;
import org.genyris.core.Dictionary;
import org.genyris.core.Exp;
import org.genyris.core.ExpWithEmbeddedClasses;
import org.genyris.core.NilSymbol;
import org.genyris.core.Pair;
import org.genyris.core.StandardClass;
import org.genyris.core.StrinG;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.EagerProcedure;
import org.genyris.interp.LazyProcedure;
import org.genyris.io.readerstream.ReaderStream;

public class HTMLFormatter extends AbstractFormatter {

	public HTMLFormatter(Writer out) {
		super(out);
	}

	private void emit(String s) throws GenyrisException {
		write(HTMLEntityEncode(s));
	}

	public void visitDictionary(Dictionary frame) throws GenyrisException {
		Symbol standardClassSymbol = frame.getSymbolTable().STANDARDCLASS();
		Exp standardClass = frame.getParent().lookupVariableValue(standardClassSymbol);
		if( ! (standardClass instanceof Dictionary)) {
			throw new GenyrisException("Non-Dictionary class: " + standardClass.toString());
		}
		Dictionary scDict = (Dictionary) standardClass;
		if (frame.isTaggedWith(scDict)) {
			if( ! (frame instanceof StandardClass)) {
				throw new GenyrisException("Non-StandardClass: " + frame.toString());
			}
			((StandardClass)frame).acceptVisitor(this);
			return;
		}
		frame.asAlist().acceptVisitor(this);
	}

	public void visitEagerProc(EagerProcedure proc) throws GenyrisException {
		write("<EagerProc: " + proc.toString() + ">");
	}

	public void visitLazyProc(LazyProcedure proc) throws GenyrisException {
		emit(proc.toString());
	}

	public void visitPair(Pair cons) throws GenyrisException {

		if (cons.car() instanceof Symbol) {
			Symbol tag = (Symbol) cons.car();
			Exp attributes = new NilSymbol();
			Exp body = new NilSymbol();
			if (cons.cdr() instanceof NilSymbol) {
				// no attributes or body
				body = attributes = cons.cdr();
			} else {
				if (cons.cdr().isPair()) {
					attributes = cons.cdr().car();
					body = cons.cdr().cdr();

				} else {
					; // skip bad or missing attributes list
					body = cons.cdr();
				}
			}
			if (tag.getPrintName().equals("verbatim")) {
				while (!body.isNil()) {
					DisplayFormatter formatter = new DisplayFormatter(_output);
					body.car().acceptVisitor(formatter);
					body = body.cdr();
				}
				return;
			}
			if (tag.getPrintName().equals("stream")) {
				if (!body.isNil()) {
					if( body.car() instanceof ReaderStream) {
						ReaderStream str = (ReaderStream)body.car();
						str.copy(_output, 25*80);
					} else {
						throw new GenyrisException("non-Reader passed in stream tag.");
					}
				} else {
					throw new GenyrisException("non body in stream tag.");					
				}
				return;
			}
			write("<" + tag.getPrintName());
			writeAttributes(attributes);
			if (body instanceof NilSymbol) {
				write("/>");
			} else {
				write(">");
				body.acceptVisitor(this);
				write("</" + tag.getPrintName() + ">");
			}
		} else {
			Exp head = cons;
			while( !head.isNil() ) {
				head.car().acceptVisitor(this);
				head = head.cdr();
			}
//			cons.car().acceptVisitor(this);
//			if (!(cons.cdr() instanceof NilSymbol)) {
//				cons.cdr().acceptVisitor(this);
//			}
		}
	}

	private void writeAttributes(Exp attributes) throws GenyrisException {
		if (!(attributes instanceof NilSymbol)) {
			write(" ");
			while (!(attributes instanceof NilSymbol)) {
				if (!(attributes instanceof Pair)) {
					write("*** error bad HTML attribute: ");
					write(attributes.toString());
					return;
				}
				if (!(attributes.car() instanceof Pair)) {
					write("*** error bad HTML attribute: ");
					write(attributes.toString());
					return;
				}
				write(attributes.car().car().toString());
				write("=\"");
				write(attributes.car().cdr().toString());
				write("\"");
				if (!(attributes.cdr() instanceof NilSymbol))
					write(" ");
				attributes = attributes.cdr();
			}
		}
	}

	public void visitBignum(Bignum bignum) throws GenyrisException {
		write(bignum.toString());
	}

	public void visitStrinG(StrinG lst) throws GenyrisException {
		emit(lst.toString());
	}

	public static String HTMLEntityEncode(String s) {
		StringBuffer buf = new StringBuffer();
		int len = (s == null ? -1 : s.length());

		for (int i = 0; i < len; i++) {
			char c = s.charAt(i);
			if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0'
					&& c <= '9' || c == ' ' || c == '.' || c == '-' || c == ':'
					|| c == '+') {
				buf.append(c);
			} else {
				buf.append("&#" + (int) c + ";");
			}
		}
		return buf.toString();
	}

	public void visitExpWithEmbeddedClasses(ExpWithEmbeddedClasses exp)
			throws GenyrisException {
		emit(exp.toString());
	}

	public void print(String message) throws GenyrisException {
		emit(message);
	}
}
