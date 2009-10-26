// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.core.StrinG;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;
import org.genyris.format.Formatter;
import org.genyris.format.IndentedFormatter;
import org.genyris.io.ConvertEofInStream;
import org.genyris.io.InStream;
import org.genyris.io.IndentStream;
import org.genyris.io.LexException;
import org.genyris.io.NullWriter;
import org.genyris.io.Parser;
import org.genyris.io.ReaderInStream;
import org.genyris.io.StdioInStream;
import org.genyris.io.UngettableInStream;
import org.genyris.load.SourceLoader;

public class ClassicReadEvalPrintLoop {

	// TODO DRY

	private Interpreter _interpreter;

	public static void main(String[] args) {
		try {
			if (args.length == 0) {
				new ClassicReadEvalPrintLoop().run(args);
			} else {
				if (args[0].equals("-eval")) {
					String expression = "";
					for (int i = 1; i < args.length; i++) {
						expression += args[i] + " ";
					}
					expression += "\n\n";
					evalString(expression);
				} else if (args[0].startsWith("-")) {
					usage();
				} else {
					evalFileWithArguments(args[0], args);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void evalFileWithArguments(String filename, String[] args)
			throws IOException {
		Writer output = new PrintWriter(System.out);
		try {
			Interpreter interpreter = new Interpreter();
			interpreter.init(false);
			setArgs(args, interpreter);
			SourceLoader.loadScriptFromFile(interpreter.getGlobalEnv(), interpreter.getSymbolTable(), filename, output);
		} catch (GenyrisException e) {
			output.write("*** Error in file : " + filename + " " + e.getData());
			output.flush();
			System.exit(-1);
		}
	}

	private static void evalString(String script) throws IOException {
		Interpreter interp;
		Writer output = new PrintWriter(System.out);
		Formatter formatter = new IndentedFormatter(output, 2);
		try {
			interp = new Interpreter();
			interp.init(false);
			InStream is = new UngettableInStream(new ConvertEofInStream(
					new IndentStream(new UngettableInStream(new ReaderInStream(
							new StringReader(script))), true)));

			Parser parser = interp.newParser(is);
			setInitialPrefixes(parser);

			Exp expression = parser.read();
			Exp result = interp.evalInGlobalEnvironment(expression);
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
				.println("Usage: genyris [-h] [-eval (expression) args...]  [filename args... ] ");
		System.exit(-1);
	}

	private static void setArgs(String[] args, Interpreter interpreter)
			throws GenyrisException {
		Symbol ARGS = interpreter.intern(Constants.GENYRIS + "system#"
				+ Constants.ARGS);
		Exp argsAlist = makeListOfStrings(interpreter.getSymbolTable().NIL(),
				args, 0);
		interpreter.getGlobalEnv().defineVariable(ARGS, argsAlist);
	}

	public void run(String args[]) throws IOException {
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
			setArgs(args, _interpreter);

			setInitialPrefixes(parser);
			SourceLoader
					.loadScriptFromClasspath(_interpreter.getGlobalEnv(), _interpreter.getSymbolTable(),
							"org/genyris/load/boot/repl.g",
							(Writer) new NullWriter());
			Exp expression = null;
			do {
				try {
					output.write("\n> ");
					output.flush();
					expression = parser.read();
					if (expression.equals(EOF)) {
						formatter.print("Bye..\n");
						break;
					}

					Exp result = _interpreter
							.evalInGlobalEnvironment(expression);

					result.acceptVisitor(formatter);

					output.write(" " + Constants.COMMENTCHAR);
					formatter.printClassNames(result, _interpreter);
					output.flush();
				} catch (LexException e) {
					formatter.print("*** Error: " + e.getMessage() + "\n");
					parser.resetAfterError();
				} catch (GenyrisException e) {
					formatter.print("*** Error");
					formatter.printClassNames(e.getData(), _interpreter);
                    formatter.print(" : "+ e.getData() + ". ");
                    _interpreter.printDebugBackTrace(formatter);
                    _interpreter.resetDebugBackTrace();
					output.flush();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} while (true);
		} catch (GenyrisException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}

	}

	private static void setInitialPrefixes(Parser parser)
			throws GenyrisException {
		parser.addPrefix("u", Constants.PREFIX_UTIL);
		parser.addPrefix("web", Constants.PREFIX_WEB);
		parser.addPrefix("email", Constants.PREFIX_EMAIL);
		parser.addPrefix("g", Constants.PREFIX_SYNTAX);
		parser.addPrefix("sys", Constants.PREFIX_SYSTEM);
		parser.addPrefix("ver", Constants.PREFIX_VERSION);
		parser.addPrefix("types", Constants.PREFIX_TYPES);
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
