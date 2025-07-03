package org.genyris.io.parser;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.StrinG;
import org.genyris.core.Symbol;
import org.genyris.core.Visitor;
import org.genyris.exception.GenyrisException;
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
    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindMethodInstance(Constants.PARENPARSER, new ParenStreamParser.NewMethod(interpreter));
    }

        @Override
    public int compareTo(Object o) {
        return this == o ? 0 : 1;
    }

}
