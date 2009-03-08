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
import org.genyris.core.NilSymbol;
import org.genyris.core.Symbol;
import org.genyris.interp.EagerProcedure;
import org.genyris.interp.LazyProcedure;
import org.genyris.interp.UnboundException;

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

    public void printLcons(Lcons cons) throws IOException {
        // TODO - Yuck!
        _consDepth += 1;
        Exp head = cons;
        int countOfRight = 0;
        if (cons instanceof LconsWithcolons) {
            printSpaces(_consDepth);
            cons.car().acceptVisitor(_basic);
            _output.write(" " + Constants.CDRCHAR + " ");
            cons.cdr().acceptVisitor(_basic);
            _consDepth -= 1;
            return;
        }
        while ( !(head instanceof NilSymbol)) {
            countOfRight += 1;
            if (head.listp()) {
                Lcons headCons = ((Lcons) head);
                if (headCons.car().listp()) {
                    Lcons first = ((Lcons) headCons.car());
                    if (countOfRight <= INDENT_DEPTH) {
                        if (countOfRight > 1)
                            _output.write(' ');
//                        else
//                            printSpaces(_consDepth);
                        headCons.car().acceptVisitor(_basic);
                        head = headCons.cdr();
                        continue;
                    }
                    else {
                        _output.write('\n');
                        ;
                        printSpaces(_consDepth + 1);
                        printLcons(first);
                    }
                    if (headCons.cdr().listp()) {
                        Lcons rest = (Lcons) headCons.cdr();
                        if (!rest.car().listp()) {
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

    public void visitLcons(Lcons cons) {
        try {
            printLcons(cons);
        }
        catch (IOException e) {
            // TODO what to do with these exceptions?
        }
    }

    public void visitEagerProc(EagerProcedure proc) {
        writeAtom(proc);
    }

    public void visitLazyProc(LazyProcedure proc) {
        writeAtom(proc);
    }

    public void visitLdouble(Ldouble dub) {
        writeAtom(dub);
    }

    public void visitLinteger(Linteger lint) {
        writeAtom(lint);
    }

    public void visitBignum(Bignum bignum) {
        writeAtom(bignum);
    }

    private void writeAtom(Exp exp) {
        try {
            if (_consDepth == 0)
                _output.write("~ ");
            exp.acceptVisitor(_basic);
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void visitLstring(Lstring lst) {
        writeAtom(lst);
    }

    public void visitSymbol(Symbol sym) {
        writeAtom(sym);
    }

    public void visitLobject(Lobject frame) {
        try {
            Exp standardClassSymbol = frame.getParent().internString(Constants.STANDARDCLASS);
            Lobject standardClass;
            standardClass = (Lobject) frame.getParent().lookupVariableValue(standardClassSymbol);
            if (frame.isTaggedWith(standardClass)) {
                new ClassWrapper(frame).acceptVisitor(this);
                return;
            }
        }
        catch (UnboundException ignore) {
        }

        try {
            printLcons((Lcons) frame.getAlist());
        }
        catch (IOException e) {
            // TODO what to do with these exceptions?
        }

    }

    public void visitExpWithEmbeddedClasses(ExpWithEmbeddedClasses exp) {
        writeAtom(exp);
    }


}
