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
	public Symbol DICT();
	public Symbol VARS();
	public Symbol TRUE();
	public Symbol FALSE();
	public Symbol SUBCLASSES();
	public Symbol STANDARDCLASS();
	public Symbol THING();
	public SimpleSymbol VALIDATE();
	public Symbol LAMBDA();
	public Symbol LAMBDAQ();
	public Symbol LAMBDAM();
	public Symbol EOF();
	public Symbol NIL();
	public Symbol TEMPLATE();
	public Symbol QUOTE();
	public Symbol COMMA_AT();
	public Symbol COMMA();
	public Symbol PREFIX();
	public Symbol BIGNUM();
	public Symbol EAGERPROC();
	public Symbol LAZYPROC();
	public Symbol PAIR();
	public Symbol STRING();
	public Symbol READER();
	public Symbol SIMPLESYMBOL();
	public Symbol PARENPARSER();
	public Symbol WRITER();
	public Symbol DICTIONARY();
	public Symbol URISYMBOL();
	public Symbol TRIPLE();
	public Symbol TRIPLESET();
    public Symbol TYPE();
    public Symbol DESCRIPTIONS();
	public Symbol SUBCLASSOF();
	public Symbol DYNAMICSYMBOLREF();

}