// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.format;

import java.io.Writer;

import org.genyris.classification.ClassWrapper;
import org.genyris.core.Bignum;
import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.ExpWithEmbeddedClasses;
import org.genyris.core.Lcons;
import org.genyris.core.LconsWithcolons;
import org.genyris.core.Lobject;
import org.genyris.core.Lstring;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.EagerProcedure;
import org.genyris.interp.LazyProcedure;

public class BasicFormatter extends  AbstractFormatter {


    public BasicFormatter(Writer out) {
        super(out);
    }

    public void visitLobject(Lobject frame) throws GenyrisException {
        Exp standardClassSymbol = frame.getSymbolTable().STANDARDCLASS();
        Lobject standardClass;
        standardClass = (Lobject) frame.getParent().lookupVariableValue(standardClassSymbol);

        if (frame.isTaggedWith(standardClass)) {
            new ClassWrapper(frame).acceptVisitor(this);
            return;
        }
        frame.getAlist().acceptVisitor(this);

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
            if (!cons.listp()) {
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

    public void visitLcons(Lcons cons)  throws GenyrisException {
        write("(");
		cons.car().acceptVisitor(this);
		if (cons instanceof LconsWithcolons) {
		    write(" : ");
		    cons.cdr().acceptVisitor(this);
		}
		else {
		    writeCdr(cons.cdr());
		}
		write(")");
    }

    public void visitBignum(Bignum bignum) throws GenyrisException {
        write(bignum.getJavaValue().toString());
    }

    public void visitLstring(Lstring lst) throws GenyrisException {
        write("\"");
		StringBuffer str = new StringBuffer (lst.getJavaValue().toString());
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
        write("[Exp: " + exp.getJavaValue().toString() + "]");
    }


}
