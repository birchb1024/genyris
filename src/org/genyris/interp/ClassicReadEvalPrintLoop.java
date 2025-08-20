// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.genyris.core.*;
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
                    evalString(expression.toString(), "-eval");
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
            interpreter.init(false, getContainingDirectoryPath(filename));
            interpreter.getDebugBackTrace();
            setArgs(args, interpreter);
            try {
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
                output.write("*** Error in file " + e.getMessage() + "\n");
                Exp bt = interpreter.getDebugBackTraceAsList();
                while (bt != interpreter.NIL) {
                    output.write("\t" + bt.car() + "\n");
                    bt = bt.cdr();
                }
                output.flush();
                return -1;
            }
        } catch (GenyrisException e) {
            output.write("*** Error in file " + e.getMessage() + "\n");
            output.flush();
            return -1;
        }
    }

    private static void evalString(String script, String name) throws IOException {
        Interpreter interp;
        Writer output = new PrintWriter(System.out);
        Formatter formatter = new IndentedFormatter(output, 2);
        try {
            interp = new Interpreter();
            interp.init(false, name);
            Exp result = interp.evalStringInGlobalEnvironment(script);
            result.acceptVisitor(formatter);
            output.write(" " + Constants.COMMENTCHAR);
            formatter.printClassNames(result, interp);
            output.write("\n");
            output.flush();
            System.exit(result == interp.NIL ? 0 : 1);

        } catch (GenyrisException e) {
            output.write("*** Error in script: " + e.getMessage());
            output.flush();
            System.exit(-1);
        }
    }

    private static void usage() {
        System.out
                .println("Usage: genyris [-h] [-eval (expression) args...] [- args...] [filename args... ] ");
        System.exit(-1);
    }

    private static void setArgs(String[] args, Interpreter interpreter) throws GenyrisException {
        Symbol argv = interpreter.intern(new PrefixSymbol(Constants.GENYRIS + "system#", Constants.ARGV, "sys"));
        Exp argsAlist = makeListOfStrings(interpreter.getSymbolTable().NIL(), args, 0);
        interpreter.getGlobalEnv().defineVariable(argv, argsAlist);
    }

    public static String getContainingDirectoryPath(String url) throws GenyrisException {
        String filename = url;
        try {
            if(url == "-") {
                return ".";
            }
            if(url.startsWith("file:")) {
                filename = url.substring("file:".length());
                Path scriptPath = Paths.get(filename).toRealPath();
                return scriptPath.getParent().toString();
            }
            if(url.startsWith("jar:")) {
                String[] path =  url.split("/");
                String[] parent = Arrays.copyOfRange(path, 0, path.length);
                String result = String.join("/", Arrays.asList(parent));
                return result;
            }
            Path scriptPath = Paths.get(filename).toRealPath();
            return scriptPath.getParent().toString();
        } catch (IOException e) {
            throw new GenyrisException(e);
        }

    }

    private int runWithJline(String[] args) {
        try {
            JlineStdioInStream console = JlineStdioInStream.knew();
            _interpreter = new Interpreter((InStream) console,
                    console.getOutput());
            _interpreter.init(false, ".");
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
