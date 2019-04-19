// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.string;

import org.genyris.core.*;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONTokener;

import java.util.Iterator;

public class FromJSONMethod extends AbstractStringMethod {

	public static String getStaticName() {return Constants.FROMJSON;};

	public FromJSONMethod(Interpreter interp) {
        super(interp, getStaticName());
    }

    public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
			throws GenyrisException {

        Class[] types = { };
        checkArgumentTypes(types, arguments);
        String theString = getSelfString(env).toString();
        try {
            JSONTokener lexer = new JSONTokener(theString);
            char ch = lexer.nextClean();
            if ( ch == '{') {
                JSONObject jo = new JSONObject(theString);
                return convertJSON(jo, env);
            }
            else if (ch == '[') {
                JSONArray jo = new JSONArray(theString);
                return convertJSON(jo, env);
            }
            else {
                throw new GenyrisException("Expexting non-atomic JSON - got " + theString);
            }
        }
        catch (JSONException e) {
                throw new GenyrisException(e.getMessage());
            }
        }


    public Exp convertJSON(Object obj, Environment env) throws GenyrisException
    {
        try {
            if( obj instanceof Integer ) {
                return new Bignum((Integer)obj);
            }
            else if( obj instanceof Long ) {
                return new Bignum((Long)obj);
            }
            else if( obj instanceof Float ) {
                return new Bignum((Float)obj);
            }
            else if( obj instanceof Double ) {
                return new Bignum((Double)obj);
            }
            else if( obj instanceof String ) {
                return new StrinG((String)obj);
            }
            else if( obj instanceof Boolean ) {
                return (Boolean)obj ? TRUE : NIL;
            }
            else if (obj == JSONObject.NULL) {
                return NIL;
            }
            else if( obj instanceof JSONObject ) {
                JSONObject jo = (JSONObject)obj;
                if( jo.isEmpty() ) {
                    return NIL;
                }
                Dictionary dict = new Dictionary(env);
                Iterator keys = jo.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    DynamicSymbol key_sym = new DynamicSymbol(env.getSymbolTable().internString(key));
                    Object value = jo.get(key);
                    dict.defineDynamicVariable(key_sym, convertJSON(value, env));
                }
                return dict;
            }
            else if( obj instanceof JSONArray) {
                Exp head = NIL;
                JSONArray ja = (JSONArray)obj;
                for( int index = ja.length()-1; index >= 0 ; index--) {
                    head = Pair.cons(convertJSON(ja.get(index), env), head);
                }
                return head;
            }
            else {
                    throw new GenyrisException("Unknown JSON type" + obj.toString());
            }
        }
        catch (JSONException e) {
            throw new GenyrisException(e.getMessage());
        }
    }
}
