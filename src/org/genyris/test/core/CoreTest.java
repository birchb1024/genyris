// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.core;

import junit.framework.TestCase;

import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.genyris.core.Linteger;
import org.genyris.core.Lsymbol;
import org.genyris.core.NilSymbol;
import org.genyris.exception.AccessException;

public class CoreTest extends TestCase {
	private Lsymbol NIL;
	
	public void setUp() {
		NIL = new NilSymbol();		
	}
	
	public void testAccessExceptionCar() {

		Exp a = new Linteger(0);
		try {
			a.car();
			fail("expecting exception");
		}
		catch (AccessException e) {
		}
	}

	public void testAccessExceptionCdr() {

		Exp a = new Linteger(0);
		try {
			a.cdr();
			fail("expecting exception");
		}
		catch (AccessException e) {
		}
	}

	public void testAccessExceptionSetCar() {

		Exp a = new Linteger(0);
		try {
			a.setCar(new Lsymbol("foo"));
			fail("expecting exception");
		}
		catch (AccessException e) {
		}
	}

	public void testAccessExceptionSetCdr() {

		Exp a = new Linteger(0);
		try {
			a.setCar(new Lsymbol("foo"));
			fail("expecting exception");
		}
		catch (AccessException e) {
		}
	}

	public void testLength() throws AccessException {

		Exp list = new Lcons(new Linteger(1), new Lcons(new Linteger(2), NIL));
		assertEquals(2, list.length(NIL));
		assertEquals(1, list.cdr().length(NIL));
		assertEquals(0, list.cdr().cdr().length(NIL));
	}

	public void testLengthNIL() throws AccessException {
		Exp list = NIL;
		try {
			list.cdr().cdr().cdr().length(NIL);
			fail("expecting exception");
		}
		catch (AccessException e) {
		}
		finally {
		}
	}
	public void testLisp() {
		assertTrue(new Lcons(NIL, NIL).listp());
		assertFalse(new Linteger(1).listp());
	}
}
