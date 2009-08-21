// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.format;

import java.io.IOException;
import java.io.Writer;

import org.genyris.core.Bignum;
import org.genyris.core.Constants;
import org.genyris.core.Dictionary;
import org.genyris.core.Exp;
import org.genyris.core.ExpWithEmbeddedClasses;
import org.genyris.core.Pair;
import org.genyris.core.PairWithcolons;
import org.genyris.core.StrinG;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.EagerProcedure;
import org.genyris.interp.LazyProcedure;

public class BasicFormatter extends  AbstractFormatter {


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

    void writeCdr(Exp cons)  throws GenyrisException {
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
        }
        catch (AccessException e) {
            throw new GenyrisException(this.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void visitPair(Pair cons)  throws GenyrisException {
        write("(");
		cons.car().acceptVisitor(this);
		if (cons instanceof PairWithcolons) {
		    write(" " + Constants.CDRCHAR + " ");
		    cons.cdr().acceptVisitor(this);
		}
		else {
		    writeCdr(cons.cdr());
		}
		write(")");
    }

    public void visitBignum(Bignum bignum) throws GenyrisException {
        write(bignum.toString());
    }

    public void visitStrinG(StrinG lst) throws GenyrisException {
        write("\"");
		StringBuffer str = new StringBuffer (lst.toString());
		for(int i=0; i< str.length(); i++) {
		    char ch = str.charAt(i);
		    if( ch == '\n') { // TODO move this into a table in Lex.
		        write("\\n");
		    } else if( ch == '"') {
		        write("\\\"");
		    }else if( ch == '\t') {
		        write("\\t");
		    } else if( ch == '\r') {
		        write("\\r");
		    }
		    else 
		        write(ch);
		}
		 write("\"");
    }

    public void visitExpWithEmbeddedClasses(ExpWithEmbeddedClasses exp) throws GenyrisException {
       write("[Exp: " + exp.toString() + "]");
    }

	public void print(String message) throws GenyrisException, IOException {
		write(message);	
	}

}
