// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import java.io.PrintWriter;
import java.io.Writer;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.format.Formatter;
import org.genyris.format.IndentedFormatter;
import org.genyris.io.ConvertEofInStream;
import org.genyris.io.InStream;
import org.genyris.io.IndentStream;
import org.genyris.io.Parser;
import org.genyris.io.StdioInStream;
import org.genyris.io.UngettableInStream;

public class ClassicReadEvalPrintLoop {

    public static void main(String[] args) {
        Interpreter interpreter;
        try {
            interpreter = new Interpreter();
            interpreter.init(false);
            InStream input = new UngettableInStream(new ConvertEofInStream(
                    new IndentStream(
                            new UngettableInStream(new StdioInStream()), true)));
            Parser parser = interpreter.newParser(input);
            Writer output = new PrintWriter(System.out);
            Formatter formatter = new IndentedFormatter(output, 1, interpreter);
            System.out.println("*** Genyris is listening...");
            Exp expression = null;
            do {
                try {
                    System.out.print("\n> ");
                    expression = parser.read();
                    if (expression.equals(interpreter.getSymbolTable().internString(Constants.EOF))) {
                        System.out.println("Bye..");
                        break;
                    }

                    Exp result = interpreter.evalInGlobalEnvironment(expression);

                    result.acceptVisitor(formatter);

                    output.write(" ;");
                    formatter.printClassNames(result, interpreter);
                    output.flush();
                }
                catch (GenyrisException e) {
                    System.out.println("*** Error: " + e.getMessage());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            } while (true);
        }
        catch (GenyrisException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            System.exit(-1);
        }

    }

}
