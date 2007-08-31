package org.lispin.jlispin.interp;

import java.io.IOException;
import java.io.Writer;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.format.IndentedFormatter;
import org.lispin.jlispin.io.ConvertEofInStream;
import org.lispin.jlispin.io.InStream;
import org.lispin.jlispin.io.IndentStream;
import org.lispin.jlispin.io.Parser;
import org.lispin.jlispin.io.StringInStream;
import org.lispin.jlispin.io.UngettableInStream;

public class Runner {

    public static Exp executeScript(Interpreter interp, StringInStream stream, Writer output) throws LispinException {
        InStream input = new UngettableInStream(new ConvertEofInStream(new IndentStream(new UngettableInStream(stream),
                false)));
        Parser parser = interp.newParser(input);
        IndentedFormatter formatter = new IndentedFormatter(output, 3, interp.getNil());
        Exp expression = null;
        Exp result = null;
        do {
            expression = parser.read();
            if (expression.equals(interp.getSymbolTable().EOF)) {
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
