// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io.pipe;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.genyris.core.Atom;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.Pair;
import org.genyris.core.StrinG;
import org.genyris.core.Symbol;
import org.genyris.core.Visitor;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.AbstractMethod;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;
import org.genyris.io.readerstream.ReaderStream;
import org.genyris.io.writerstream.WriterStream;

public class Pipe extends Atom {
	String name;
	
	PipedWriter pipeout;
	PipedReader pipein;
	
	static Map sharedPipeTable = new HashMap();
	
    public Pipe(String pipename) throws GenyrisException {
    	this.name = pipename;
    	pipeout = new PipedWriter();
    	try {
			pipein = new PipedReader(pipeout);
		} catch (IOException e) {
			throw new GenyrisException(e.getMessage());
		}
    }
    public static class PipeOpenMethod extends AbstractMethod {

        public PipeOpenMethod(Interpreter interp) {
            super(interp, "open");
        }

        public synchronized Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
        	String name =  ((StrinG)arguments[0]).toString();
        	if( sharedPipeTable.containsKey(name) ) {
        		return (Exp)sharedPipeTable.get(name);
        	} else {
            	Pipe newpipe = new Pipe(name);
            	sharedPipeTable.put(name, newpipe);
            	return newpipe;
        	}
       }
    }
    public static class PipeInputMethod extends AbstractMethod {

        public PipeInputMethod(Interpreter interp) {
            super(interp, "input");
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
        	getSelf(env);
        	Pipe self = (Pipe)_self;
        	return new ReaderStream(self.pipein, self.toString());
        }
    }
    public static class PipeOutputMethod extends AbstractMethod {

        public PipeOutputMethod(Interpreter interp) {
            super(interp, "output");
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
        	getSelf(env);
        	Pipe self = (Pipe)_self;
        	return new WriterStream(self.pipeout);
        }
    }
    public  static class PipeDeleteMethod extends AbstractMethod {

        public PipeDeleteMethod(Interpreter interp) {
            super(interp, "delete");
        }

        public synchronized Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
        	String name =  ((StrinG)arguments[0]).toString();
        	if( sharedPipeTable.containsKey(name) ) {
        		Pipe pipe = (Pipe)sharedPipeTable.get(name);
        		try {
					pipe.pipeout.close();
				} catch (IOException ignore) {
				}
        		sharedPipeTable.remove(name);
        		return TRUE;
        	} else {
        	return NIL;
        	}
        }
    }
    public static class PipeListMethod extends AbstractMethod {

        public PipeListMethod(Interpreter interp) {
            super(interp, "list");
        }

        public synchronized Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
        	Set names = sharedPipeTable.keySet();
        	Iterator iter = names.iterator();
        	Exp result = NIL;
        	while(iter.hasNext()) {
        		result = new Pair(new StrinG((String)iter.next()), result);
        	}
        	return result;
        }
    }
    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindMethodInstance("Pipe", new Pipe.PipeOpenMethod(interpreter));
        interpreter.bindMethodInstance("Pipe", new Pipe.PipeInputMethod(interpreter));
        interpreter.bindMethodInstance("Pipe", new Pipe.PipeOutputMethod(interpreter));
        interpreter.bindMethodInstance("Pipe", new Pipe.PipeDeleteMethod(interpreter));
        interpreter.bindMethodInstance("Pipe", new Pipe.PipeListMethod(interpreter));
    }
	public void acceptVisitor(Visitor guest) throws GenyrisException {
		guest.visitPipe(this);
		
	}
	public Exp eval(Environment env) throws GenyrisException {
		return this;
	}
	public Symbol getBuiltinClassSymbol(Internable table) {
		return table.PIPE();
	}
	public String toString() {
		return "[Pipe: " + name + "]";
	}
	    @Override
    public int compareTo(Object o) {
        return this == o ? 0 : 1;
    }

}
