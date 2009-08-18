package org.genyris.core;

public interface Internable {

	public SimpleSymbol internSymbol(SimpleSymbol newSym);
	public SimpleSymbol internString(String newname);
	public SimpleSymbol SELF();
	public SimpleSymbol CLASSES();
	public SimpleSymbol SUPERCLASSES();
	public SimpleSymbol CLASSNAME();
	public SimpleSymbol LEFT();
	public SimpleSymbol RIGHT();
	public SimpleSymbol DYNAMIC_SYMBOL();
	public SimpleSymbol REST();
	public SimpleSymbol DICT();
	public SimpleSymbol VARS();
	public SimpleSymbol TRUE();
	public SimpleSymbol FALSE();
	public SimpleSymbol SUBCLASSES();
	public SimpleSymbol STANDARDCLASS();
	public SimpleSymbol THING();
	public SimpleSymbol VALIDATE();
	public SimpleSymbol LAMBDA();
	public SimpleSymbol LAMBDAQ();
	public SimpleSymbol LAMBDAM();
	public SimpleSymbol EOF();
	public SimpleSymbol NIL();
	public SimpleSymbol SQUARE();
	public SimpleSymbol CURLY();
	public SimpleSymbol TEMPLATE();
	public SimpleSymbol QUOTE();
	public SimpleSymbol COMMA_AT();
	public SimpleSymbol COMMA();
	public SimpleSymbol PREFIX();
	public SimpleSymbol BIGNUM();
	public SimpleSymbol EAGERPROC();
	public SimpleSymbol LAZYPROC();
	public SimpleSymbol PAIR();
	public SimpleSymbol STRING();
	public SimpleSymbol READER();
	public SimpleSymbol SIMPLESYMBOL();
	public SimpleSymbol PARENPARSER();
	public SimpleSymbol WRITER();
	public SimpleSymbol DICTIONARY();
	public SimpleSymbol URISYMBOL();
	public SimpleSymbol TRIPLE();
	public SimpleSymbol TRIPLESTORE();
    public SimpleSymbol TYPE();
    public SimpleSymbol DESCRIPTIONS();
	public SimpleSymbol SUBCLASSOF();
	public SimpleSymbol DYNAMICSYMBOLREF();

}