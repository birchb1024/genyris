// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io.parser;

import org.genyris.core.Atom;
import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.AbstractMethod;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;
import org.genyris.io.InStream;
import org.genyris.io.Parser;

public abstract class StreamParser extends Atom {
	protected InStream _input;
    protected Parser _parser;

    public Exp eval(Environment env) throws GenyrisException {
		return this;
	}
    public void close() throws GenyrisException {
        _input.close();
    }

    public static class PrefixMethod extends AbstractParserMethod {
        public PrefixMethod(Interpreter interp) {
            super(interp, "prefix");
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
        	checkArguments(arguments, 2);
        	getSelfParser(env)._parser.addPrefix(_interp, arguments[0].toString(), arguments[1].toString());
        	return NIL;
        }
    }
    public static abstract class AbstractParserMethod extends AbstractMethod {
        public AbstractParserMethod(Interpreter interp, String name) {
            super(interp, name);
        }

        protected StreamParser getSelfParser(Environment env)
                throws GenyrisException {
            getSelf(env);
            if (!(_self instanceof StreamParser)) {
                throw new GenyrisException(
                        "Non-Parser passed to a Parser method.");
            } else {
                return (StreamParser) _self;
            }
        }
    }
    public static class ReadMethod extends AbstractParserMethod {
        public ReadMethod(Interpreter interp) {
            super(interp, "read");
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
            StreamParser self = getSelfParser(env);
            return self._parser.read();
        }
    }

    public static class CloseMethod extends AbstractParserMethod {
        public CloseMethod(Interpreter interp) {
            super(interp, "close");
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
            getSelfParser(env).close();
            return NIL;
        }
    }
    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindMethodInstance(Constants.ABSTRACTPARSER, new StreamParser.ReadMethod(interpreter));
        interpreter.bindMethodInstance(Constants.ABSTRACTPARSER, new StreamParser.CloseMethod(interpreter));
        interpreter.bindMethodInstance(Constants.ABSTRACTPARSER, new StreamParser.PrefixMethod(interpreter));
    }


}
