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
import org.genyris.core.ExpWithEmbeddedClasses;
import org.genyris.core.Lcons;
import org.genyris.core.LconsWithcolons;
import org.genyris.core.Ldouble;
import org.genyris.core.Linteger;
import org.genyris.core.Lobject;
import org.genyris.core.Lstring;
import org.genyris.core.Symbol;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.EagerProcedure;
import org.genyris.interp.LazyProcedure;
import org.genyris.interp.UnboundException;

public class BasicFormatter extends  AbstractFormatter {


    public BasicFormatter(Writer out) {
        super(out);
    }

    public void visitLobject(Lobject frame) throws GenyrisException {
        Exp standardClassSymbol = frame.internString(Constants.STANDARDCLASS);
        Lobject standardClass;
        try {
            standardClass = (Lobject) frame.getParent().lookupVariableValue(standardClassSymbol);
        }
        catch (UnboundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return;
        }

        if (frame.isTaggedWith(standardClass)) {
            new ClassWrapper(frame).acceptVisitor(this);
            return;
        }
        frame.getAlist().acceptVisitor(this);

    }

    public void visitEagerProc(EagerProcedure proc) {
        try {
            _output.write(proc.toString());
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void visitLazyProc(LazyProcedure proc) {
        try {
            _output.write(proc.toString());
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    void writeCdr(Exp cons)  throws GenyrisException {
        try {
            if (cons.isNil()) {
                return;
            }
            _output.write(" ");
            if (!cons.listp()) {
                _output.write(Constants.CDRCHAR + " "); // cdr_char
                cons.acceptVisitor(this);
                return;
            }
            cons.car().acceptVisitor(this);
            if (cons.cdr().isNil()) {
                return;
            }
            writeCdr(cons.cdr());
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (AccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void visitLcons(Lcons cons)  throws GenyrisException {
        try {
            _output.write("(");
            cons.car().acceptVisitor(this);
            if (cons instanceof LconsWithcolons) {
                _output.write(" : ");
                cons.cdr().acceptVisitor(this);
            }
            else {
                writeCdr(cons.cdr());
            }
            _output.write(")");
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void visitLdouble(Ldouble dub) {
        try {
            _output.write(dub.getJavaValue().toString());
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void visitLinteger(Linteger lint) {
        try {
            _output.write(lint.getJavaValue().toString());
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void visitBignum(Bignum bignum) {
        try {
            _output.write(bignum.getJavaValue().toString());
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void visitLstring(Lstring lst) {
        try {
            _output.write("\"");
            StringBuffer str = new StringBuffer (lst.getJavaValue().toString());
            for(int i=0; i< str.length(); i++) {
                char ch = str.charAt(i);
                if( ch == '\n') { // TODO move this into a table in Lex.
                    _output.write("\\n");
                } else if( ch == '"') {
                    _output.write("\\\"");
                }else if( ch == '\t') {
                    _output.write("\\t");
                } else if( ch == '\r') {
                    _output.write("\\r");
                }
                else 
                    _output.write(ch);
            }
             _output.write("\"");

        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void visitSymbol(Symbol sym) {
        try {
            _output.write(sym.getJavaValue().toString());
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void visitExpWithEmbeddedClasses(ExpWithEmbeddedClasses exp) {
        try {
            _output.write("[Exp: " + exp.getJavaValue().toString() + "]");
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
