// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io.file;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.genyris.core.Exp;
import org.genyris.core.StrinG;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.AbstractMethod;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;
import org.genyris.io.readerstream.ReaderStream;
import org.genyris.io.writerstream.WriterStream;

public class Gfile {
    public static Exp open(StrinG filename, Symbol mode) throws GenyrisException {
        if (mode.toString().equals("read")) {
            try {
                return new ReaderStream(new InputStreamReader(new FileInputStream(filename.toString())));
            }
            catch (FileNotFoundException e) {
                throw new GenyrisException(e.getMessage());
            }
        } else if (mode.toString().equals("write")) {
            try {
                return new WriterStream(new OutputStreamWriter(new FileOutputStream(filename.toString())));
            }
            catch (FileNotFoundException e) {
                throw new GenyrisException(e.getMessage());
            }
        }
        return null;
    }
    public static class FileOpenMethod extends AbstractMethod {

        public FileOpenMethod(Interpreter interp) {
            super(interp, "static-open");
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
            if (arguments.length > 1) {
                if (!(arguments[0] instanceof StrinG)) {
                    throw new GenyrisException("Non-string filname passed to File_static-open");
                }
                if (!(arguments[1] instanceof Symbol)) {
                    throw new GenyrisException("Non-symbol mode passed to File_static-open");
                }
                return open((StrinG)arguments[0], (Symbol)arguments[1]);
            } else {
                throw new GenyrisException("Missing argument to File_open");
            }
        }
    }
    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindMethodInstance("File", new Gfile.FileOpenMethod(interpreter));
    }
}
