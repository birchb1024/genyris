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

	public Exp bindAndExecute(Closure proc, Exp[] arguments,
			Environment envForBindOperations) throws GenyrisException {
		checkArguments(arguments, 1);
		Class[] types = { Pair.class };
		checkArgumentTypes(types, arguments);
		Pair theList = (Pair) arguments[0];
		Exp head = theList;
		Exp item = head.car();
		if(!(	item instanceof Bignum
			 || item instanceof StrinG
			 || item instanceof Symbol )) {
			throw new GenyrisException("item in argument to sort must be atomic, but got " + head.getClass().getName());
		}
		Class theClass = theList.car().getClass();
		List<Exp> tmp = new ArrayList<Exp>();
		try {
			while(!head.isNil()) {
				if(!head.car().getClass().equals(theClass)) {
					throw new GenyrisException("item in argument to sort must be of the same atomic class" + theClass.getName() + " but was " + head.car().getClass().getName());
				}
				tmp.add(head.car());
				head = head.cdr();
			}
		} catch (Exception e) {
			throw new GenyrisException(e.getMessage());
		}
		Collections.sort(tmp);
		Collections.reverse(tmp);
		Iterator<Exp> it = tmp.iterator();
		Exp result = NIL;
		while(it.hasNext()) {
			Exp a = (Exp)it.next();
			result = Pair.cons(a, result);
		}
		return result;

	}

}
