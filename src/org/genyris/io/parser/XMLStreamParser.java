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
import org.genyris.io.parser.ParserXML;
import org.genyris.io.StringInStream;
import org.genyris.io.UngettableInStream;
import org.genyris.io.readerstream.ReaderStream;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;




public class XMLStreamParser extends StreamParser {
    public XMLStreamParser(Interpreter interp, ReaderStream reader) throws GenyrisException {
        _input = new UngettableInStream(reader.getInStream());
        _parser = new ParserXML(interp.getSymbolTable(), _input); 
    }

    public XMLStreamParser(Interpreter interp, StrinG script) throws GenyrisException {
		_input = new UngettableInStream( new StringInStream(script.toString()));
        _parser =  new ParserXML(interp.getSymbolTable(), _input); 
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
            if (arguments[0] instanceof ReaderStream) {
            	return new XMLStreamParser(_interp, (ReaderStream) arguments[0]);
            } else if (arguments[0] instanceof StrinG){
            	return new XMLStreamParser(_interp, (StrinG)arguments[0]);
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
