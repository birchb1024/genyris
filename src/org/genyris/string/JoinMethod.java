// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.string;

import java.io.StringWriter;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;
import org.genyris.format.DisplayFormatter;
import org.genyris.format.Formatter;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class JoinMethod extends AbstractStringMethod {

    public static String getStaticName() {
        return Constants.JOIN;
    };

    public JoinMethod(Interpreter interp) {
        super(interp, getStaticName());
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
            throws GenyrisException {

        StringWriter output = new StringWriter();
        Formatter formatter = new DisplayFormatter(output);
        Class[] types = { Pair.class };
        checkArgumentTypes(types, arguments);
        StrinG theString = getSelfString(env);
        Exp theList = arguments[0];
        while(NIL != theList.cdr()) {
            theList.car().acceptVisitor(formatter);
            theString.acceptVisitor(formatter);
            theList = theList.cdr();
        }
        theList.car().acceptVisitor(formatter);
        return new StrinG(output.toString());
    }
}
