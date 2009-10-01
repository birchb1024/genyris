// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.genyris.core.Exp;
import org.genyris.core.Pair;
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
    	private static Class[] types = {StrinG.class, Symbol.class};

        public FileOpenMethod(Interpreter interp) {
            super(interp, "static-open");
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
        	checkMinArguments(arguments, 2);
        	checkArgumentTypes(types, arguments);
            return open((StrinG)arguments[0], (Symbol)arguments[1]);
        }
    }
    public static class FileListDir extends AbstractMethod {
    	private static Class[] types = {StrinG.class};

        public FileListDir(Interpreter interp) {
            super(interp, "static-list-dir");
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
        	Exp retval = NIL;
        	checkMinArguments(arguments, 1);
        	checkArgumentTypes(types, arguments);
        		File dir = new File(arguments[0].toString());
        		String[] children = dir.list();
        		
        		if (children == null) {
        	        throw new GenyrisException("File.static-list-dir: failed on " + dir);
        	        // Either dir does not exist or is not a directory
        	    } else {
        	        for (int i=children.length-1; i>=0; i--) {
        	            retval = new Pair( new StrinG(children[i]), retval);
        	        }
        	    }
            return retval;
        }
    }

    public static class IsDir extends AbstractMethod {
    	private static Class[] types = {StrinG.class};

        public IsDir(Interpreter interp) {
            super(interp, "static-is-dir?");
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
        	checkMinArguments(arguments, 1);
        	checkArgumentTypes(types, arguments);
        	File dir = new File(arguments[0].toString());
        	boolean isDir = dir.isDirectory();

            return (isDir? TRUE: NIL);
        }
    }
    public static class FileAbsPath extends AbstractMethod {
    	private static Class[] types = {StrinG.class};

        public FileAbsPath(Interpreter interp) {
            super(interp, "static-abs-path");
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
        	checkMinArguments(arguments, 1);
        	checkArgumentTypes(types, arguments);
        	File file = new File(arguments[0].toString());
        	
            try {
				return new StrinG(file.getCanonicalPath());
			} catch (IOException e) {
				throw new GenyrisException("static-abs-path: " + e.getMessage());
			}
        }
    }
    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindMethodInstance("File", new Gfile.FileOpenMethod(interpreter));
        interpreter.bindMethodInstance("File", new Gfile.FileListDir(interpreter));
        interpreter.bindMethodInstance("File", new Gfile.FileAbsPath(interpreter));
        interpreter.bindMethodInstance("File", new Gfile.IsDir(interpreter));
    }
}
