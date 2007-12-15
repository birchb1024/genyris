// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.load;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Lstring;
import org.genyris.exception.GenyrisException;
import org.genyris.format.Formatter;
import org.genyris.format.IndentedFormatter;
import org.genyris.interp.Interpreter;
import org.genyris.io.ConvertEofInStream;
import org.genyris.io.InStream;
import org.genyris.io.IndentStream;
import org.genyris.io.Parser;
import org.genyris.io.ReaderInStream;
import org.genyris.io.UngettableInStream;

public class SourceLoader {


    public static Parser parserFactory(String filename, Reader input, Interpreter interp) throws GenyrisException {
        if(filename.endsWith(".lin")) {
            InStream is = new UngettableInStream(
                    new ConvertEofInStream(
                            new IndentStream(
                                    new UngettableInStream(
                                            new ReaderInStream(input)),
                    false)));
            return new Parser(interp.getSymbolTable(), is);

        }
        else if(filename.endsWith(".lsp")) {
            InStream is = new UngettableInStream(new ReaderInStream(input));
            return new Parser(interp.getSymbolTable(), is, Constants.LISPCDRCHAR);
        }
        else {
            throw new GenyrisException("unknown file suffix in : " + filename);
        }
    }

    public static Exp loadScriptFromClasspath(Interpreter _interp, String filename, Writer writer) throws GenyrisException {

        InputStream in  = SourceLoader.class.getClassLoader().getResourceAsStream(filename);
        // this use of getResourceAsStream() means paths are relative to this class
        // unless preceded by a '/'
        if( in == null ) {
            throw new GenyrisException("loadScriptFromClasspath: null pointer from getResourceAsStream.");
        }
        String url = SourceLoader.class.getClassLoader().getResource(filename).toString();
        executeScript(filename, _interp, new InputStreamReader(in), writer);
        return new Lstring(url);
    }

    public static Exp executeScript(String filename, Interpreter interp, Reader reader, Writer output) throws GenyrisException {
        Parser parser = parserFactory(filename, reader, interp);
        Formatter formatter = new IndentedFormatter(output, 3, interp);
        Exp expression = null;
        Exp result = null;
        do {
            expression = parser.read();
            if (expression.equals(interp.getSymbolTable().internString(Constants.EOF))) {
                break;
            }
            result = interp.evalInGlobalEnvironment(expression);
            result.acceptVisitor(formatter);
            try {
                output.write('\n');
                output.flush();
            } catch (IOException ignore) {}
        } while (true);
        return result;
    }


}
