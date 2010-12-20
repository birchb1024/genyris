package org.genyris.io.parser;

import org.genyris.core.Internable;
import org.genyris.core.StrinG;
import org.genyris.core.Symbol;
import org.genyris.core.Visitor;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Interpreter;
import org.genyris.io.ConvertEofInStream;
import org.genyris.io.InStream;
import org.genyris.io.IndentStream;
import org.genyris.io.Parser;
import org.genyris.io.StdioInStream;
import org.genyris.io.StringInStream;
import org.genyris.io.UngettableInStream;
import org.genyris.io.readerstream.ReaderStream;

public class IndentedStreamParser extends StreamParser {
	private InStream _input;
	private Parser _parser;

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
        return table.PARENPARSER();
    }
    public String toString() {
        return "<IndentedStreamParser>";
    }


}
