// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.interp.builtin;

import org.genyris.core.*;
import org.genyris.dl.Triple;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SortFunction extends ApplicableFunction {

	public SortFunction(Interpreter interp) {
		super(interp, "sort", true);
	}

	public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations) throws GenyrisException {
		checkArguments(arguments, 1);
		if(arguments[0] == NIL){
			return NIL;
		}
		Class[] types = { Pair.class };
		checkArgumentTypes(types, arguments);
		Pair theList = (Pair) arguments[0];
		Exp head = theList;
		Exp item = head.car();
		if(!(	item instanceof Bignum
			 || item instanceof Pair
			 || item instanceof PairEquals
			 || item instanceof StrinG
			 || item instanceof Symbol
			 || item instanceof Triple
			)) {
			throw new GenyrisException("item in argument to sort must be atomic, but got " + item.getClasses(envForBindOperations).toString());
		}
		Class theClass = theList.car().getClass();
		// Get parent classes. . .
		if(theClass.equals(PairEquals.class)) {
			theClass = Pair.class; // Treat PairEquals exactl the same as Pair. TODO special case should be handled by isInstance or similar.
		}
		if(theClass.equals(PrefixSymbol.class) || theClass.equals(EscapedSymbol.class) || theClass.equals(SimpleSymbol.class)) {
			theClass = Symbol.class; // Treat all Symbols the same. TODO special case should be handled by isInstance or similar.
		}
		List<Exp> tmp = new ArrayList<Exp>();
		try {
			while(!head.isNil()) {
				if(!theClass.isInstance(head.car())) {
					throw new GenyrisException("item in argument to sort must be similar class " + theClass.getName() + " but was " + head.car().getClass().getName());
				}
				tmp.add(head.car());
				head = head.cdr();
			}
		} catch (Exception e) {
			throw new GenyrisException(e.getMessage());
		}
		Collections.sort(tmp);
		Collections.reverse(tmp);
		Iterator<Exp> it = tmp.iterator(); // TODO extract method
		Exp result = NIL;
		while(it.hasNext()) {
			Exp a = (Exp)it.next();
			result = Pair.cons(a, result);
		}
		return result;

	}

}
