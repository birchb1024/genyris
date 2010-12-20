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
import org.genyris.io.ConvertEofInStream;
import org.genyris.io.IndentStream;
import org.genyris.io.Parser;
import org.genyris.io.StdioInStream;
import org.genyris.io.StringInStream;
import org.genyris.io.UngettableInStream;
import org.genyris.io.readerstream.ReaderStream;

public class IndentedStreamParser extends StreamParser {

	public IndentedStreamParser(Interpreter interp, ReaderStream reader) {
		_input = new UngettableInStream(new ConvertEofInStream(
				new IndentStream(new UngettableInStream(new StdioInStream()),
						true)));
		_parser = interp.newParser(_input);
	}

	public IndentedStreamParser(Interpreter interp, StrinG script) {
		_input = new UngettableInStream(new StringInStream(script.toString()));
		_parser = new Parser(interp.getSymbolTable(), _input); // TODO two ways
																// to do the
																// same thing
	}
	public void acceptVisitor(Visitor guest) throws GenyrisException {
        guest.visitExpWithEmbeddedClasses(this);
    }

    public Symbol getBuiltinClassSymbol(Internable table) {
        return table.INDENTPARSER();
    }
    public String toString() {
        return "<IndentedStreamParser>";
    }

    public static class NewMethod extends AbstractParserMethod {
        public NewMethod(Interpreter interp) {
            super(interp, "new");
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
            if (arguments[0] instanceof ReaderStream) {
            	return new IndentedStreamParser(_interp, (ReaderStream) arguments[0]);
            } else if (arguments[0] instanceof StrinG){
            	return new IndentedStreamParser(_interp, (StrinG)arguments[0]);
            }
            else {
                throw new GenyrisException("Bad arg to new method of Parser");
            }
        }
    }
    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindMethodInstance(Constants.INDENTEDPARSER, new IndentedStreamParser.NewMethod(interpreter));
    }

}
