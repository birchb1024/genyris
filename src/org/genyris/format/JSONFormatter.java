// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.format;

import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.genyris.core.*;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.EagerProcedure;
import org.genyris.interp.Environment;
import org.genyris.interp.LazyProcedure;
import org.genyris.io.readerstream.ReaderStream;

public class JSONFormatter extends AbstractFormatter {

    public JSONFormatter(Writer out) {
        super(out);
    }

    private void emit(String s) throws GenyrisException {
        write(s);
    }

    public void visitDynamicSymbol(DynamicSymbol sym) throws GenyrisException {
        emitStringEscaped(sym.getRealSymbol().getPrintName());
    }

    public void visitFullyQualifiedSymbol(URISymbol sym)
            throws GenyrisException {
        emitStringEscaped(sym.getPrintName());
    }

    public void visitFullyQualifiedSymbol(EscapedSymbol sym)
            throws GenyrisException {
        emitStringEscaped(sym.getPrintName());
    }

    public void visitSimpleSymbol(SimpleSymbol sym) throws GenyrisException {
        if ( sym.isNil() ) {
            write("[]");
            return;
        }
        String asString = sym.toString();
        if( asString.equals("true") || asString.equals("false") || asString.equals("null")) {
            write(asString);
        } else {
            emitStringEscaped(asString);
        }
    }

    public void visitDictionary(Dictionary frame) throws GenyrisException {
        Map _dict = frame.getMap();
        Environment _parent = frame.getParent();
        Map keys = new TreeMap(_dict);
        Iterator iter = keys.keySet().iterator();
        // TODO Sort the keyset to get a consistent result for test cases.
        write("{ ");
        Exp result = _parent.getNil();
        while (iter.hasNext()) {
            Exp key = (Exp)iter.next();
            Exp value = (Exp)  _dict.get(key);
            key.acceptVisitor(this);
            write(" : ");
            value.acceptVisitor(this);
            if (iter.hasNext()) {
                write(" , ");
            }
        }
        write(" }");
    }

    public void visitEagerProc(EagerProcedure proc) throws GenyrisException {
        emitStringEscaped(proc.toString());
    }

    public void visitLazyProc(LazyProcedure proc) throws GenyrisException {
        emitStringEscaped(proc.toString());
    }

    public void visitPair(Pair cons) throws GenyrisException {
        write("[ ");
        Exp head = cons;
        while( !head.isNil() ) {
            head.car().acceptVisitor(this);
            head = head.cdr();
            if( !head.isNil() )
                write(" , ");
        }
        write(" ]");
    }


    public void visitBignum(Bignum bignum) throws GenyrisException {
        write(bignum.toString());
    }

    public void visitStrinG(StrinG lst) throws GenyrisException {
        emitStringEscaped(lst.toString());
    }

    private void emitStringEscaped(String lst) throws GenyrisException {
        write("\"");
        StringBuffer str = new StringBuffer(lst);
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '\n') {
                write("\\n");
            } else if (ch == '\\') {
                write("\\\\");
            } else if (ch == '"') {
                write("\\\"");
            } else if (ch == '\t') {
                write("\\t");
            } else if (ch == '\r') {
                write("\\r");
            } else {
                write(ch);
            }
        }
        write("\"");
    }


    public void visitExpWithEmbeddedClasses(ExpWithEmbeddedClasses exp)
            throws GenyrisException {
        emit(exp.toString());
    }

    public void print(String message) throws GenyrisException {
        emit(message);
    }
}
