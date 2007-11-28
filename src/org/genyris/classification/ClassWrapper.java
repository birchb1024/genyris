// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.classification;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.genyris.core.Lobject;
import org.genyris.core.Lsymbol;
import org.genyris.core.Visitor;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;
import org.genyris.interp.UnboundException;

public class ClassWrapper {
    private Lobject _theClass;
    private Exp     CLASSNAME, SUPERCLASSES, SUBCLASSES;
    private Lsymbol NIL;

    public ClassWrapper(Lobject toWrap) {
        _theClass = toWrap;
        CLASSNAME = toWrap.getParent().internString(Constants.CLASSNAME);
        SUPERCLASSES = toWrap.getParent().internString(Constants.SUPERCLASSES);
        SUBCLASSES = toWrap.getParent().internString(Constants.SUBCLASSES);
        NIL = toWrap.getParent().getNil();
    }

    public Lobject getTheClass() {
        return _theClass;
    }

    public void acceptVisitor(Visitor guest) {
        guest.visitClassWrapper(this);
    }

    public String toString() {
        String result = "<class ";
        try {
            result += getClassName();
            Exp classes = getSuperClasses();
            result += " (";
            while (classes != NIL) {
                ClassWrapper klass = new ClassWrapper((Lobject)classes.car());
                result += klass.getClassName();
                if (classes.cdr() != NIL) result += ' ';
                classes = classes.cdr();
            }
            result += ")";
            // TODO DRY
            Exp subclasses = getSubClasses();
            result += " (";
            while (subclasses != NIL) {
                ClassWrapper klass = new ClassWrapper((Lobject)subclasses.car());
                result += klass.getClassName();
                if (subclasses.cdr() != NIL) result += ' ';
                subclasses = subclasses.cdr();
            }
            result += ")";
            result += ">";
        } catch (AccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    private Exp getSubClasses() {
        try {
            return _theClass.lookupVariableShallow(SUBCLASSES);
        } catch (UnboundException e) {
            return NIL;
        }
    }

    public void addSuperClass(Lobject klass) {
        if (klass == null)
            return;
        try {
            Exp supers = _theClass.lookupVariableShallow(SUPERCLASSES);
            supers = new Lcons(klass, supers);
            _theClass.setVariableValue(SUPERCLASSES, supers);
            new ClassWrapper(klass).addSubClass(_theClass);
            // TODO use a list set adding function to avoid duplicates.
        } catch (UnboundException e) {
            try {
                _theClass.defineVariable(SUPERCLASSES, new Lcons(klass, NIL));
                new ClassWrapper(klass).addSubClass(_theClass);
            } catch (GenyrisException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    public void addSubClass(Lobject klass) {
        if (klass == null)
            return;
        try {
            Exp subs = _theClass.lookupVariableShallow(SUBCLASSES);
            subs = new Lcons(klass, subs);
            _theClass.setVariableValue(SUBCLASSES, subs);
            // TODO use a list set adding function to avoid duplicate subclasses.
        } catch (UnboundException e) {
            try {
                _theClass.defineVariable(SUBCLASSES, new Lcons(klass, NIL));
            } catch (GenyrisException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    private Exp getSuperClasses() {
        try {
            return _theClass.lookupVariableShallow(SUPERCLASSES);
        } catch (UnboundException e) {
            return NIL;
        }
    }

    public String getClassName() {
        try {
            return _theClass.lookupVariableShallow(CLASSNAME).toString();
        } catch (UnboundException e) {
            return "anonymous";
        }
    }

    public static Lobject makeClass(Environment env, Exp klassname, Exp superklasses)
            throws GenyrisException {
        Exp NIL = env.getNil();
        Exp standardClassSymbol = env.internString(Constants.STANDARDCLASS);
        Exp standardClass = env.lookupVariableValue(standardClassSymbol);
        Lobject newClass = new Lobject(env);
        newClass.addClass(standardClass);
        newClass.defineVariable(env.internString(Constants.CLASSNAME), klassname);
        newClass.defineVariable(env.internString(Constants.CLASSES), new Lcons(standardClass, NIL));
        newClass.defineVariable(env.internString(Constants.SUBCLASSES), NIL);
        if (superklasses == NIL)
            superklasses = new Lcons(env.internString(Constants.THING), NIL);
        {
            newClass.defineVariable(env.internString(Constants.SUPERCLASSES), lookupClasses(env,
                    superklasses));
            Exp sklist = superklasses;
            while (sklist != NIL) {
                Lobject sk = (Lobject)(env.lookupVariableValue(sklist.car()));
                Exp subklasses = NIL;
                try {
                    subklasses = sk.lookupVariableShallow(env.internString(Constants.SUBCLASSES));
                } catch (UnboundException ignore) {
                    sk.defineVariable(env.internString(Constants.SUBCLASSES), NIL);
                }
                sk.setVariableValue(env.internString(Constants.SUBCLASSES), new Lcons(newClass,
                        subklasses));
                sklist = sklist.cdr();
            }
        }
        env.defineVariable(klassname, newClass);
        return newClass;
    }

    private static Exp lookupClasses(Environment env, Exp superklasses) throws GenyrisException {
        Exp result = env.getNil();
        while (superklasses != env.getNil()) {
            result = new Lcons(env.lookupVariableValue(superklasses.car()), result);
            superklasses = superklasses.cdr();
        }
        return result;
    }
    public boolean isSubClass(ClassWrapper klass) throws AccessException {
        if(klass._theClass == this._theClass) {
            return true;
        }
        Environment env = _theClass.getParent();
        Exp mysubclasses = getSubClasses();
                // TODO - maybe if ClassWRapper could return an array of ClassWrappers this would be tidy!
        while (mysubclasses != env.getNil()) {
            ClassWrapper mysubklass = new ClassWrapper((Lobject)mysubclasses.car()); // TODO unsafe downcast
            if(mysubklass._theClass == klass._theClass) {
                return true;
            }
            else if(mysubklass.isSubClass(klass)) {
                return true;
            }
            mysubclasses = mysubclasses.cdr();
        }
        return false;
    }

    public boolean isInstance(Exp object) {
        Environment env = _theClass.getParent();
        Exp classes;

        try {
            classes = object.getClasses(env);
            while (classes != env.getNil()) {
                ClassWrapper klass = new ClassWrapper((Lobject)classes.car()); // TODO unsafe downcast
                if(classes.car() == _theClass) {
                    return true;
                }
                if( isSubClass(klass) ) {
                    return true;
                }
                classes = classes.cdr();
            }
        } catch (UnboundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (AccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
}
