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
import org.genyris.io.StringInStream;
import org.genyris.io.UngettableInStream;
import org.genyris.io.readerstream.ReaderStream;

public class XMLStreamParser extends StreamParser {
    private final boolean _optionQname;

    public XMLStreamParser(Interpreter interp, ReaderStream reader, boolean optionQname) throws GenyrisException {
        _optionQname = optionQname;
        _input = new UngettableInStream(reader.getInStream());
        _parser = new ParserXML(interp.getSymbolTable(), _input, _optionQname);
    }

    public XMLStreamParser(Interpreter interp, StrinG script, boolean optionQname) throws GenyrisException {
        _optionQname = optionQname;
        _input = new UngettableInStream( new StringInStream(script.toString()));
        _parser =  new ParserXML(interp.getSymbolTable(), _input, _optionQname);
	}

	public void acceptVisitor(Visitor guest) throws GenyrisException {
        guest.visitExpWithEmbeddedClasses(this);
    }

    public String toString() {
        return "<XMLStreamParser>";
    }

    public Symbol getBuiltinClassSymbol(Internable table) {
        return table.XMLPARSER();
    }

    public static class NewMethod extends AbstractParserMethod {
        public NewMethod(Interpreter interp) {
            super(interp, "new");
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
            boolean optionQname = true;
            if (arguments.length == 2) {
                if ( arguments[0].isNil() ) {
                    optionQname = false;
                }
            }
            if (arguments[0] instanceof ReaderStream) {
            	return new XMLStreamParser(_interp, (ReaderStream) arguments[0], optionQname);
            } else if (arguments[0] instanceof StrinG){
            	return new XMLStreamParser(_interp, (StrinG)arguments[0], optionQname);
            }
            else {
                throw new GenyrisException("Bad arg to new method of Parser");
            }
        }
    }
    
    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindMethodInstance(Constants.XMLPARSER, new XMLStreamParser.NewMethod(interpreter));
    }
}
