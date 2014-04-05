// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.string;

import org.genyris.core.Constants;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.AbstractMethod;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;

public abstract class AbstractStringMethod extends AbstractMethod {

    public AbstractStringMethod(Interpreter interp, String name) {
        super(interp, name);
    }

    protected StrinG getSelfString(Environment env) throws GenyrisException {
        getSelf(env);
        if (!(_self instanceof StrinG)) {
            throw new GenyrisException("Non-String passed to getSelfString: " + _self.toString());
        } else {
            StrinG theString = (StrinG) _self;
            return theString;
        }
    }
    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindMethodInstance(Constants.STRING, new SplitMethod(interpreter));
        interpreter.bindMethodInstance(Constants.STRING, new ConcatMethod(interpreter));
        interpreter.bindMethodInstance(Constants.STRING, new MatchMethod(interpreter));
        interpreter.bindMethodInstance(Constants.STRING, new RegexMethod(interpreter));
        interpreter.bindMethodInstance(Constants.STRING, new LengthMethod(interpreter));
        interpreter.bindMethodInstance(Constants.STRING, new ToLowerCaseMethod(interpreter));
        interpreter.bindMethodInstance(Constants.STRING, new StringFormatMethod(interpreter));
        interpreter.bindMethodInstance(Constants.STRING, new ReplaceMethod(interpreter));
        interpreter.bindMethodInstance(Constants.STRING, new ToIntsMethod(interpreter));
        interpreter.bindMethodInstance(Constants.STRING, new FromIntsMethod(interpreter));
        interpreter.bindMethodInstance(Constants.STRING, new SliceMethod(interpreter));
    }

}
