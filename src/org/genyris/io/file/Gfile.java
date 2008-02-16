// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.ExpWithEmbeddedClasses;
import org.genyris.core.Lstring;
import org.genyris.core.Lsymbol;
import org.genyris.core.Visitor;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.AbstractMethod;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.io.writerstream.WriterStream;

public class Gfile extends ExpWithEmbeddedClasses {
    private File _value;

    public Object getJavaValue() {
        return _value;
    }

    public Gfile(String string) {
        _value = new File(string);
    }

    public void acceptVisitor(Visitor guest) {
        guest.visitExpWithEmbeddedClasses(this);
    }

    public String toString() {
        return _value.toString();
    }

    public String getBuiltinClassName() {
        return Constants.FILE;
    }

    public Exp open(Lsymbol mode) throws GenyrisException {
        if (mode.toString().equals("write")) {
            try {
                return new WriterStream(new OutputStreamWriter(new FileOutputStream(_value)));
            }
            catch (FileNotFoundException e) {
                throw new GenyrisException(e.getMessage());
            }
        }
        return null;
    }
    
    public static abstract class AbstractFileMethod extends AbstractMethod {

        public AbstractFileMethod(Interpreter interp) {
            super(interp);
        }

        protected Gfile getSelfFile(Environment env) throws GenyrisException {
            getSelf(env);
            if (!(_self instanceof Gfile)) {
                throw new GenyrisException("Non-File passed to a File method.");
            } else {
                Gfile theFile = (Gfile) _self;
                return theFile;
            }
        }
    }

    public static class NewFileMethod extends AbstractFileMethod {

        public NewFileMethod(Interpreter interp) {
            super(interp);
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
            if(arguments.length > 0) {
                if(!(arguments[0] instanceof Lstring)) {
                    throw new GenyrisException("Non string passed to NewFileMethod");
                }
                return new Gfile((String)((Lstring)arguments[0]).getJavaValue());
            }
            else {
                throw new GenyrisException("Missing argument to NewFileMethods");           
            }
        }
    }
    public static class FileOpenMethod extends AbstractFileMethod {

        public FileOpenMethod(Interpreter interp) {
            super(interp);
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
            Gfile self = this.getSelfFile(env);
            if(arguments.length > 0) {
                if(!(arguments[0] instanceof Lsymbol)) {
                    throw new GenyrisException("Non-symbol mode passed to File_open");
                }
                return self.open(( Lsymbol)arguments[0]);
            }
            else {
                throw new GenyrisException("Missing argument to File_open");            
            }
        }
    }

}
