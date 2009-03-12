// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.load;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
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

	public static Parser parserFactory(String filename, Reader input,
			Internable table) throws GenyrisException {
		if (filename.endsWith(".lin")) {
			InStream is = new UngettableInStream(new ConvertEofInStream(
					new IndentStream(new UngettableInStream(new ReaderInStream(
							input)), false)));
			return new Parser(table, is);

		} else if (filename.endsWith(".lsp")) {
			InStream is = new UngettableInStream(new ReaderInStream(input));
			return new Parser(table, is, Constants.LISPCDRCHAR);
		} else {
			throw new GenyrisException("unknown file suffix in : " + filename);
		}
	}

	private static void execAndClose(Interpreter _interp, InputStream in,
			String filename, Writer writer) throws GenyrisException {
		try {
			executeScript(filename, _interp, new InputStreamReader(in), writer);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				throw new GenyrisException("loadScriptFromInputStream: "
						+ e.getMessage());
			}
		}
	}

	public static Exp loadScriptFromClasspath(Interpreter _interp,
			String filename, Writer writer) throws GenyrisException {

		InputStream in = SourceLoader.class.getClassLoader()
				.getResourceAsStream(filename);
		if (in == null) {
			throw new GenyrisException(
					"loadScriptFromInputStream: could not open: " + filename);
		}
		String url = SourceLoader.class.getClassLoader().getResource(filename)
				.toString();
		execAndClose(_interp, in, url, writer);
		return new Lstring(url);
	}

	public static Exp loadScriptFromFile(Interpreter _interp, String filename,
			Writer writer) throws GenyrisException {

		InputStream in;
		try {
			in = new FileInputStream(filename);
		} catch (FileNotFoundException e) {
			throw new GenyrisException("loadScriptFromFile: " + e.getMessage());
		}
		execAndClose(_interp, in, filename, writer);
		return new Lstring(filename);
	}

	public static Exp executeScript(String filename, Interpreter interp,
			Reader reader, Writer output) throws GenyrisException {
		Parser parser = parserFactory(filename, reader, interp.getSymbolTable());
		Formatter formatter = new IndentedFormatter(output, 3);
		Exp expression = null;
		Exp result = null;
		do {
			expression = parser.read();
			if (expression.equals(interp.getSymbolTable().EOF())) {
				break;
			}
			result = interp.evalInGlobalEnvironment(expression);
			result.acceptVisitor(formatter);
			try {
				output.write('\n');
				output.flush();
			} catch (IOException ignore) {
			}

		} while (true);
		return result;
	}

}
