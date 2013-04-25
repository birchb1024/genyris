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
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Debugger;
import org.genyris.interp.Environment;
import org.genyris.io.ConvertEofInStream;
import org.genyris.io.InStream;
import org.genyris.io.IndentStream;
import org.genyris.io.Parser;
import org.genyris.io.ReaderInStream;
import org.genyris.io.UngettableInStream;

public class SourceLoader {

	public static Parser parserFactory(String filename, Reader input,
			Internable table, Debugger debugger) throws GenyrisException {
		if (filename.endsWith(".g") || filename.equals("-")) {
			InStream is = new UngettableInStream(new ConvertEofInStream(
					new IndentStream(new UngettableInStream(new ReaderInStream(
							input, filename)), false)));
			return new Parser(table, is, debugger);

		} else if (filename.endsWith(".lsp")) {
			InStream is = new UngettableInStream(new ReaderInStream(input, filename));
			return new Parser(table, is, Constants.LISPDYNACHAR, Constants.LISPCDRCHAR, Constants.LISPCOMMENTCHAR, debugger);
		} else {
			throw new GenyrisException("unknown file suffix in : " + filename);
		}
	}

	public static void execAndClose(Environment env, Internable table, InputStream in,
			String filename, Writer writer, Debugger debugger) throws GenyrisException {
        debugger.nowParsingFile(filename);
		try {
			executeScript(env, filename, table, new InputStreamReader(in), writer, debugger);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				throw new GenyrisException("loadScriptFromInputStream: "
						+ e.getMessage());
			}
		}
	}

	public static Exp loadScriptFromClasspath(Environment env, Internable table,
			String filename, Writer writer, Debugger debugger) throws GenyrisException {
        debugger.nowParsingFile(filename);

		InputStream in = SourceLoader.class.getClassLoader()
				.getResourceAsStream(filename);
		if (in == null) {
			throw new GenyrisException(
					"loadScriptFromInputStream: could not open: " + filename);
		}
		String url = SourceLoader.class.getClassLoader().getResource(filename)
				.toString();
		execAndClose(env, table, in, url, writer, debugger);
		return new StrinG(url);
	}

	public static Exp loadScriptFromFile(Environment env, Internable table, String filename,
			Writer writer, Debugger debugger) throws GenyrisException {
        debugger.nowParsingFile(filename);
		InputStream in;
		try {
			in = new FileInputStream(filename);
		} catch (FileNotFoundException e) {
			throw new GenyrisException("loadScriptFromFile: " + filename + " (No such file or directory)");
		}
		execAndClose(env, table, in, filename, writer, debugger);
		return new StrinG(filename);
	}

	public static Exp executeScript(Environment env, String filename, Internable table,
			Reader reader, Writer output, Debugger debugger) throws GenyrisException {
		Parser parser = parserFactory(filename, reader, table, debugger);
		Exp expression = null;
		Exp result = null;
		do {
			expression = parser.read();
			if (expression.equals(table.EOF())) {
				break;
			}
			result = expression.evalCatchOverFlow(env);
		} while (true);
		return result;
	}

}
