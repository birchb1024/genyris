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
import org.genyris.interp.Environment;
import org.genyris.interp.GenyrisException;

public class BuiltinClasses {

    // TODO get rid of these statics.
    public static Lobject THING;
    public static Lobject OBJECT;
    public static Lobject PAIR;;
    public static Lobject INTEGER;
    public static Lobject BIGNUM;
    public static Lobject STRING;
    public static Lobject DOUBLE;
    public static Lobject SYMBOL;
    public static Lobject PRINTWITHCOLON;

    public static Lobject EAGERPROCEDURE;
    public static Lobject LAZYPROCEDURE;

//    public static Lobject STANDARDCLASS;


    private static Lobject mkClass(Lsymbol classname, String name, Environment env, Exp STANDARDCLASS) throws GenyrisException {
        Exp symbolicName = env.internString(name);
        Lobject newClass = new Lobject(classname, symbolicName, env );
        newClass.addClass(STANDARDCLASS);
        new ClassWrapper(newClass).addSuperClass(THING);
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

        THING = mkClass(classname, "Thing", env, STANDARDCLASS);
        PAIR = mkClass(classname, "Pair", env, STANDARDCLASS);
        OBJECT = mkClass(classname, "Object", env, STANDARDCLASS);
        INTEGER = mkClass(classname, "Integer", env, STANDARDCLASS);
        BIGNUM = mkClass(classname, "Bignum", env, STANDARDCLASS);
        STRING = mkClass(classname, "String", env, STANDARDCLASS);
        DOUBLE = mkClass(classname, "Double", env, STANDARDCLASS);
        SYMBOL = mkClass(classname, "Symbol", env, STANDARDCLASS);
        EAGERPROCEDURE = mkClass(classname, "EagerProcedure", env, STANDARDCLASS);
        LAZYPROCEDURE = mkClass(classname, "LazyProcedure", env, STANDARDCLASS);
        PRINTWITHCOLON = mkClass(classname, Constants.PRINTWITHCOLON, env, STANDARDCLASS);
    }
}
