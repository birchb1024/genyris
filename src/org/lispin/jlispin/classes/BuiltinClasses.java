package org.lispin.jlispin.classes;

import org.lispin.jlispin.core.Lobject;
import org.lispin.jlispin.core.Lsymbol;
import org.lispin.jlispin.interp.Environment;

public class BuiltinClasses {

	public static Lobject OBJECT;
	public static Lobject PAIR;;
	public static Lobject INTEGER;
	public static Lobject BIGNUM;
	public static Lobject STRING;
	public static Lobject DOUBLE;
	public static Lobject SYMBOL;
    public static Lobject PRINTWITHCOLON;
    public static Lobject STANDARDCLASS;

	private static Lobject mkClass(Lsymbol classname, String name, Lsymbol nil) {
		return new Lobject(classname, new Lsymbol(name), nil ); 
	}
	public static void init(Environment env) {
		Lsymbol NIL = env.getNil();
		Lsymbol classname = (Lsymbol) env.internString("_classname");
		PAIR = mkClass(classname, "Pair", NIL);
		OBJECT = mkClass(classname, "Object", NIL);
		INTEGER = mkClass(classname, "Integer", NIL);
		BIGNUM = mkClass(classname, "Bignum", NIL);
		STRING = mkClass(classname, "String", NIL);
		DOUBLE = mkClass(classname, "Double", NIL);
		SYMBOL = mkClass(classname, "Symbol", NIL);
        PRINTWITHCOLON = mkClass(classname, "PRINTWITHCOLON", NIL);
        STANDARDCLASS = mkClass(classname, "StandardClass", NIL);	
	}
}
