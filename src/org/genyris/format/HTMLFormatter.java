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
import org.genyris.core.Ldouble;
import org.genyris.core.Linteger;
import org.genyris.core.Lobject;
import org.genyris.core.Lstring;
import org.genyris.core.Lsymbol;
import org.genyris.core.NilSymbol;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.EagerProcedure;
import org.genyris.interp.LazyProcedure;
import org.genyris.interp.UnboundException;

public class HTMLFormatter extends AbstractFormatter {

    public HTMLFormatter(Writer out) {
        super(out);
    }

    private void emit(String s) throws IOException{
        _output.write(HTMLEntityEncode(s));
    }
    public void visitLobject(Lobject frame) {
        Exp standardClassSymbol = frame.internPlainString(Constants.STANDARDCLASS);
        Lobject standardClass;
        try {
            standardClass = (Lobject) frame.getParent().lookupVariableValue(
                    standardClassSymbol);
        } catch (UnboundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return;
        }
        catch (GenyrisException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
            _output
                    .write("<EagerProc: " + proc.getJavaValue().toString()
                            + ">");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void visitLazyProc(LazyProcedure proc) {
        try {
            emit(proc.getJavaValue().toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void visitLcons(Lcons cons) {
        try {
            if (cons.car() instanceof Lsymbol) {
                Lsymbol tag = (Lsymbol) cons.car();
                Exp attributes = new NilSymbol();
                Exp body = new NilSymbol();
                if(cons.cdr() instanceof NilSymbol) {
                    // no attributes or body
                    body = attributes = cons.cdr();
                } else {
                    if(cons.cdr().listp()) {
                        attributes = cons.cdr().car();
                        body = cons.cdr().cdr();

                    } else {
                        ; // skip bad or missing attributes list
                        body = cons.cdr();
                    }
                }
                if(tag.getPrintName().equals("verbatim")) {
                    while(!body.isNil()) {
                        DisplayFormatter formatter = new DisplayFormatter(_output);
                        body.car().acceptVisitor(formatter);
                        body = body.cdr();
                    }
                    return;
                }
                _output.write("<" + tag.getPrintName());
                writeAttributes(attributes);
                if (body instanceof NilSymbol) {
                    _output.write("/>");
                } else {
                    _output.write(">");
                    body.acceptVisitor(this);
                    _output.write("</" + tag.getPrintName() + ">");
                }
            } else {
                cons.car().acceptVisitor(this);
                if ( !(cons.cdr() instanceof NilSymbol)) {
                    cons.cdr().acceptVisitor(this);
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (AccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void writeAttributes(Exp attributes) throws IOException, AccessException {
        if (!(attributes instanceof NilSymbol)) {
            _output.write(" ");
            while ( !(attributes instanceof NilSymbol)) {
                if(!(attributes instanceof Lcons)) {
                    _output.write("*** error bad HTML attribute: ");
                    _output.write(attributes.toString());
                    return;
                }
                if(!(attributes.car() instanceof Lcons)) {
                    _output.write("*** error bad HTML attribute: ");
                    _output.write(attributes.toString());
                    return;
                }
                _output.write(attributes.car().car().toString());
                _output.write("=\"");
                _output.write(attributes.car().cdr().toString());
                _output.write("\"");
                if (!(attributes.cdr() instanceof NilSymbol))
                    _output.write(" ");
                attributes = attributes.cdr();
            }
        }
    }

    public void visitLdouble(Ldouble dub) {
        try {
            _output.write(dub.getJavaValue().toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void visitLinteger(Linteger lint) {
        try {
            _output.write(lint.getJavaValue().toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void visitBignum(Bignum bignum) {
        try {
            _output.write(bignum.getJavaValue().toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void visitLstring(Lstring lst) {
        try {
            emit(lst.getJavaValue().toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void visitLsymbol(Lsymbol lsym) {
        try {
            emit(lsym.getJavaValue().toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String HTMLEntityEncode(String s) {
        StringBuffer buf = new StringBuffer();
        int len = (s == null ? -1 : s.length());

        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (c >= 'a' && c <= 'z'
                || c >= 'A' && c <= 'Z'
                || c >= '0'&& c <= '9'
                || c == ' '
                || c == '.'
                || c == '-'
                || c == ':'
                || c == '+'
                ) {
                buf.append(c);
            } else {
                buf.append("&#" + (int) c + ";");
            }
        }
        return buf.toString();
    }

    public void visitExpWithEmbeddedClasses(ExpWithEmbeddedClasses exp) {
        try {
            emit(exp.getJavaValue().toString());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
