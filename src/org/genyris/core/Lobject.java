// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.genyris.interp.Environment;
import org.genyris.interp.Evaluator;
import org.genyris.interp.GenyrisException;
import org.genyris.interp.Interpreter;
import org.genyris.interp.SpecialEnvironment;
import org.genyris.interp.UnboundException;
import org.genyris.interp.builtin.TagFunction;


public class Lobject extends ExpWithEmbeddedClasses implements Environment {
    private Map _dict;
    private Lsymbol NIL;
    private Environment _parent;
    Exp _self, CLASSES, SUPERCLASSES, CLASSNAME;

    public Lobject(Environment parent) {
        _dict = new HashMap();
        _parent = parent;
        init();
    }

    public Lobject(Lsymbol key, Exp value, Environment parent) {
       _dict = new HashMap();
        _dict.put(key, value);
        _parent = parent;
        init();
    }

    private void init() {
        _self = _parent.internString(Constants.SELF);
        CLASSES = _parent.internString(Constants.CLASSES);
        SUPERCLASSES = _parent.internString(Constants.SUPERCLASSES);
        CLASSNAME = _parent.internString(Constants.CLASSNAME);
        NIL = _parent.getNil();
    }

    public Environment getParent() {
        return _parent;
    }

    public Object getJavaValue() {
        return _dict;
    }

    public void acceptVisitor(Visitor guest) {
        guest.visitLobject(this);
    }

    public boolean isSelfEvaluating() {
        return false;
    }

    public boolean hasKey(Exp a) {
        return _dict.containsKey(a);
    }

    public Exp getAlist() {
        try {
            Iterator iter = _dict.keySet().iterator();
            Exp classesPair = new LconsWithcolons(CLASSES, getClasses(_parent));
            Exp result = new Lcons(classesPair, NIL);
            while(iter.hasNext()) {
                Exp key = (Exp) iter.next();
                Exp value = (Exp) _dict.get(key);
                Exp tmp = new LconsWithcolons(key, value);
                result = new Lcons( tmp , result);
            }
            return new Lcons(_parent.internString(Constants.DICT), result);
        }
        catch (UnboundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return NIL;
    }

    public void defineVariable(Exp symbol, Exp valu)  throws GenyrisException
    {
        if(! (symbol instanceof Lsymbol) ) {
            throw new GenyrisException("cannot define non-symbol: " + symbol.toString());
        }
        if (symbol == CLASSES) {
            setClasses(valu, NIL);
            return;
        }
        else {
            _dict.put(symbol, valu);
        }
    }

    public Exp lookupVariableValue(Exp symbol) throws UnboundException {
        if( symbol == _self ) {
            return this;
        }
        else if (symbol == CLASSES) {
            return getClasses(_parent);
        }
        else if( _dict.containsKey(symbol) ) {
            return (Exp)_dict.get(symbol);
        }
        try {
                return lookupInClasses(symbol);
        } catch (UnboundException ignore) {}

        if(_dict.containsKey(SUPERCLASSES)) {
            return lookupInSuperClasses(symbol);
        }
        throw new UnboundException("unbound " + symbol.toString());
    }

    private Exp lookupInClasses(Exp symbol) throws UnboundException {
        Exp classes = getClasses(_parent);
        while( classes != NIL) {
            try {
                Environment klass = (Environment)(classes.car());
                try {
                    return (Exp)klass.lookupInThisClassAndSuperClasses(symbol);
                } catch (UnboundException e) {
                    ;
                } finally {
                    classes = classes.cdr();
                }
            }
            catch (AccessException e) {
                throw new UnboundException("bad classes list in object");
            }
        }
        throw new UnboundException("dict does not contain key: " + symbol.toString());
    }

    public Exp lookupInThisClassAndSuperClasses(Exp symbol) throws UnboundException {
        if( _dict.containsKey(symbol) ) {
            return (Exp)_dict.get(symbol);
        } else {
            return lookupInSuperClasses(symbol);
        }
    }

    private Exp lookupInSuperClasses(Exp symbol) throws UnboundException {
        if( !_dict.containsKey(SUPERCLASSES) ) {
            throw new UnboundException("object has no superclasses");
        }
        Exp superclasses = (Exp)_dict.get(SUPERCLASSES);
        while( superclasses != NIL) {
            try {
                Environment klass = (Environment)(superclasses.car());
                try {
                    return (Exp)klass.lookupInThisClassAndSuperClasses(symbol);
                } catch (UnboundException e) {
                    ;
                } finally {
                    superclasses = superclasses.cdr();
                }
            }
            catch (AccessException e) {
                throw new UnboundException("bad classes list in object");
            }
        }
        throw new UnboundException("dict does not contain key: " + symbol.toString());
    }

    public void setVariableValue(Exp symbol, Exp valu) throws UnboundException {
        if (symbol == CLASSES) {
            try {
                setClasses(valu, NIL);
            }
            catch (AccessException ignore) {
            }
        }
        else {
            if( _dict.containsKey(symbol)) {
                _dict.put(symbol, valu);
            }
            else {
                throw new UnboundException("in object, undefined variable: " + ((Lsymbol)symbol).getPrintName()); // TODO downcast
            }
        }
    }

    public String toString() {
        return "<dict "+ getAlist().toString() +">";
    }

    public Exp lookupVariableShallow(Exp symbol) throws UnboundException {
        if (symbol == CLASSES) {
            return getClasses(_parent);
        }
        else if( _dict.containsKey(symbol) ) {
            return (Exp)_dict.get(symbol);
        } else {
            throw new UnboundException("dict does not contain key: " + symbol.toString());
        }
    }


    public Exp applyFunction(Environment environment, Exp[] arguments) throws GenyrisException {
        if(arguments[0].isNil()) {
            return this;
        }
        Map bindings = new HashMap();
        bindings.put(_self, this);
        SpecialEnvironment newEnv = new SpecialEnvironment(environment, bindings, this);
        if(arguments[0].listp()) {
            return Evaluator.evalSequence(newEnv, arguments[0]);
        } else {
            try {
                Lobject klass = (Lobject) Evaluator.eval(newEnv, arguments[0]);
                TagFunction.validateClassTagging(environment, this, klass);
                this.addClass(klass);
                return this;
            }
            catch (ClassCastException e) {
                throw new GenyrisException("type tag failure: " + arguments[0] + " is not a class");
            }
        }

    }

    public Lsymbol getNil() {
        return NIL;
    }

    public Interpreter getInterpreter() {
        // TODO Auto-generated method stub
        return null;
    }

    public Exp internString(String symbolName) {
        return _parent.internString(symbolName);
    }
    public String getBuiltinClassName() {
        return Constants.OBJECT;
    }

}
