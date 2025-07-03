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
import org.genyris.io.InStream;
import org.genyris.io.IndentStream;
import org.genyris.io.Parser;
import org.genyris.io.ParserSource;
import org.genyris.io.StringInStream;
import org.genyris.io.UngettableInStream;
import org.genyris.io.readerstream.ReaderStream;

public class IndentedStreamParser extends StreamParser {

    private static InStream mkIndentedStream(InStream inStream) {
        return new UngettableInStream(new ConvertEofInStream(new IndentStream(
                new UngettableInStream(inStream), true)));
    }

    public IndentedStreamParser(Interpreter interp, ReaderStream reader, boolean source) {
        _input = mkIndentedStream(reader.getInStream());
        if (source) {
            _parser = new ParserSource(interp.getSymbolTable(), _input);
        } else {
            _parser = new Parser(interp.getSymbolTable(), _input);
        }
    }

    public IndentedStreamParser(Interpreter interp, StrinG script) {
        _input = mkIndentedStream(new StringInStream(script.toString()));
        _parser = interp.newParser(_input);
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
                boolean source = false;
                if (arguments.length == 2) {
                    source = !arguments[1].isNil();
                }
                return new IndentedStreamParser(_interp, (ReaderStream) arguments[0],
                        source);
            } else if (arguments[0] instanceof StrinG) {
                return new IndentedStreamParser(_interp, (StrinG) arguments[0]);
            } else {
                throw new GenyrisException("Bad arg to new method of Parser");
            }
        }
    }

    public static void bindFunctionsAndMethods(Interpreter interpreter)
            throws UnboundException, GenyrisException {
        interpreter.bindMethodInstance(Constants.INDENTEDPARSER,
                new IndentedStreamParser.NewMethod(interpreter));
    }
        @Override
    public int compareTo(Object o) {
        return this == o ? 0 : 1;
    }

}
