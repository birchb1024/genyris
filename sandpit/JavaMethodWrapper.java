// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
//
// Warning
//  This code is a playful spike. Not for use.

package org.genyris.java;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.ExpWithEmbeddedClasses;
import org.genyris.core.Lcons;
import org.genyris.core.Lsymbol;
import org.genyris.core.Visitor;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;


public class JavaMethodWrapper extends ExpWithEmbeddedClasses implements Environment {
    static private Map _modifiers;

    private Object _theJavaObject;
    private Method _theMethod;
    private Lsymbol NIL;
    private Exp CLASSES;
    private Environment _parent;
    Exp _self;

    public JavaMethodWrapper(Environment parent, Object thing, Method method) throws GenyrisException {
        if( _modifiers == null ) {
            _modifiers = new HashMap();
            _modifiers.put( new Integer(Modifier.ABSTRACT), "abstract");
            _modifiers.put( new Integer(Modifier.FINAL), "final");
            _modifiers.put( new Integer(Modifier.INTERFACE), "interface");
            _modifiers.put( new Integer(Modifier.NATIVE), "native");
            _modifiers.put( new Integer(Modifier.PRIVATE), "private");
            _modifiers.put( new Integer(Modifier.PROTECTED), "protected");
            _modifiers.put( new Integer(Modifier.PUBLIC), "public");
            _modifiers.put( new Integer(Modifier.STATIC), "static");
            _modifiers.put( new Integer(Modifier.STRICT), "strict");
            _modifiers.put( new Integer(Modifier.SYNCHRONIZED), "synchronized");
            _modifiers.put( new Integer(Modifier.TRANSIENT), "transient");
            _modifiers.put( new Integer(Modifier.VOLATILE), "volatile");
        }

        _parent = parent;
        _theJavaObject = thing;
        _theMethod = method;
        _self = _parent.internString(Constants.SELF);
        NIL = _parent.getNil();
        CLASSES = _parent.internString(Constants.CLASSES);
    }

//    private static String ModifiersToString(int mods) {
//        String retval = "";
//        for( Iterator iter = _modifiers.keySet().iterator(); iter.hasNext(); ) {
//            int key = ((Integer)iter.next()).intValue();
//            if( (mods & key) != 0 ) {
//                retval = retval + _modifiers.get(new Integer(key)) + " ";
//            }
//        }
//        return retval;
//    }

    public Environment getParent() {
        return _parent;
    }

    public Object getJavaValue() {
        return _theJavaObject;
    }

    public void acceptVisitor(Visitor guest) {
///        guest.visitLobject(this);
    }

    public boolean hasKey(Exp a) {
        return false;
    }

    public void defineVariable(Exp symbol, Exp valu)  throws GenyrisException
    {
        throw new GenyrisException("Cannot define variables on Java Methods.");
    }

    public Exp lookupVariableValue(Exp symbol) throws GenyrisException {
        if( symbol == _self ) {
            return this;
        }
        else if (symbol == CLASSES) {
            Exp result = getClasses(_parent);
            result = new Lcons(_parent.internString("<java " + _theMethod.toString() + ">"), result);
            return result;
        }
        try {
                return lookupInClasses(symbol);
        } catch (UnboundException ignore) {}
        throw new UnboundException("lookupVariableValue not implemeted yet for JavaMethod");

    }

    private Exp lookupInClasses(Exp symbol) throws GenyrisException {
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
        throw new UnboundException("lookupInSuperClasses not implemented for JavaObjects.");
    }

    public void setVariableValue(Exp symbol, Exp valu) throws UnboundException {
        throw new UnboundException("setVariableValue not implemeted yet for JavaMethod");
    }

    public String toString() {
        return _theMethod.toString();
    }

    public Exp lookupVariableShallow(Exp symbol) throws GenyrisException {
        if (symbol == CLASSES) {
            return getClasses(_parent);
        }
        else {
            throw new UnboundException("lookupVariableShallow not implemeted yet for JavaMethod");
        }
    }

    private int len(Exp l) throws AccessException {
        int result = 0;
        while(l != NIL) {
            result += 1;
            l = l.cdr();
        }
        return result;
    }
    public Exp applyFunction(Environment environment, Exp[] arguments) throws GenyrisException {
        try {
            Object[] jargs = new Object[len(arguments[0])];
            int i = 0;
            while(arguments[0] != NIL) {
                jargs[i] = arguments[0].car().getJavaValue();
                arguments[0] = arguments[0].cdr();
                i++;
            }
            return new JavaWrapper(_parent, _theMethod.invoke(_theJavaObject, jargs));
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return NIL;
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
