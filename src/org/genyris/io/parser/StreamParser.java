// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io.parser;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.ExpWithEmbeddedClasses;
import org.genyris.core.Internable;
import org.genyris.core.Symbol;
import org.genyris.core.Visitor;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.AbstractMethod;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;
import org.genyris.io.InStream;
import org.genyris.io.Parser;
import org.genyris.io.UngettableInStream;
import org.genyris.io.readerstream.ReaderStream;

public class StreamParser extends ExpWithEmbeddedClasses {
    private InStream _input;
    private Parser _parser;

    public StreamParser(Interpreter interp, ReaderStream reader) {
        _input = new UngettableInStream(reader.getInStream());
        _parser = interp.newParser(_input);
    }

    public void acceptVisitor(Visitor guest) throws GenyrisException {
        guest.visitExpWithEmbeddedClasses(this);
    }

    public String toString() {
        return "<StreamParser>";
    }

    public Symbol getBuiltinClassSymbol(Internable table) {
        return table.PARENPARSER();
    }
    public void close() throws GenyrisException {
        _input.close();
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

    public static class NewMethod extends AbstractParserMethod {
        public NewMethod(Interpreter interp) {
            super(interp, "new");
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
            if (!(arguments[0] instanceof ReaderStream)) {
                throw new GenyrisException("Bad arg to new method of Parser");
            } else {
                return new StreamParser(_interp, (ReaderStream) arguments[0]);
            }
        }
    }

    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindMethodInstance(Constants.PARENPARSER, new StreamParser.NewMethod(interpreter));
        interpreter.bindMethodInstance(Constants.PARENPARSER, new StreamParser.ReadMethod(interpreter));
        interpreter.bindMethodInstance(Constants.PARENPARSER, new StreamParser.CloseMethod(interpreter));
    }
	public Exp eval(Environment env) throws GenyrisException {
		return this;
	}

}
