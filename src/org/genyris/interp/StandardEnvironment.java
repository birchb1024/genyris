// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp;

import java.util.HashMap;
import java.util.Map;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.NilSymbol;
import org.genyris.core.Symbol;
import org.genyris.exception.GenyrisException;

// TODO Break this into a Root environment and a Standard Env....
public class StandardEnvironment implements Environment {

    Map _frame; // Exp, Exp
    Environment _parent;
    protected SimpleSymbol NIL;
    protected Exp _self, _classes, _superclasses, _classname;
    protected Exp _left, _right, _dynamic;
    private Interpreter _interpreter;


    public StandardEnvironment(NilSymbol nil) {
        _parent = null;
        _frame = new HashMap();
        NIL = nil;
        _interpreter = null;
    }

    public StandardEnvironment(Interpreter interp, NilSymbol nil) {
        _parent = null;
        _frame = new HashMap();
        NIL = nil;
        _interpreter = interp;

        _self = interp.getSymbolTable().internPlainString(Constants.SELF);
        _classes = interp.getSymbolTable().internPlainString(Constants.CLASSES);
        _superclasses = interp.getSymbolTable().internPlainString(Constants.SUPERCLASSES);
        _classname = interp.getSymbolTable().internPlainString(Constants.CLASSNAME);
        _left = interp.getSymbolTable().internPlainString(Constants.LEFT);
        _right = interp.getSymbolTable().internPlainString(Constants.RIGHT);
        _dynamic = interp.getSymbolTable().internPlainString(Constants.DYNAMIC_SYMBOL);
    }

    private void init() throws GenyrisException {
        NIL = _parent.getNil();
        _self = _parent.internString(Constants.SELF);
        _classes = _parent.internString(Constants.CLASSES);
        _superclasses = _parent.internString(Constants.SUPERCLASSES);
        _classname = _parent.internString(Constants.CLASSNAME);
        _left = _parent.internString(Constants.LEFT);
        _right = _parent.internString(Constants.RIGHT);
        _dynamic = _parent.internString(Constants.DYNAMIC_SYMBOL);
    }
    public StandardEnvironment(Environment parent) throws GenyrisException {
        _parent = parent;
        _frame = new HashMap();
        init();
    }
    public StandardEnvironment(Environment parent, Map bindings) throws GenyrisException {
        _parent = parent;
        _frame = bindings;
        init();
    }

    public Object getJavaValue() {
        return "<StandardEnvironment>";
    }

    public Exp lookupVariableValue(Exp symbol) throws UnboundException {
        if( _frame.containsKey(symbol) ) {
            return (Exp)_frame.get(symbol);
        }
        else if(_parent == null) {
            throw new UnboundException("unbound variable: " + symbol.toString());
        }
        else {
            return _parent.lookupVariableValue(symbol);
        }
    }
    public Exp lookupVariableShallow(Exp symbol) throws UnboundException {
        if( _frame.containsKey(symbol) ) {
            return (Exp)_frame.get(symbol);
        } else {
            throw new UnboundException("frame does not contain key: " + symbol.toString());
        }
    }

    public void setVariableValue(Exp symbol, Exp valu) throws UnboundException {
        if(! (symbol instanceof SimpleSymbol) ) {
            throw new UnboundException("cannot set non-symbol: " + symbol.toString());
        }
        if( _frame.containsKey(symbol) ) {
            _frame.put(symbol, valu);
        }
        else if(_parent == null) {
            throw new UnboundException("unbound: " + symbol.toString());
        }
        else {
            _parent.setVariableValue(symbol, valu);
        }
    }

    public void defineVariable(Exp symbol, Exp valu) throws GenyrisException {
        if(! (symbol instanceof Symbol) ) {
            throw new GenyrisException("cannot define non-symbol: " + symbol.toString());
        }
        _frame.put(symbol, valu);
    }



    public String toString() {
        if( _parent != null ) {
            return _parent.toString() + "/" + _frame.toString();
        }
        else {
            return "/" + _frame.toString();
        }
    }
    public Exp lookupInThisClassAndSuperClasses(Exp symbol) throws UnboundException {
        throw new UnboundException("lookupInSuperClasses not implemented for standard environments.");
    }

    public SimpleSymbol getNil() {
        return NIL;
    }

    public Interpreter getInterpreter() {
        if( _parent != null )
            return _parent.getInterpreter();
        else
            return _interpreter;
    }

    public Exp internPlainString(String symbolName) {
        if(_interpreter == null) {
            return _parent.internPlainString(symbolName);
        }
        else {
            return _interpreter.getSymbolTable().internPlainString(symbolName);
        }
    }
    public Exp internString(String symbolName) throws GenyrisException {
        if(_interpreter == null) {
            return _parent.internString(symbolName);
        }
        else {
            return _interpreter.getSymbolTable().internString(symbolName);
        }
    }

    public Exp lookupDynamicVariableValue(Exp symbol) throws UnboundException {
        throw new UnboundException("no dynamic variables in standard environments: " + symbol.toString());
    }

    public Exp getSelf() throws UnboundException {
        throw new UnboundException("no dynamic variable self in standard environments.");
    }

}
