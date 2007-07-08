package org.lispin.jlispin.load;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.format.IndentedFormatter;
import org.lispin.jlispin.interp.Interpreter;
import org.lispin.jlispin.interp.LispinException;
import org.lispin.jlispin.io.ConvertEofInStream;
import org.lispin.jlispin.io.InStream;
import org.lispin.jlispin.io.IndentStream;
import org.lispin.jlispin.io.Parser;
import org.lispin.jlispin.io.ReaderInStream;
import org.lispin.jlispin.io.UngettableInStream;

public class SourceLoader {


	public static void bootStrap(Interpreter interp, Writer writer) throws LispinException {

		InputStream in  = SourceLoader.class.getResourceAsStream("boot/init.lin");
		// this use of getResourceAsStream() means paths are relative to this class 
		// unless preceded by a '/'
		executeScript(interp, new InputStreamReader(in), writer);
	}
	
    public static Exp executeScript(Interpreter interp, Reader reader, Writer output) throws LispinException {
        InStream input = new UngettableInStream(new ConvertEofInStream(new IndentStream(new UngettableInStream(new ReaderInStream(reader)),
                false)));
        Parser parser = interp.newParser(input);
        IndentedFormatter formatter = new IndentedFormatter(output, 3);
        Exp expression = null;
        Exp result = null;
        do {
            expression = parser.read();
            if (expression.equals(SymbolTable.EOF)) {
                break;
            }
            result = interp.evalInGlobalEnvironment(expression);
            result.acceptVisitor(formatter);
            try {
                output.flush();
            } catch (IOException ignore) {}
        } while (true);
        return result;
    }


}
