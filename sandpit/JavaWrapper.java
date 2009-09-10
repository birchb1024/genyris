// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
//
// Warning
//  This code is a playful spike. Not for use.
package org.genyris.java;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.ExpWithEmbeddedClasses;
import org.genyris.core.Lcons;
import org.genyris.core.Lobject;
import org.genyris.core.Lstring;
import org.genyris.core.Lsymbol;
import org.genyris.core.Visitor;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;
import org.genyris.interp.Evaluator;
import org.genyris.interp.Interpreter;
import org.genyris.interp.SpecialEnvironment;
import org.genyris.interp.UnboundException;
import org.genyris.interp.builtin.TagFunction;


public class JavaWrapper extends ExpWithEmbeddedClasses implements Environment {

    private Object _theJavaObject;
    private Lsymbol NIL;
    private Environment _parent;
    Exp _self, CLASSES, SUPERCLASSES, CLASSNAME;

    public JavaWrapper(Environment parent, Object thing) throws GenyrisException {
        _parent = parent;
        _theJavaObject = thing;
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
        return _theJavaObject;
    }

    public void acceptVisitor(Visitor guest) {
        guest.visitLobject(this);
    }

    public boolean hasKey(Exp a) {
        return false;
    }

    public void defineVariable(Exp symbol, Exp valu)  throws GenyrisException
    {
        throw new GenyrisException("Cannot define variables on Java Objects.");
    }

    public Exp lookupVariableValue(Exp symbol) throws SecurityException, GenyrisException {
        if( symbol == _self ) {
            return this;
        }
        else if (symbol == _parent.internString(".methods")) {
            Exp result = NIL;
            Method[] methods = _theJavaObject.getClass().getMethods();
            for(int i=0; i<methods.length; i++) {
                try {
                    result = new Lcons(new Lstring(methods[i].getName()), result);
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return result;
        }
        else if (symbol == _parent.internString(".fields")) {
            Exp result = NIL;
            Field[] fields = _theJavaObject.getClass().getFields();
            for(int i=0; i<fields.length; i++) {
                try {
                    result = new Lcons(_parent.internString("." + fields[i].getName()), result);
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return result;
        }
        else if (symbol == CLASSES) {
            Exp result = getClasses(_parent);
            result = new Lcons(_parent.internString("<java " + _theJavaObject.getClass().toString() + ">"), result);
            return result;
        }
        else  {
            Exp result = NIL;
            Method[] methods = _theJavaObject.getClass().getMethods();
            for(int i=0; i<methods.length; i++) {
                try {
                    if( methods[i].getName().equals(symbol.toString().substring(1) )) {
                        return new JavaMethodWrapper(_parent, _theJavaObject, methods[i]);
                    }
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return result;
        }
    }


    public Exp lookupInThisClassAndSuperClasses(Exp symbol) throws UnboundException {
        throw new UnboundException("lookupInSuperClasses not implemented for JavaObjects.");
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
            throw new UnboundException("setVariableValue not implemeted yet for JavaObject");
        }
    }

    public String toString() {
        return _theJavaObject.toString();
    }

    public Exp lookupVariableShallow(Exp symbol) throws GenyrisException {
        if (symbol == CLASSES) {
            return getClasses(_parent);
        }
        else {
            throw new UnboundException("lookupVariableShallow not implemeted yet for JavaObject");
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
                TagFunction.validateObjectInClass(environment, this, klass);
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

    public Exp internString(String symbolName) throws GenyrisException {
        return _parent.internString(symbolName);
    }

}
