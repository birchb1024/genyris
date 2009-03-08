// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;
import org.genyris.interp.Evaluator;
import org.genyris.interp.SpecialEnvironment;
import org.genyris.interp.UnboundException;
import org.genyris.interp.builtin.TagFunction;

public class Lobject extends ExpWithEmbeddedClasses implements Environment {
    private Map _dict;

    private SimpleSymbol NIL;

    protected Environment _parent;

    Exp _self, CLASSES, SUPERCLASSES, CLASSNAME, VARS, _dynamic;

    public Lobject() {
        _dict = new TreeMap();
        _parent = null;
    }

    public Lobject(Environment parent) {
        _dict = new TreeMap();
        _parent = parent;
        init(_parent);
    }

    public Lobject(Symbol key, Exp value, Environment parent) {
        _dict = new TreeMap();
        _dict.put(key, value);
        _parent = parent;
        init(_parent);
    }

    protected void init(Environment env) {
        _self = env.internString(Constants.SELF);
        CLASSES = env.internString(Constants.CLASSES);
        SUPERCLASSES = env.internString(Constants.SUPERCLASSES);
        CLASSNAME = env.internString(Constants.CLASSNAME);
        VARS = env.internString(Constants.VARS);
        _dynamic = env.internString(Constants.DYNAMIC_SYMBOL);
        NIL = env.getNil();
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
        return true;
    }

    public boolean hasKey(Exp a) {
        return _dict.containsKey(a);
    }

    public Exp getAlist() {
        Iterator iter = _dict.keySet().iterator();
        Exp result = NIL;
        while (iter.hasNext()) {
            Exp key = (Exp) iter.next();
            Exp value = (Exp) _dict.get(key);
            Exp tmp = new LconsWithcolons(key, value);
            result = new Lcons(tmp, result);
        }
        return new Lcons(_parent.internString(Constants.DICT), result);
    }

    public void defineVariableRaw(Exp exp, Exp valu) throws GenyrisException {

        if (!(exp instanceof Symbol)) {
            throw new GenyrisException("cannot define non-symbol: " + exp.toString());
        }
        Exp sym = exp;
        if (sym == CLASSES) {
            setClasses(valu, NIL);
            return;
        }
        else {
            _dict.put(sym, valu);
            return;
        }
    }

    public void defineVariable(Exp exp, Exp valu) throws GenyrisException {
        if (exp.listp()) {
            if (exp.car() == _dynamic) {
                defineVariableRaw(exp.cdr().car(), valu);
                return;
            }
        }
        throw new GenyrisException("cannot define non-dynamic symbol in object: " + exp.toString());
    }

    public Exp lookupVariableValue(Exp symbol) throws UnboundException {
        if (symbol == _self) {
            return this;
        }
        else if (symbol == CLASSES) {
            return getClasses(_parent);
        }
        else if (symbol == VARS) {
            return getVarsList();
        }
        else if (_dict.containsKey(symbol)) {
            return (Exp) _dict.get(symbol);
        }
        try {
            return lookupInClasses(symbol);
        }
        catch (UnboundException ignore) {
        }

        if (_dict.containsKey(SUPERCLASSES)) {
            return lookupInSuperClasses(symbol);
        }
        throw new UnboundException("unbound " + symbol.toString());
    }

    private Exp getVarsList() {
        Iterator iter = _dict.keySet().iterator();
        Exp result = new Lcons(VARS, NIL);
        while (iter.hasNext()) {
            Exp key = (Exp) iter.next();
            result = new Lcons(key, result);
        }
        return result;
    }

    private Exp lookupInClasses(Exp symbol) throws UnboundException {
        Exp classes = getClasses(_parent);
        while (classes != NIL) {
            try {
                Environment klass = (Environment) (classes.car());
                try {
                    return (Exp) klass.lookupInThisClassAndSuperClasses(symbol);
                }
                catch (UnboundException e) {
                    ;
                }
                finally {
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
        if (_dict.containsKey(symbol)) {
            return (Exp) _dict.get(symbol);
        }
        else {
            return lookupInSuperClasses(symbol);
        }
    }

    private Exp lookupInSuperClasses(Exp symbol) throws UnboundException {
        if (!_dict.containsKey(SUPERCLASSES)) {
            throw new UnboundException("object has no superclasses");
        }
        Exp superclasses = (Exp) _dict.get(SUPERCLASSES);
        while (superclasses != NIL) {
            try {
                Environment klass = (Environment) (superclasses.car());
                try {
                    return (Exp) klass.lookupInThisClassAndSuperClasses(symbol);
                }
                catch (UnboundException e) {
                    ;
                }
                finally {
                    superclasses = superclasses.cdr();
                }
            }
            catch (AccessException e) {
                throw new UnboundException("bad classes list in object");
            }
        }
        throw new UnboundException("dict does not contain key: " + symbol.toString());
    }

    private Symbol realSymbol(Exp dynamicOrReal) throws UnboundException {
        if(dynamicOrReal.listp()) {
            Lcons tmp = (Lcons)dynamicOrReal;
            if(tmp.car() == _dynamic) {
                tmp = (Lcons)tmp.cdr(); // TODO unsafe downcast
                return (Symbol) tmp.car();
            } else {
                throw new UnboundException("Bad dynamic symbol: " + dynamicOrReal.toString());
            }
        } else if(dynamicOrReal instanceof Symbol) {
            return (Symbol) dynamicOrReal;
        }
        else {
            throw new UnboundException("Bad symbol: " + dynamicOrReal.toString());
        }
    }
    public void setVariableValue(Exp symbol, Exp valu) throws UnboundException {
        Symbol sym = realSymbol(symbol);
        if (sym == CLASSES) {
            try {
                setClasses(valu, NIL);
            }
            catch (AccessException ignore) {
            }
        }
        else {
            if (_dict.containsKey(sym)) {
                _dict.put(sym, valu);
            }
            else {
                throw new UnboundException("in object, undefined variable: " + symbol); // TODO downcast
            }
        }
    }

    public String toString() {
        return "<dict " + getAlist().toString() + ">";
    }

    public Exp lookupVariableShallow(Exp symbol) throws UnboundException {
        if (symbol == CLASSES) {
            return getClasses(_parent);
        }
        else if (_dict.containsKey(symbol)) {
            return (Exp) _dict.get(symbol);
        }
        else {
            throw new UnboundException("dict does not contain key: " + symbol.toString());
        }
    }

    public Exp applyFunction(Environment environment, Exp[] arguments) throws GenyrisException {
        if (arguments[0].isNil()) {
            return this;
        }
        Map bindings = new HashMap();
        bindings.put(_self, this);
        SpecialEnvironment newEnv = new SpecialEnvironment(environment, bindings, this);
        if (arguments[0].listp()) {
            return Evaluator.evalSequence(newEnv, arguments[0]);
        }
        else {
            try {
                Lobject klass = (Lobject) Evaluator.eval(newEnv, arguments[0]);
                TagFunction.validateObjectInClass(environment, this, klass);
                return this;
            }
            catch (ClassCastException e) {
                throw new GenyrisException("type tag failure: " + arguments[0] + " is not a class");
            }
        }

    }

    public SimpleSymbol getNil() {
        return NIL;
    }

    public Symbol internString(String symbolName) {
        return _parent.internString(symbolName);
    }

    public String getBuiltinClassName() {
        return Constants.OBJECT;
    }

    public Exp lookupDynamicVariableValue(Exp symbol) throws UnboundException {
        return lookupVariableValue(symbol);
    }

    public Exp getSelf() {
        return this;
    }

}
