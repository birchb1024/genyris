// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.core.StrinG;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;
import org.genyris.format.Formatter;
import org.genyris.format.IndentedFormatter;
import org.genyris.io.InStream;
import org.genyris.io.JlineStdioInStream;
import org.genyris.load.SourceLoader;

public class ClassicReadEvalPrintLoop {

    // TODO DRY

    private Interpreter _interpreter;

    public static void main(String[] args) {
        int result = 0;
        try {
            if (args.length == 0) {
                result = new ClassicReadEvalPrintLoop().runWithJline(args);
            } else {
                if (args[0].equals("-eval")) {
                    StringBuffer expression = new StringBuffer("");
                    for (int i = 1; i < args.length; i++) {
                        expression.append(args[i]);
                        expression.append(" ");
                    }
                    expression.append("\n\n");
                    evalString(expression.toString());
                    result = 0;
                } else if (args[0].equals("-")) {
                    result = evalFileWithArguments(args[0], args);
                } else if (args[0].startsWith("-")) {
                    usage();
                    result = -1;
                } else {
                    result = evalFileWithArguments(args[0], args);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(result);
    }

    private static int evalFileWithArguments(String filename, String[] args)
            throws IOException {
        Writer output = new PrintWriter(System.out);
        try {
            Interpreter interpreter = new Interpreter();
            interpreter.init(false);
            setArgs(args, interpreter);
            if (filename.equals("-")) {
                SourceLoader.execAndClose(interpreter.getGlobalEnv(),
                        interpreter.getSymbolTable(), System.in, filename,
                        output);
                return 0;
            }
            SourceLoader.loadScriptFromFile(interpreter.getGlobalEnv(),
                    interpreter.getSymbolTable(), filename, output);
            return 0;
        } catch (GenyrisException e) {
            output.write("*** Error in file : " + filename + " " + e.getData());
            output.flush();
            return -1;
        }
    }

    private static void evalString(String script) throws IOException {
        Interpreter interp;
        Writer output = new PrintWriter(System.out);
        Formatter formatter = new IndentedFormatter(output, 2);
        try {
            interp = new Interpreter();
            interp.init(false);
            Exp result = interp.evalStringInGlobalEnvironment(script);
            result.acceptVisitor(formatter);
            output.write(" " + Constants.COMMENTCHAR);
            formatter.printClassNames(result, interp);
            output.write("\n");
            output.flush();
            System.exit(result == interp.NIL ? 0 : 1);

        } catch (GenyrisException e) {
            output.write("*** Error in script: " + e.getData());
            output.flush();
            System.exit(-1);
        }
    }

    private static void usage() {
        System.out
                .println("Usage: genyris [-h] [-eval (expression) args...] [- args...] [filename args... ] ");
        System.exit(-1);
    }

    private static void setArgs(String[] args, Interpreter interpreter)
            throws GenyrisException {
        Symbol ARGS = interpreter.internEscaped(Constants.GENYRIS + "system#"
                + Constants.ARGS );
        Exp argsAlist = makeListOfStrings(interpreter.getSymbolTable().NIL(),
                args, 0);
        interpreter.getGlobalEnv().defineVariable(ARGS, argsAlist);
    }

    private int runWithJline(String[] args) {
        try {
            JlineStdioInStream console = JlineStdioInStream.knew();
            _interpreter = new Interpreter((InStream) console,
                    console.getOutput());
            _interpreter.init(false);
            setArgs(args, _interpreter);
            console.setInterpreter(_interpreter);
            _interpreter
                    .evalStringInGlobalEnvironment("sys:read-eval-print-loop");
            return 0;
        } catch (GenyrisException e1) {
            e1.printStackTrace();
            return -1;
        }
    }

    private static Exp makeListOfStrings(Symbol NIL, String[] args,
            int startFrom) {
        Exp arglist = NIL;
        for (int i = args.length - 1; i >= startFrom; i--) {
            arglist = new Pair(new StrinG(args[i]), arglist);
        }
        return arglist;
    }

}
