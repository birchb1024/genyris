// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.classes;

import org.genyris.classification.ClassWrapper;
import org.genyris.core.Constants;
import org.genyris.core.Lobject;
import org.genyris.core.Lsymbol;
import org.genyris.interp.Environment;

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

    public static Lobject STANDARDCLASS;


    private static Lobject mkClass(Lsymbol classname, String name, Environment env) {
        Lobject newClass = new Lobject(classname, env.internString(name), env );
        newClass.addClass(STANDARDCLASS);
        new ClassWrapper(newClass).addSuperClass(THING);
        return newClass;
    }
    public static void init(Environment env) {
        Lsymbol classname = (Lsymbol) env.internString(Constants.CLASSNAME);
        // Bootstrap the meta-class
        STANDARDCLASS = new Lobject(classname, env.internString(Constants.STANDARDCLASS), env );
        STANDARDCLASS.addClass(STANDARDCLASS);

        THING = mkClass(classname, "Thing", env);
        PAIR = mkClass(classname, "Pair", env);
        OBJECT = mkClass(classname, "Object", env);
        INTEGER = mkClass(classname, "Integer", env);
        BIGNUM = mkClass(classname, "Bignum", env);
        STRING = mkClass(classname, "String", env);
        DOUBLE = mkClass(classname, "Double", env);
        SYMBOL = mkClass(classname, "Symbol", env);
        EAGERPROCEDURE = mkClass(classname, "EagerProcedure", env);
        LAZYPROCEDURE = mkClass(classname, "LazyProcedure", env);
        PRINTWITHCOLON = mkClass(classname, Constants.PRINTWITHCOLON, env);
    }
}
