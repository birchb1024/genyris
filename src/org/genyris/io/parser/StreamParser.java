// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io.parser;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.ExpWithEmbeddedClasses;
import org.genyris.core.Lsymbol;
import org.genyris.core.Visitor;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.AbstractMethod;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.io.ConvertEofInStream;
import org.genyris.io.InStream;
import org.genyris.io.IndentStream;
import org.genyris.io.Parser;
import org.genyris.io.UngettableInStream;
import org.genyris.io.readerstream.ReaderStream;

public class StreamParser extends ExpWithEmbeddedClasses {
    private InStream _input;
    private Parser _parser;

    public Object getJavaValue() {
        return _input;
    }

    public StreamParser(Interpreter interp, ReaderStream reader) {
        _input = new UngettableInStream(new ConvertEofInStream(
               new IndentStream(
                        new UngettableInStream(reader.getInStream()), true)));
       _parser = interp.newParser(_input);

    }

    public void acceptVisitor(Visitor guest) {
        guest.visitExpWithEmbeddedClasses(this);
    }

    public String toString() {
        return _input.toString();
    }

    public String getBuiltinClassName() {
        return Constants.INDENTEDPARSER;
    }

    public void close() throws GenyrisException {
        _input.close();
    }

    public static abstract class AbstractParserMethod extends AbstractMethod {

        public AbstractParserMethod(Interpreter interp, Lsymbol name) {
            super(interp, name);
        }

        protected StreamParser getSelfParser(Environment env) throws GenyrisException {
            getSelf(env);
            if (!(_self instanceof StreamParser)) {
                throw new GenyrisException("Non-Parser passed to a Parser method.");
            } else {
                return (StreamParser)_self;
            }
        }
    }
    public static class ReadMethod extends AbstractParserMethod {

        public ReadMethod(Interpreter interp, Lsymbol name) {
            super(interp, name);
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
            StreamParser self = getSelfParser(env);
            return self._parser.read();
            
        }
    }
    public static class CloseMethod extends AbstractParserMethod {

        public CloseMethod(Interpreter interp, Lsymbol name) {
            super(interp, name);
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
            getSelfParser(env).close();
            return NIL;
        }
    }
    public static class NewMethod extends AbstractParserMethod {
    
        public NewMethod(Interpreter interp, Lsymbol name) {
            super(interp, name);
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
            if(!(arguments[0] instanceof ReaderStream) ) {
                throw new GenyrisException("Bad arg to _new method of Parser");
            } else {
                return new StreamParser(_interp, (ReaderStream)arguments[0]);
            }
            
        }
    }

}
