package org.genyris.web;

import java.net.URLConnection;

import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;

public abstract class HTTPclientFunction extends ApplicableFunction {

    public HTTPclientFunction(Interpreter interp, String name, boolean eager) {
        super(interp, name, eager);
    }

    public abstract Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations)
            throws GenyrisException;

    protected Exp getResponseHeadersAsList(URLConnection conn) throws GenyrisException {
        //
        //  Go into an HTTPresponse and retrieve headers as a nested list.
        //
        Exp headerList = NIL;
        int headerIndex = 1;
        String headerName;
        while( (headerName = conn.getHeaderFieldKey(headerIndex)) != null ) {
            StrinG headerValue = new StrinG(conn.getHeaderField(headerIndex));
            Exp thisHeader = Pair.cons(new StrinG(headerName), headerValue);
            headerList = Pair.cons(thisHeader, headerList);
            headerIndex += 1;
        }
        return Pair.reverse(headerList, NIL);
    }

    public static void bindFunctionsAndMethods(Interpreter interpreter)
            throws UnboundException, GenyrisException {
        interpreter.bindGlobalProcedureInstance(new HTTPgetFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new HTTPpostFunction(interpreter));
    }

}
