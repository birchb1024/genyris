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
import org.genyris.core.Pair;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;
import org.genyris.format.Formatter;
import org.genyris.format.IndentedFormatter;
import org.genyris.io.ConvertEofInStream;
import org.genyris.io.InStream;
import org.genyris.io.IndentStream;
import org.genyris.io.NullWriter;
import org.genyris.io.Parser;
import org.genyris.io.StdioInStream;
import org.genyris.io.UngettableInStream;
import org.genyris.load.SourceLoader;

public class ClassicReadEvalPrintLoop {

	private Interpreter _interpreter;
	
    public static void main(String[] args) {
    	ClassicReadEvalPrintLoop loop = new ClassicReadEvalPrintLoop();
    	loop.run(args);
    }
    
    public void run(String args[]) {
        try {
            _interpreter = new Interpreter();
            _interpreter.init(false);
            InStream input = new UngettableInStream(new ConvertEofInStream(
                    new IndentStream(
                            new UngettableInStream(new StdioInStream()), true)));
            Parser parser = _interpreter.newParser(input);
            Writer output = new PrintWriter(System.out);
            Formatter formatter = new IndentedFormatter(output, 1);
            Exp EOF = _interpreter.getSymbolTable().EOF();
            Exp ARGS = _interpreter.intern(Constants.GENYRIS + "system#" + Constants.ARGS);
            Exp argsAlist = makeArgList(args);
            _interpreter.getGlobalEnv().defineVariable(ARGS, argsAlist);

            setInitialPrefixes(parser);
            SourceLoader.loadScriptFromClasspath(_interpreter, "org/genyris/load/boot/repl.lin", (Writer)new NullWriter());
            Exp expression = null;
            do {
                try {
                    System.out.print("\n> ");
                    expression = parser.read();
                    if (expression.equals(EOF)) {
                        System.out.println("Bye..");
                        break;
                    }

                    Exp result = _interpreter.evalInGlobalEnvironment(expression);

                    result.acceptVisitor(formatter);

                    output.write(" ;");
                    formatter.printClassNames(result, _interpreter);
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
            e1.printStackTrace();
            System.exit(-1);
        }

    }

	private void setInitialPrefixes(Parser parser) throws GenyrisException {
		parser.addPrefix("u","http://www.genyris.org/lang/utilities#");
		parser.addPrefix("web","http://www.genyris.org/lang/web#");
		parser.addPrefix("g","http://www.genyris.org/lang/syntax#");
		parser.addPrefix("sys","http://www.genyris.org/lang/system#");
		parser.addPrefix("ver", "http://www.genyris.org/lang/version#");
		}

	private Exp makeArgList(String[] args) {
		Exp arglist = this._interpreter.NIL;
		for(int i=args.length-1;i>=0;i--) {
			arglist = new Pair(new StrinG(args[i]), arglist);
		}
		return arglist;
	}

}
