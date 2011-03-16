// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.core;

import java.util.ArrayList;
import java.util.Comparator;

import org.genyris.classification.ClassMROComparator;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;
import org.genyris.interp.UnboundException;

public abstract class ExpWithEmbeddedClasses extends Exp implements
		Classifiable {
	private ArrayList _classes;

	public ExpWithEmbeddedClasses() {
		_classes = new ArrayList(1);
	}

	public abstract void acceptVisitor(Visitor guest) throws GenyrisException;

	private void sortClassesinMRO(Environment env) {
		Object[] tmp = _classes.toArray();
		Comparator comp = new ClassMROComparator(env.getSymbolTable().NIL(),
				env.getSymbolTable().SUPERCLASSES());
		java.util.Arrays.sort(tmp, comp);
		_classes.clear();
		for (int i = 0; i < tmp.length; i++)
			_classes.add(tmp[i]); // TODO learn some Java
	}

	public void addClass(Dictionary klass) { // TODO change signature to
												// Dictionary
		if (_classes.contains(klass)) {
			return;
		}
		_classes.add(klass);
		sortClassesinMRO(klass.getParent());
	}

	public void setClasses(Exp classList, Exp NIL) throws AccessException {
		if (!(classList instanceof Pair)) {
			throw new AccessException("setClasses expected a list, not "
					+ classList);
		}
		_classes.clear();
		while (classList != NIL) {
			_classes.add(classList.car());
			classList = classList.cdr();
		}
	}

	public Exp getClasses(Environment env) {
		Exp NIL = env.getNil();
		Symbol builtinClassSymbol = getBuiltinClassSymbol(env.getSymbolTable());
		Exp builtinClass;
		try {
			builtinClass = env.lookupVariableValue(builtinClassSymbol);
		} catch (UnboundException e) {
			throw new Error(builtinClassSymbol
					+ "Missing builting class - fatal!");
		}
		Exp classes = new Pair(builtinClass, NIL);
		Object arryOfObjects[] = _classes.toArray(); // TODO why convert?
		for (int i = 0; i < arryOfObjects.length; i++) {
			classes = new Pair((Exp) arryOfObjects[i], classes);
		}
		return classes;
	}

	public void removeClass(Exp k) {
		Dictionary klass = (Dictionary) k;
		_classes.remove(klass);
		sortClassesinMRO(klass.getParent());
	}

	public boolean isTaggedWith(Dictionary klass) {
		return _classes.contains(klass);
	}

}
