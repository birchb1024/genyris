package org.genyris.core;

public interface Internable {

	public Symbol internSymbol(Symbol newSym);
	public Symbol internString(String newname);
	public Symbol SELF();
	public Symbol CLASSES();
	public Symbol SUPERCLASSES();
	public Symbol CLASSNAME();
	public Symbol LEFT();
	public Symbol RIGHT();
	public Symbol DYNAMIC_SYMBOL();
	public Symbol REST();
	public Exp DICT();
	public Exp VARS();
	public Exp TRUE();
	public Exp FALSE();
	public Exp SUBCLASSES();
	public Exp STANDARDCLASS();
	public Exp THING();
	public Symbol VALIDATE();

}