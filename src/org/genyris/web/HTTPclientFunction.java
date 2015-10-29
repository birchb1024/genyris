package org.genyris.web;

import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        Map responseHeaders = conn.getHeaderFields();
        Exp headerList = NIL;
        Set<Map.Entry<String, List<String>>> entrySet = responseHeaders.entrySet();
        for (Map.Entry<String, List<String>> entry : entrySet) {
            Exp headerName = entry.getKey() == null ? NIL : new StrinG(entry.getKey());
            List<String> headerValues = entry.getValue();
            Exp valueList = NIL;
            for (String value : headerValues) {
                valueList = Pair.cons(new StrinG(value), valueList);
            }
            Exp thisHeader = Pair.cons(headerName, Pair.reverse(valueList, NIL));
            headerList = Pair.cons(thisHeader, headerList);
        }
        return Pair.reverse(headerList, NIL);
    }

    public static void bindFunctionsAndMethods(Interpreter interpreter)
            throws UnboundException, GenyrisException {
        interpreter.bindGlobalProcedureInstance(new HTTPgetFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new HTTPpostFunction(interpreter));
    }

}
