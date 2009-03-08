// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io.writerstream;

import java.io.IOException;
import java.io.Writer;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.ExpWithEmbeddedClasses;
import org.genyris.core.Lstring;
import org.genyris.core.Visitor;
import org.genyris.exception.GenyrisException;
import org.genyris.format.BasicFormatter;
import org.genyris.format.DisplayFormatter;
import org.genyris.format.Formatter;
import org.genyris.format.HTMLFormatter;
import org.genyris.interp.AbstractMethod;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class WriterStream extends ExpWithEmbeddedClasses {
    private Writer _value;

    public Object getJavaValue() {
        return _value;
    }

    public WriterStream(Writer w) {
        _value = w;
    }

    public void acceptVisitor(Visitor guest) throws GenyrisException {
        guest.visitExpWithEmbeddedClasses(this);
    }

    public String toString() {
        return _value.toString();
    }

    public String getBuiltinClassName() {
        return Constants.WRITER;
    }

    public void close() throws GenyrisException {
        try {
            _value.close();
        }
        catch (IOException e) {
            throw new GenyrisException(e.getMessage());
        }
    }

    public Exp format(Lstring formatString, Exp[] args, Environment env) throws GenyrisException {
        StringBuffer format = new StringBuffer(formatString.toString());
        int argCounter = 1;
        try {
            for (int i = 0; i < format.length(); i++) {
                if ((format.charAt(i) == '~') && (i == format.length())) {
                    _value.append('~');
                    break;
                }
                if (format.charAt(i) == '~' && format.charAt(i + 1) == 'a') {
                    // display - TODO DRY
                    i++;
                    if (argCounter > args.length) {
                        break;
                    }
                    Formatter formatter = new DisplayFormatter(_value);
                    args[argCounter++].acceptVisitor(formatter);
                } else if (format.charAt(i) == '~' && format.charAt(i + 1) == 's') {
                    // write - TODO DRY
                    i++;
                    if (argCounter > args.length) {
                        break;
                    }
                    Formatter formatter = new BasicFormatter(_value);
                    args[argCounter++].acceptVisitor(formatter);
                } else if (format.charAt(i) == '~' && format.charAt(i + 1) == 'x') {
                    // write - TODO DRY
                    i++;
                    if (argCounter > args.length) {
                        break;
                    }
                    Formatter formatter = new HTMLFormatter(_value);
                    args[argCounter++].acceptVisitor(formatter);
                } else if (format.charAt(i) == '~' && format.charAt(i + 1) == '%') {
                    i++;
                    _value.append('\n');
                } else if (format.charAt(i) == '~' && format.charAt(i + 1) == '~') {
                    i++;
                    _value.append('~');
                } else {
                    _value.append(format.charAt(i));
                }
            }
        }
        catch (IOException e) {
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
    }
    public static class FormatMethod extends AbstractWriterMethod {
        private Exp STDOUT;

    	public static String getStaticName() {return "format";};
        public FormatMethod(Interpreter interp) {
        	super(interp, getStaticName());
            try {
                STDOUT = interp.lookupGlobalFromString(Constants.STDOUT);
            }
            catch (GenyrisException e) {
                STDOUT = null;
            }
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
            if (arguments.length > 0) {
                if (!(arguments[0] instanceof Lstring)) {
                    throw new GenyrisException("Non string passed to FormatMethod");
                }
                WriterStream self = getSelfWriter(env);
                Exp retval = self.format((Lstring)arguments[0], arguments, env);
                if(self == STDOUT) {
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

}
