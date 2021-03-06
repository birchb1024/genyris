// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.core;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.genyris.core.Bignum;
import org.genyris.core.Exp;
import org.genyris.core.SymbolTable;
import org.genyris.exception.GenyrisException;
import org.genyris.io.Lex;
import org.genyris.io.LexException;
import org.genyris.io.StringInStream;
import org.genyris.io.UngettableInStream;

public class LexTestNumbers extends TestCase {

	public SymbolTable _table = new SymbolTable();

	public void setUp() {
		_table.init(null);
	}

	private void excerciseNextTokenBadInt(String toparse)
			throws GenyrisException {
		_table.init(null);
		Lex lexer = new Lex(
				new UngettableInStream(new StringInStream(toparse)), _table);
		try {
			Exp result = lexer.nextToken();

			fail("got " + result + " when expecting exception");
		} catch (LexException e) {
		}
	}

	private BigDecimal excerciseParseDecimalNumber(String input)
			throws LexException {
		return new Lex(new UngettableInStream(new StringInStream(input)),
				_table).parseDecimalNumber();
	}

	private BigDecimal excerciseParseDecimalFraction(String input)
			throws LexException {
		return new Lex(new UngettableInStream(new StringInStream(input)),
				_table).parseDecimalNumber();
	}

	private BigDecimal excerciseParseNumberInt(String input)
			throws LexException {
		Exp result = new Lex(new UngettableInStream(new StringInStream(input)),
				_table).parseNumber();
		return ((Bignum) result).bigDecimalValue();
	}

	private BigDecimal excerciseParseNumberFloatingPoint(String input)
			throws LexException {
		Exp result = new Lex(new UngettableInStream(new StringInStream(input)),
				_table).parseNumber();
		return ((Bignum) result).bigDecimalValue();
	}

	private double excerciseParseNumberDouble(String input) throws LexException {
		Bignum result = (Bignum) new Lex(new UngettableInStream(
				new StringInStream(input)), _table).parseNumber();
		return result.doubleValue();
	}

	public void testParseDecimalNumber1() throws Exception {
		assertEquals(new BigDecimal("1"), excerciseParseDecimalNumber("1"));
		assertEquals(new BigDecimal("1"), excerciseParseDecimalNumber("01"));
		assertEquals(new BigDecimal("1000"),
				excerciseParseDecimalNumber("1000"));
		assertEquals(new BigDecimal("0"), excerciseParseDecimalNumber("0"));
		assertEquals(new BigDecimal("999999"),
				excerciseParseDecimalNumber("999999"));
		assertEquals(new BigDecimal("999999999"),
				excerciseParseDecimalNumber("999999999"));
		assertEquals(new BigDecimal("999999999"),
				excerciseParseDecimalNumber("00999999999"));
	}

	public void testParseDecimalFraction1() throws Exception {
		assertEquals(new BigDecimal("0.1").toString(),
				excerciseParseDecimalFraction(".1").toString());
		assertEquals(new BigDecimal("0.01"),
				excerciseParseDecimalFraction(".01"));
		assertEquals(new BigDecimal("0.1000"),
				excerciseParseDecimalFraction(".1000"));
		assertEquals(new BigDecimal("0.0"), excerciseParseDecimalFraction(".0"));
		assertEquals(new BigDecimal("0.12345"),
				excerciseParseDecimalFraction(".12345"));
		assertEquals(new BigDecimal("0.001"),
				excerciseParseDecimalFraction(".001"));
		assertEquals(new BigDecimal("0.000000012"),
				excerciseParseDecimalFraction(".000000012"));
	}

	public void testParseNumber1() throws Exception {
		assertEquals(new BigDecimal("1"), excerciseParseNumberInt("1"));
		assertEquals(new BigDecimal("1"), excerciseParseNumberInt("01"));
		assertEquals(new BigDecimal("1000"), excerciseParseNumberInt("1000"));
		assertEquals(new BigDecimal("0"), excerciseParseNumberInt("0"));
		assertEquals(new BigDecimal("999999"),
				excerciseParseNumberInt("999999"));
		assertEquals(new BigDecimal("999999999"),
				excerciseParseNumberInt("999999999"));
		assertEquals(new BigDecimal("999999999"),
				excerciseParseNumberInt("00999999999"));
	}

	public void testParseNumberNegative1() throws Exception {
		assertEquals(new BigDecimal("-1"), excerciseParseNumberInt("-1"));
		assertEquals(new BigDecimal("-1"), excerciseParseNumberInt("-01"));
		assertEquals(new BigDecimal("-1000"), excerciseParseNumberInt("-1000"));
		assertEquals(new BigDecimal("0"), excerciseParseNumberInt("-0"));
		assertEquals(new BigDecimal("-999999"),
				excerciseParseNumberInt("-999999"));
		assertEquals(new BigDecimal("-999999999"),
				excerciseParseNumberInt("-999999999"));
		assertEquals(new BigDecimal("-999999999"),
				excerciseParseNumberInt("-00999999999"));
	}

	public void testParseNumberDoubles() throws Exception {
		assertEquals(new BigDecimal("1.1"),
				excerciseParseNumberFloatingPoint("1.1"));
		assertEquals(new BigDecimal("1.2345"),
				excerciseParseNumberFloatingPoint("1.2345"));
		assertEquals(new BigDecimal("1.01"),
				excerciseParseNumberFloatingPoint("01.01"));
		assertEquals(new BigDecimal("1000.100"),
				excerciseParseNumberFloatingPoint("1000.100"));
		assertEquals(new BigDecimal("0.0"),
				excerciseParseNumberFloatingPoint("0.0"));
		assertEquals(new BigDecimal("999999.99"),
				excerciseParseNumberFloatingPoint("999999.99"));
		assertEquals(new BigDecimal("999999999.99"),
				excerciseParseNumberFloatingPoint("999999999.99"));
		assertEquals(new BigDecimal("999999999.99"),
				excerciseParseNumberFloatingPoint("00999999999.99"));
	}

	public void testParseNumberDoubleNegative() throws Exception {
		assertEquals(new BigDecimal("-1.1"),
				excerciseParseNumberFloatingPoint("-1.1"));
		assertEquals(new BigDecimal("-1.01"),
				excerciseParseNumberFloatingPoint("-01.01"));
		assertEquals(new BigDecimal("-1.010"),
				excerciseParseNumberFloatingPoint("-01.010"));
		assertEquals(new BigDecimal("-1000.100"),
				excerciseParseNumberFloatingPoint("-1000.100"));
		assertEquals(new BigDecimal("0.0"),
				excerciseParseNumberFloatingPoint("-0.0"));
		assertEquals(new BigDecimal("-999999.99"),
				excerciseParseNumberFloatingPoint("-999999.99"));
		assertEquals(new BigDecimal("-999999999.99"),
				excerciseParseNumberFloatingPoint("-999999999.99"));
		assertEquals(new BigDecimal("-999999999.99"),
				excerciseParseNumberFloatingPoint("-00999999999.99"));
	}

	public void testParseNumberDoubleSci() throws Exception {
		assertEquals(-110.0, excerciseParseNumberDouble("-1.1e2"), 0.001);
		assertEquals(-1.01e1, excerciseParseNumberDouble("-01.01e1"), 0.001);
		assertEquals(-1.01e3, excerciseParseNumberDouble("-01.010e3"), 0.001);
		assertEquals(-1000.1e4, excerciseParseNumberDouble("-1000.100e4"),
				0.001);
		assertEquals(0.0e0, excerciseParseNumberDouble("-0.0e0"), 0.001);
		assertEquals(-999999.99e1, excerciseParseNumberDouble("-999999.99e1"),
				0.001);
		assertEquals(-999999999.99e2,
				excerciseParseNumberDouble("-999999999.99e2"), 0.001);
		assertEquals(-999999999.99e3,
				excerciseParseNumberDouble("-00999999999.99e3"), 0.001);
	}

	public void testParseNumberDoubleNegativeSci() throws Exception {
		assertEquals(-1.1e-2, excerciseParseNumberDouble("-1.1e-2"), 0.00001);
		assertEquals(-1.01e-1, excerciseParseNumberDouble("-01.01e-1"),
				0.000001);
		assertEquals(-1.01e-30, excerciseParseNumberDouble("-01.010e-30"),
				0.000001);
		assertEquals(-1000.1e-40, excerciseParseNumberDouble("-1000.100e-40"),
				0.001);
		assertEquals(0.0e-0, excerciseParseNumberDouble("-0.0e-0"), 0.001);
		assertEquals(-999999.99e-1,
				excerciseParseNumberDouble("-999999.99e-1"), 0.001);
		assertEquals(-999999999.99e-2,
				excerciseParseNumberDouble("-999999999.99e-2"), 0.001);
		assertEquals(-999999999.99e-3,
				excerciseParseNumberDouble("-00999999999.99e-3"), 0.001);
	}

	public void testLexBadNumbers() throws Exception {

		excerciseNextTokenBadInt("1..2");
		excerciseNextTokenBadInt("1ee3");
		excerciseNextTokenBadInt("1e");
		excerciseNextTokenBadInt("1e");
	}

}
