// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io.csv;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;

import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.AbstractMethod;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;
import org.genyris.io.readerstream.ReaderStream;

import au.com.bytecode.opencsv.CSVReader;

public class CSV {
	
	public static class ReadCSVFileMethod extends AbstractMethod {
		private static Class[] types = { ReaderStream.class };

		public ReadCSVFileMethod(Interpreter interp) {
			super(interp, "read");
		}

		public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
				throws GenyrisException {
			checkMinArguments(arguments, 1);
			checkArgumentTypes(types, arguments);
			CSVReader reader = null;
			Exp retval = NIL;
			try {
				Reader fr = ((ReaderStream)arguments[0]).getReader();
				if (fr == null) {
					throw new GenyrisException("read expects a stream with an underlying Java Reader");					
				}
				reader = new CSVReader(fr);
				String[] nextLine;
				Exp row = NIL;
				while ((nextLine = reader.readNext()) != null) {
					row = NIL;
					for(int i=0; i< nextLine.length;i++) {
						row = new Pair(new StrinG(nextLine[i]),row);
					}
					retval = new Pair(Pair.reverse(row, NIL), retval);
				}
				
			} catch (FileNotFoundException e) {
				throw new GenyrisException(e.getMessage());
			} catch (IOException e) {
				throw new GenyrisException(e.getMessage());
			} finally {
				try {
					reader.close();
				} catch (IOException ignore) {
				}
			}

			return Pair.reverse(retval, NIL);
		}
	}

	public static void bindFunctionsAndMethods(Interpreter interpreter)
			throws UnboundException, GenyrisException {
		interpreter.bindMethodInstance("CSV", new CSV.ReadCSVFileMethod(interpreter));
	}
}
