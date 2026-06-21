// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.format;

import java.io.Writer;

import org.genyris.core.*;
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

	private String abbreviate(Symbol T){
		if (T instanceof PrefixSymbol) {
			return ((PrefixSymbol)T).getAbbreviatedPrintName();
		}
		return T.getPrintName();
	}
	public void visitPair(Pair exp) throws GenyrisException {

		if (exp.car() instanceof Symbol) {
			Symbol tag = (Symbol) exp.car();
			Exp attributes = new NilSymbol();
			Exp body = new NilSymbol();
			if (exp.cdr() instanceof NilSymbol) {
				// no attributes or body
				body = attributes = exp.cdr();
			} else {
				if (exp.cdr().isPair()) {
					attributes = exp.cdr().car();
					body = exp.cdr().cdr();

				} else {
					; // skip bad or missing attributes list
					body = exp.cdr();
				}
			}
			if  (abbreviate(tag).equals("nil") && exp.cdr().isNil()) {
				// skip
				return;
			}
			else if (abbreviate(tag).equals("verbatim")) {
				while (!body.isNil()) {
					DisplayFormatter formatter = new DisplayFormatter(_output);
					body.car().acceptVisitor(formatter);
					body = body.cdr();
				}
				return;
			}
			if (abbreviate(tag).equals("stream")) {
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
			write("<" + abbreviate(tag));
			writeAttributes(attributes);
			if (body instanceof NilSymbol) {
				write("/>");
			} else {
				write(">");
				body.acceptVisitor(this);
				write("</" + abbreviate(tag) + ">");
			}
		} else {
			Exp head = exp;
			while( !head.isNil() ) {
				head.car().acceptVisitor(this);
				head = head.cdr();
			}
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
				Exp attrName = attributes.car().car();
				if(! (attrName instanceof Symbol)) {
					write("*** error bad HTML attribute: ");
					write(attributes.toString());
					return;
				}
				write(abbreviate((Symbol)attrName));
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

	public void visitPrefixSymbol(PrefixSymbol sym)
            throws GenyrisException {
        emit(sym.getAbbreviatedPrintName());
    }

}
