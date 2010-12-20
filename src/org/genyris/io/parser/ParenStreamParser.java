package org.genyris.io.parser;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.StrinG;
import org.genyris.core.Symbol;
import org.genyris.core.Visitor;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.AbstractMethod;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;
import org.genyris.io.Parser;
import org.genyris.io.StringInStream;
import org.genyris.io.UngettableInStream;
import org.genyris.io.readerstream.ReaderStream;

public class ParenStreamParser extends StreamParser {
    public ParenStreamParser(Interpreter interp, ReaderStream reader) {
        _input = new UngettableInStream(reader.getInStream());
        _parser = interp.newParser(_input); //TODO two ways to do the same thing
    }

    public ParenStreamParser(Interpreter interp, StrinG script) {
		_input = new UngettableInStream( new StringInStream(script.toString()));
        _parser =  new Parser(interp.getSymbolTable(), _input); //TODO two ways to do the same thing
	}

	public void acceptVisitor(Visitor guest) throws GenyrisException {
        guest.visitExpWithEmbeddedClasses(this);
    }

    public String toString() {
        return "<ParenStreamParser>";
    }

    public Symbol getBuiltinClassSymbol(Internable table) {
        return table.PARENPARSER();
    }
    public static abstract class AbstractParserMethod extends AbstractMethod {
        public AbstractParserMethod(Interpreter interp, String name) {
            super(interp, name);
        }

        protected StreamParser getSelfParser(Environment env)
                throws GenyrisException {
            getSelf(env);
            if (!(_self instanceof ParenStreamParser)) {
                throw new GenyrisException(
                        "Non-Parser passed to a Parser method.");
            } else {
                return (ParenStreamParser) _self;
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
            if (arguments[0] instanceof ReaderStream) {
            	return new ParenStreamParser(_interp, (ReaderStream) arguments[0]);
            } else if (arguments[0] instanceof StrinG){
            	return new ParenStreamParser(_interp, (StrinG)arguments[0]);
            }
            else {
                throw new GenyrisException("Bad arg to new method of Parser");
            }
        }
    }
    public static class PrefixMethod extends AbstractParserMethod {
        public PrefixMethod(Interpreter interp) {
            super(interp, "prefix");
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
        	checkArguments(arguments, 2);
        	getSelfParser(env)._parser.addPrefix(arguments[0].toString(), arguments[1].toString());
        	return NIL;
        }
    }
    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindMethodInstance(Constants.PARENPARSER, new ParenStreamParser.NewMethod(interpreter));
        interpreter.bindMethodInstance(Constants.PARENPARSER, new ParenStreamParser.ReadMethod(interpreter));
        interpreter.bindMethodInstance(Constants.PARENPARSER, new ParenStreamParser.CloseMethod(interpreter));
        interpreter.bindMethodInstance(Constants.PARENPARSER, new ParenStreamParser.PrefixMethod(interpreter));
    }

}
