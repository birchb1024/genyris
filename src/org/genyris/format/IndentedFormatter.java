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
import org.genyris.core.NilSymbol;
import org.genyris.core.Pair;
import org.genyris.core.PairWithcolons;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.StrinG;
import org.genyris.core.URISymbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.EagerProcedure;
import org.genyris.interp.LazyProcedure;

public class IndentedFormatter extends AbstractFormatter {

    private final int INDENT_DEPTH;
    private int _consDepth;
    private Formatter _basic;

    public IndentedFormatter(Writer out, int indentDepth) {
        super(out);
        INDENT_DEPTH = indentDepth;
        _consDepth = 0;
        _basic = new BasicFormatter(out);
    }

    private void printSpaces(int level) throws IOException {
        for (int i = 1; i < level; i++)
            _output.write("   ");
    }

    public void printPair(Pair cons) throws GenyrisException, IOException  {
        // TODO - Yuck!
        _consDepth += 1;
        Exp head = cons;
        int countOfRight = 0;
        if (cons instanceof PairWithcolons) {
            printSpaces(_consDepth);
            cons.car().acceptVisitor(_basic);
            _output.write(" " + Constants.CDRCHAR + " ");
            cons.cdr().acceptVisitor(_basic);
            _consDepth -= 1;
            return;
        }
        while ( !(head instanceof NilSymbol)) {
            countOfRight += 1;
            if (head.isPair()) {
                Pair headCons = ((Pair) head);
                if (headCons.car().isPair()) {
                    Pair first = ((Pair) headCons.car());
                    if (countOfRight <= INDENT_DEPTH) {
                        if (countOfRight > 1)
                            _output.write(' ');
                        headCons.car().acceptVisitor(_basic);
                        head = headCons.cdr();
                        continue;
                    }
                    else {
                        _output.write('\n');
                        ;
                        printSpaces(_consDepth + 1);
                        printPair(first);
                    }
                    if (headCons.cdr().isPair()) {
                        Pair rest = (Pair) headCons.cdr();
                        if (!rest.car().isPair()) {
                            _output.write('\n');
                            printSpaces(_consDepth + 1);
                            _output.write('~');
                        }
                    }
                }
                else {
                    if (countOfRight > 1)
                        _output.write(' ');
                    headCons.car().acceptVisitor(this);
                }
                head = headCons.cdr();
            }
            else {
                if (countOfRight > 1)
                    _output.write(' ');
                _output.write(Constants.CDRCHAR + " ");
                head.acceptVisitor(this);
                _consDepth -= 1;
                return;
            }
        }
        _consDepth -= 1;
    }

    public void visitPair(Pair cons) throws GenyrisException {
        try {
            printPair(cons);
        }
        catch (IOException e) {
            throw new GenyrisException(e.getMessage());
        }
    }

    public void visitEagerProc(EagerProcedure proc) throws GenyrisException {
        writeAtom(proc);
    }

    public void visitLazyProc(LazyProcedure proc) throws GenyrisException {
        writeAtom(proc);
    }

    public void visitBignum(Bignum bignum) throws GenyrisException {
        writeAtom(bignum);
    }

    private void writeAtom(Exp exp) throws GenyrisException {
        try {
            if (_consDepth == 0)
                _output.write("~ ");
            exp.acceptVisitor(_basic);
        }
        catch (IOException e) {
            throw new GenyrisException(this.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void visitSimpleSymbol(SimpleSymbol sym) throws GenyrisException {
        writeAtom(sym);
    }

    public void visitFullyQualifiedSymbol(URISymbol sym) throws GenyrisException {
        writeAtom(sym);
    } 
    
    public void visitStrinG(StrinG lst) throws GenyrisException {
        writeAtom(lst);
    }

    public void visitDictionary(Dictionary frame) throws GenyrisException {

        try {
            printPair((Pair) frame.asAlist());
        }
        catch (IOException e) {
            throw new GenyrisException(this.getClass().getName() + ": " + e.getMessage());
        }

    }

    public void visitExpWithEmbeddedClasses(ExpWithEmbeddedClasses exp) throws GenyrisException {
        writeAtom(exp);
    }

	public void print(String message) throws GenyrisException, IOException {
		_output.write(message);
		
	}

}
