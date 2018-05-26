// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io.writerstream;

import java.io.IOException;
import java.io.Writer;

import org.genyris.core.Atom;
import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.StrinG;
import org.genyris.core.Symbol;
import org.genyris.core.Visitor;
import org.genyris.exception.GenyrisException;
import org.genyris.format.BasicFormatter;
import org.genyris.format.DisplayFormatter;
import org.genyris.format.Formatter;
import org.genyris.format.HTMLFormatter;
import org.genyris.format.JSONFormatter;
import org.genyris.format.UrlFormatter;
import org.genyris.interp.AbstractMethod;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;

public class WriterStream extends Atom {
    private Writer _value;

    public WriterStream(Writer w) {
        _value = w;
    }

    public void acceptVisitor(Visitor guest) throws GenyrisException {
        guest.visitExpWithEmbeddedClasses(this);
    }

    public String toString() {
        return _value.toString();
    }

    public Symbol getBuiltinClassSymbol(Internable table) {
        return table.WRITER();
    }

    public void write(char ch) throws GenyrisException {
        try {
            _value.write(ch);
        }
        catch (IOException e) {
            throw new GenyrisException(e.getMessage());
        }
    }

    public void close() throws GenyrisException {
        try {
            _value.close();
        }
        catch (IOException e) {
            throw new GenyrisException(e.getMessage());
        }
    }

    public Exp format(StrinG formatString, int offset, Exp[] args, Environment env) throws GenyrisException {
        StringBuffer format = new StringBuffer(formatString.toString());
        int argCounter = offset;
        char escape = '%';
        try {
            for (int i = 0; i < format.length(); i++) {
                if ((format.charAt(i) == escape) && (i == format.length()-1)) {
                	throw new GenyrisException("Bad format: " + format);
                }
                if (format.charAt(i) == escape && format.charAt(i + 1) == 'a') {
                    // display - TODO DRY
                    i++;
                    if (argCounter > args.length) {
                        break;
                    }
                    Formatter formatter = new DisplayFormatter(_value);
                    if(argCounter == args.length) {
                        throw new GenyrisException("Bad format: " + format + " too few real arguments.");                    	
                    }
                    args[argCounter++].acceptVisitor(formatter);
                } else if (format.charAt(i) == escape && format.charAt(i + 1) == 's') {
                    // write - TODO DRY
                    i++;
                    if (argCounter > args.length) {
                        break;
                    }
                    Formatter formatter = new BasicFormatter(_value);
                    if(argCounter == args.length) {
                        throw new GenyrisException("Bad format: " + format + " too few real arguments.");                    	
                    }
                    args[argCounter++].acceptVisitor(formatter);
                } else if (format.charAt(i) == escape && format.charAt(i + 1) == 'x') {
                    // write - TODO DRY
                    i++;
                    if (argCounter > args.length) {
                        break;
                    }
                    Formatter formatter = new HTMLFormatter(_value);
                    if(argCounter == args.length) {
                        throw new GenyrisException("Bad format: " + format + " too few real arguments.");                    	
                    }
                    args[argCounter++].acceptVisitor(formatter);
                } else if (format.charAt(i) == escape && format.charAt(i + 1) == 'u') {
                    // write - TODO DRY
                    i++;
                    if (argCounter > args.length) {
                        break;
                    }
                    Formatter formatter = new UrlFormatter(_value);
                    if(argCounter == args.length) {
                        throw new GenyrisException("Bad format: " + format + " too few real arguments.");                    	
                    }
                    args[argCounter++].acceptVisitor(formatter);
                } else if (format.charAt(i) == escape && format.charAt(i + 1) == 'n') {
                    i++;
                    _value.append('\n');
                } else if (format.charAt(i) == escape && format.charAt(i + 1) == 'j') {
                    i++;
                    if (argCounter > args.length) {
                        break;
                    }
                    Formatter formatter = new JSONFormatter(_value);
                    if(argCounter == args.length) {
                        throw new GenyrisException("Bad format: " + format + " too few real arguments.");
                    }
                    args[argCounter++].acceptVisitor(formatter);
                } else if (format.charAt(i) == escape && format.charAt(i + 1) == escape) {
                    i++;
                    _value.append(escape);
                } else {
                    _value.append(format.charAt(i));
                }
            }
            if(argCounter != args.length) {
                throw new GenyrisException("Bad format: " + format + " too many real arguments.");                    	            	
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new GenyrisException("Bad format: " + format + " " + e.getMessage());
        }
        catch (StringIndexOutOfBoundsException e) {
            throw new GenyrisException("Bad format: " + format + " " + e.getMessage());
        }
        catch (IOException e) {
        	if(e.getMessage() == null) {
                throw new GenyrisException("IOException");        		
        	}
            throw new GenyrisException(e.getMessage());
        }
        return env.getNil();
    }
    public static abstract class AbstractWriterMethod extends AbstractMethod {

        public AbstractWriterMethod(Interpreter interp, String name) {
            super(interp, name);
        }

        protected WriterStream getSelfWriter(Environment env) throws GenyrisException {
            getSelf(env);
            if (!(_self instanceof WriterStream)) {
                throw new GenyrisException("Non-Writer passed to a Writer method.");
            } else {
                return (WriterStream)_self;
            }
        }
        public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
            interpreter.bindMethodInstance(Constants.WRITER, new FormatMethod(interpreter));
            interpreter.bindMethodInstance(Constants.WRITER, new CloseMethod(interpreter));
            interpreter.bindMethodInstance(Constants.WRITER, new FlushMethod(interpreter));
        }
    }
    public static class FormatMethod extends AbstractWriterMethod {
        private Exp STDOUT;
        private Exp STDERR;

        public static String getStaticName() {return "format";};
        public FormatMethod(Interpreter interp) {
            super(interp, getStaticName());
            try {
                STDOUT = interp.lookupGlobalFromString(Constants.STDOUT);
                STDERR = interp.lookupGlobalFromString(Constants.STDERR);
            }
            catch (GenyrisException e) {
                STDOUT = null;
                STDERR = null;
            }
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
            if (arguments.length > 0) {
                if (!(arguments[0] instanceof StrinG)) {
                    throw new GenyrisException("Non string passed to FormatMethod");
                }
                WriterStream self = getSelfWriter(env);
                Exp retval = self.format((StrinG)arguments[0], 1, arguments, env);
                if(self == STDOUT | self == STDERR) {
                    try {
                        self._value.flush();
                    }
                    catch (IOException e) {
                        throw new GenyrisException(e.getMessage());
                    }
                }
                return retval;
            } else {
                throw new GenyrisException("Missing argument to FormatMethod");
            }
        }
    }
    public static class CloseMethod extends AbstractWriterMethod {

        public static String getStaticName() {return "close";};
        public CloseMethod(Interpreter interp) {
            super(interp, getStaticName());
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
            getSelfWriter(env).close();
            return NIL;
        }
    }
    public static class FlushMethod extends AbstractWriterMethod {

        public static String getStaticName() {return "flush";};
        public FlushMethod(Interpreter interp) {
            super(interp, getStaticName());
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
                getSelfWriter(env).flush();
                return NIL;
        }
    }
    public Exp flush() throws GenyrisException {
        try {
            _value.flush();
        }
        catch (IOException e) {
            throw new GenyrisException(e.getMessage());
        }
        return null;
    }
	public Exp eval(Environment env) throws GenyrisException {
		return this;
	}

	public Writer getWriter() {
		return _value;
	}

}
