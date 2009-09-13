// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.format;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;

import org.genyris.core.Bignum;
import org.genyris.core.Constants;
import org.genyris.core.Dictionary;
import org.genyris.core.Exp;
import org.genyris.core.ExpWithEmbeddedClasses;
import org.genyris.core.Pair;
import org.genyris.core.PairEquals;
import org.genyris.core.StrinG;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.EagerProcedure;
import org.genyris.interp.LazyProcedure;

public class BasicFormatter extends AbstractFormatter {

	public BasicFormatter(Writer out) {
		super(out);
	}

	public void visitDictionary(Dictionary frame) throws GenyrisException {
		frame.asAlist().acceptVisitor(this);
	}

	public void visitEagerProc(EagerProcedure proc) throws GenyrisException {
		write(proc.toString());
	}

	public void visitLazyProc(LazyProcedure proc) throws GenyrisException {
		write(proc.toString());
	}

	void writeCdr(Exp cons) throws GenyrisException {
		try {
			if (cons.isNil()) {
				return;
			}
			write(" ");
			if (!cons.isPair()) {
				write(Constants.CDRCHAR + " "); // cdr_char
				cons.acceptVisitor(this);
				return;
			}
			cons.car().acceptVisitor(this);
			if (cons.cdr().isNil()) {
				return;
			}
			writeCdr(cons.cdr());
		} catch (AccessException e) {
			throw new GenyrisException(this.getClass().getName() + ": "
					+ e.getMessage());
		}
	}

	public void visitPair(Pair cons) throws GenyrisException {
		write("(");
		cons.car().acceptVisitor(this);
		if (cons instanceof PairEquals) {
			write(" " + Constants.CDRCHAR + " ");
			cons.cdr().acceptVisitor(this);
		} else {
			writeCdr(cons.cdr());
		}
		write(")");
	}

	public void visitBignum(Bignum bignum) throws GenyrisException {
		BigDecimal value = bignum.bigDecimalValue();
		BigDecimal x = value.remainder(new BigDecimal(1));
		int y = x.compareTo(new BigDecimal(0));
		if(value.remainder(new BigDecimal(1)).compareTo(new BigDecimal(0)) > 0) {
			String padded = value.toPlainString();	
			write(trim(padded,'0'));
		} else {
			write(value.toPlainString());
		}
	}

	public void visitStrinG(StrinG lst) throws GenyrisException {
		write(lst.getQuoteChar());
		StringBuffer str = new StringBuffer(lst.toString());
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (ch == '\n') { // TODO move this into a table in Lex.
				write("\\n");
			} else if (ch == lst.getQuoteChar()) {
				write("\\");
				write(lst.getQuoteChar());
			} else if (ch == '\t') {
				write("\\t");
			} else if (ch == '\r') {
				write("\\r");
			} else
				write(ch);
		}
		write(lst.getQuoteChar());
	}

	public void visitExpWithEmbeddedClasses(ExpWithEmbeddedClasses exp)
			throws GenyrisException {
		write("[Exp: " + exp.toString() + "]");
	}

	public void print(String message) throws GenyrisException, IOException {
		write(message);
	}

	static String trim(String n, char ch) {
		//
		// Remove trailing from a string
		//
		if(n.length()==1) return n;
		if(n.charAt(n.length()-1) == ch) {
			return trim(n.substring(0, n.length()-1), ch);
		} else {
			return n;
		}
	}

}
