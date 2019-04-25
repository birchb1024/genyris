package org.genyris.core;

public interface Internable {

	public Symbol internSymbol(Symbol newSym);
	public Symbol internString(String newname);
	public SimpleSymbol SELF();
	public SimpleSymbol CLASSES();
	public SimpleSymbol SUPERCLASSES();
	public SimpleSymbol CLASSNAME();
	public SimpleSymbol LEFT();
	public SimpleSymbol RIGHT();
    public SimpleSymbol LINENUMBER();
    public SimpleSymbol FILENAME();
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
	public SimpleSymbol DOLLAR_AT();
	public SimpleSymbol DOLLAR();
	public SimpleSymbol PREFIX();
	public SimpleSymbol BIGNUM();
	public SimpleSymbol EAGERPROC();
	public SimpleSymbol LAZYPROC();
	public SimpleSymbol PAIR();
	public SimpleSymbol PAIREQUAL();
    public SimpleSymbol PAIRSOURCE();
	public SimpleSymbol STRING();
	public SimpleSymbol PIPE();
	public SimpleSymbol READER();
	public SimpleSymbol SIMPLESYMBOL();
	public SimpleSymbol PARENPARSER();
	public SimpleSymbol INDENTPARSER();
	public SimpleSymbol WRITER();
	public SimpleSymbol DICTIONARY();
	public SimpleSymbol URISYMBOL();
	public SimpleSymbol TRIPLE();
	public SimpleSymbol GRAPH();
    public SimpleSymbol TYPE();
    public SimpleSymbol GLOBALGRAPH();
	public SimpleSymbol SUBCLASSOF();
	public SimpleSymbol DYNAMICSYMBOLREF();
	public SimpleSymbol SOURCE();
	public SimpleSymbol NAME();
	public SimpleSymbol SUBJECT();
	public SimpleSymbol OBJECT();
	public SimpleSymbol PREDICATE();
	public SimpleSymbol JAVAWRAPPER();
	public SimpleSymbol JAVACTOR();
	public SimpleSymbol JAVAMETHOD();
	public SimpleSymbol JAVASTATICMETHOD();
	public SimpleSymbol JAVACLASS();
	public SimpleSymbol PROCEDUREMISSING();
    public SimpleSymbol BISCUIT();
    public SimpleSymbol TAILCALL();
}