package org.lispin.jlispin.test.core;

import junit.framework.TestCase;

import org.lispin.jlispin.core.AccessException;
import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lcons;
import org.lispin.jlispin.core.Linteger;
import org.lispin.jlispin.core.SymbolTable;

public class CoreTest extends TestCase {

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
			a.setCar(SymbolTable.NIL);
			fail("expecting exception");
		}
		catch (AccessException e) {
		}
	}

	public void testAccessExceptionSetCdr() {

		Exp a = new Linteger(0);
		try {
			a.setCar(SymbolTable.NIL);
			fail("expecting exception");
		}
		catch (AccessException e) {
		}
	}

	public void testLength() throws AccessException {
		Exp list = new Lcons(new Linteger(1), new Lcons(new Linteger(2), SymbolTable.NIL));
		assertEquals(2, list.length());
		assertEquals(1, list.cdr().length());
		assertEquals(0, list.cdr().cdr().length());
	}

	public void testLengthNIL() throws AccessException {
		Exp list = SymbolTable.NIL;
		try {
			list.cdr().cdr().cdr().length();
			fail("expecting exception");
		}
		catch (AccessException e) {
		}
		finally {
		}
	}
	public void testLisp() {
		assertTrue(new Lcons(SymbolTable.NIL, SymbolTable.NIL).listp());
		assertFalse(new Linteger(1).listp());
	}
}
