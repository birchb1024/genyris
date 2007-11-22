// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.classes;

import org.genyris.classification.ClassWrapper;
import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Lobject;
import org.genyris.core.Lsymbol;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;

public class BuiltinClasses {

    private static Lobject mkClass(Lsymbol classname, String name, Environment env, Exp STANDARDCLASS, Lobject superClass) throws GenyrisException {
        Exp symbolicName = env.internString(name);
        Lobject newClass = new Lobject(classname, symbolicName, env );
        newClass.defineVariable(env.internString(Constants.SUBCLASSES), env.getNil());
        newClass.defineVariable(env.internString(Constants.SUPERCLASSES), env.getNil());
        newClass.addClass(STANDARDCLASS);
        if(superClass != null)
            new ClassWrapper(newClass).addSuperClass(superClass);
        env.defineVariable(symbolicName, newClass);
        return newClass;
    }
    public static void init(Environment env) throws GenyrisException {
        Lobject STANDARDCLASS;
        Lsymbol classname = (Lsymbol) env.internString(Constants.CLASSNAME);
        {
            // Bootstrap the meta-class
            STANDARDCLASS = new Lobject(classname, env.internString(Constants.STANDARDCLASS), env );
            STANDARDCLASS.addClass(STANDARDCLASS);
            env.defineVariable(env.internString(Constants.STANDARDCLASS), STANDARDCLASS);
        }

        Lobject THING = mkClass(classname, "Thing", env, STANDARDCLASS, null);
        Lobject builtin = mkClass(classname, "Builtin", env, STANDARDCLASS, THING);
        Lobject pair = mkClass(classname, "Pair", env, STANDARDCLASS, builtin);
        mkClass(classname, Constants.PRINTWITHCOLON, env, STANDARDCLASS, pair);
        mkClass(classname, "Object", env, STANDARDCLASS, builtin);
        mkClass(classname, "Integer", env, STANDARDCLASS, builtin);
        mkClass(classname, "Bignum", env, STANDARDCLASS, builtin);
        mkClass(classname, "String", env, STANDARDCLASS, builtin);
        mkClass(classname, "Double", env, STANDARDCLASS, builtin);
        mkClass(classname, "Symbol", env, STANDARDCLASS, builtin);
        mkClass(classname, "JavaObject", env, STANDARDCLASS, builtin);
        mkClass(classname, "JavaMethod", env, STANDARDCLASS, builtin);
        Lobject closure = mkClass(classname, "Closure", env, STANDARDCLASS, builtin);
        mkClass(classname, "EagerProcedure", env, STANDARDCLASS, closure);
        mkClass(classname, "LazyProcedure", env, STANDARDCLASS, closure);
    }
}
