// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io.readerstream;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.genyris.core.Atom;
import org.genyris.core.Bignum;
import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.StrinG;
import org.genyris.core.Symbol;
import org.genyris.core.Visitor;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.AbstractMethod;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;
import org.genyris.io.ConvertEofInStream;
import org.genyris.io.InStream;
import org.genyris.io.InStreamEOF;
import org.genyris.io.ReaderInStream;
import org.genyris.io.StringInStream;
import org.genyris.io.UngettableInStream;
import org.genyris.io.writerstream.WriterStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class ReaderStream extends Atom {
    private InStream _input;

    public ReaderStream(InStream reader) {
        _input = reader;
    }

    public ReaderStream(InStreamEOF readerEOF) {
        _input = new ConvertEofInStream(readerEOF);
    }

    public ReaderStream(Reader reader, String filename) {
        _input = new ReaderInStream(reader, filename);
    }

    public ReaderStream(String script) {
        _input = new UngettableInStream( new StringInStream(script));
    }

	public Reader getReader() {
		if (_input instanceof ReaderInStream) {
			return ((ReaderInStream)_input).getReader();
		}
		return null;
	}
	
    public InStream getInStream() {
        return _input;
    }
    public void acceptVisitor(Visitor guest) throws GenyrisException {
        guest.visitExpWithEmbeddedClasses(this);
    }

    public String toString() {
        return _input.toString();
    }

    public void close() throws GenyrisException {
        _input.close();
    }

    public static abstract class AbstractReaderMethod extends AbstractMethod {

        public AbstractReaderMethod(Interpreter interp, String name) {
            super(interp, name);
        }

        protected ReaderStream getSelfReader(Environment env) throws GenyrisException {
            getSelf(env);
            if (!(_self instanceof ReaderStream)) {
                throw new GenyrisException("Non-Reader passed to a Reader method.");
            } else {
                return (ReaderStream)_self;
            }
        }
    }
    public static class ReadMethod extends AbstractReaderMethod {

        public ReadMethod(Interpreter interp) {
            super(interp, "read");
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
            ReaderStream self = getSelfReader(env);
            return new Bignum(self._input.readNext());
        }
    }
    public static class GetLineMethod extends AbstractReaderMethod {

        public GetLineMethod(Interpreter interp) {
            super(interp, "getline");
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
            ReaderStream self = getSelfReader(env);
            StringBuffer buf = new StringBuffer();
            if(!self._input.hasData()) {
            	return env.getSymbolTable().EOF();
            }
            do {
            	int ch = self._input.readNext();
            	if(ch == '\r') continue;
            	if( ch == '\n' ) {
            		break;
            	} else {
            		buf.append((char)ch);
            	}
            }  while(self._input.hasData());
    		return new StrinG(buf.toString());
        }
    }
    public static class ReadAllMethod extends AbstractReaderMethod {

        public ReadAllMethod(Interpreter interp) {
            super(interp, "readAll");
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
            ReaderStream self = getSelfReader(env);
            StringBuffer buf = new StringBuffer();
            if(!self._input.hasData()) {
                return env.getSymbolTable().EOF();
            }
            do {
                int ch = self._input.readNext();
                buf.append((char)ch);
            }  while(self._input.hasData());
            return new StrinG(buf.toString());
        }
    }
    public static class HasDataMethod extends AbstractReaderMethod {

        public HasDataMethod(Interpreter interp) {
            super(interp, "hasData");
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
            ReaderStream self = getSelfReader(env);
            return (self._input.hasData() ? TRUE : NIL);
        }
    }
    public static class CloseMethod extends AbstractReaderMethod {

        public static String getStaticName() {return "close";};
        public CloseMethod(Interpreter interp) {
            super(interp, getStaticName());
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
            getSelfReader(env).close();
            return NIL;
        }
    }
    public static class CopyMethod extends AbstractReaderMethod {

        public static String getStaticName() {return "copy";};
        public CopyMethod(Interpreter interp) {
            super(interp, getStaticName());
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
        	Class[] types = {WriterStream.class};
        	checkArgumentTypes(types,arguments);
        	WriterStream output = (WriterStream)arguments[0];
            ReaderStream r = getSelfReader(env);
            r.copy(output.getWriter());
            return NIL;
        }
    }
    public static class NewMethod extends AbstractReaderMethod {

        public static String getStaticName() {return "new";};
        public NewMethod(Interpreter interp) {
            super(interp, getStaticName());
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
            Class[] types = {StrinG.class};
            checkArgumentTypes(types,arguments);
            StrinG theString = (StrinG)arguments[0];
            ReaderStream result = new ReaderStream(theString.toString());
            return result;
        }
    }
    public static class DigestMethod extends AbstractReaderMethod {

        public static String getStaticName() {return "digest";};
        public DigestMethod(Interpreter interp) {
            super(interp, getStaticName());
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
            Class[] types = {StrinG.class};
            checkArgumentTypes(types,arguments);
            StrinG theDigestType = (StrinG)arguments[0];
            ReaderStream r = getSelfReader(env);
            String md5 = r.digest(theDigestType.toString());
            return new StrinG(md5);
        }
    }
    public Symbol getBuiltinClassSymbol(Internable table) {
        return table.READER();
    }

    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindMethodInstance(Constants.READER, new HasDataMethod(interpreter));
        interpreter.bindMethodInstance(Constants.READER, new ReadMethod(interpreter));
        interpreter.bindMethodInstance(Constants.READER, new CloseMethod(interpreter));
        interpreter.bindMethodInstance(Constants.READER, new GetLineMethod(interpreter));
        interpreter.bindMethodInstance(Constants.READER, new CopyMethod(interpreter));
        interpreter.bindMethodInstance(Constants.READER, new ReadAllMethod(interpreter));
        interpreter.bindMethodInstance(Constants.READER, new NewMethod(interpreter));
        interpreter.bindMethodInstance(Constants.READER, new DigestMethod(interpreter));
    }
	public Exp eval(Environment env) throws GenyrisException {
		return this;
	}

	public void copy(Writer output) throws GenyrisException {
		copy(output, Integer.MAX_VALUE);
	}
	public void copy(Writer output, int flushSize) throws GenyrisException {
		int count = 0;
		while( _input.hasData()) {
			char ch = _input.readNext();
			try {
				output.write(ch);
				count += 1;
				if( count > flushSize) {
					count =0;
					output.flush();
				}
			} catch (IOException e) {
				throw new GenyrisException(e.getMessage());
			}
		}
	}
    public String digest(String digestName) throws GenyrisException {
        // 
        // Compute the digest of the input stream,
        // return the digest in Hex.
        // 
        // Digest Names: MD5, SHA-1, SHA-256
        //
        MessageDigest md;
        try {
            md = MessageDigest.getInstance(digestName);
        } catch (NoSuchAlgorithmException e) {
            throw new GenyrisException(e.getMessage());
        }
        while( _input.hasData()) {
            char ch = _input.readNext();
            char[] charray = {ch};
            String temp = new String(charray); // default encoding
            md.update(temp.getBytes());
        }
        byte[] digest = md.digest();
        StringBuffer sb = new StringBuffer();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}
