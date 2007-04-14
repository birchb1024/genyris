package org.lispin.jlispin.test.core;

import org.lispin.jlispin.core.*;

import junit.framework.TestCase;

public class LexTestNumbers extends TestCase {
	
	private int excerciseParseDecimalNumber(String input) throws LexException
	{
		return new Lex(new UngettableInStream( new StringInStream(input))).parseDecimalNumber();
	}
	private double excerciseParseDecimalFraction(String input) throws LexException
	{
		return new Lex(new UngettableInStream( new StringInStream(input))).parseDecimalFraction();
	}
	private int excerciseParseNumberInt(String input) throws LexException
	{
		Exp result =  new Lex(new UngettableInStream( new StringInStream(input))).parseNumber();
		return ((Integer) result.getValue()).intValue();
	}

	private double excerciseParseNumberDouble(String input) throws LexException
	{
		Exp result =  new Lex(new UngettableInStream( new StringInStream(input))).parseNumber();
		return ((Double) result.getValue()).doubleValue();
	}

	public void testParseDecimalNumber1() throws Exception {
		assertEquals(1, excerciseParseDecimalNumber("1"));
		assertEquals(1, excerciseParseDecimalNumber("01"));
		assertEquals(1000, excerciseParseDecimalNumber("1000"));
		assertEquals(0, excerciseParseDecimalNumber("0"));
		assertEquals(999999, excerciseParseDecimalNumber("999999"));
		assertEquals(999999999, excerciseParseDecimalNumber("999999999"));
		assertEquals(999999999, excerciseParseDecimalNumber("00999999999"));
		}

	public void testParseDecimalFraction1() throws Exception {
		assertEquals(0.1, excerciseParseDecimalFraction("1"), 0.00001);
		assertEquals(0.01, excerciseParseDecimalFraction("01"), 0.00001);
		assertEquals(0.1000, excerciseParseDecimalFraction("1000"), 0.00001);
		assertEquals(0.0, excerciseParseDecimalFraction("0"), 0.001);
		assertEquals(0.12345, excerciseParseDecimalFraction("12345"), 0.00000001);
		assertEquals(0.001, excerciseParseDecimalFraction("001"), 0.00000001);
		assertEquals(0.000000012, excerciseParseDecimalFraction("000000012"), 0.00000001);
		}

	public void testParseNumber1() throws Exception {
		assertEquals(1, excerciseParseNumberInt("1"));
		assertEquals(1, excerciseParseNumberInt("01"));
		assertEquals(1000, excerciseParseNumberInt("1000"));
		assertEquals(0, excerciseParseNumberInt("0"));
		assertEquals(999999, excerciseParseNumberInt("999999"));
		assertEquals(999999999, excerciseParseNumberInt("999999999"));
		assertEquals(999999999, excerciseParseNumberInt("00999999999"));
	}

	public void testParseNumberNegative1() throws Exception {
		assertEquals(-1, excerciseParseNumberInt("-1"));
		assertEquals(-1, excerciseParseNumberInt("-01"));
		assertEquals(-1000, excerciseParseNumberInt("-1000"));
		assertEquals(0, excerciseParseNumberInt("-0"));
		assertEquals(-999999, excerciseParseNumberInt("-999999"));
		assertEquals(-999999999, excerciseParseNumberInt("-999999999"));
		assertEquals(-999999999, excerciseParseNumberInt("-00999999999"));
	}

	public void testParseNumberDoubles() throws Exception {
		assertEquals(1.1 , excerciseParseNumberDouble("1.1"), 0.001);
		assertEquals(1.01, excerciseParseNumberDouble("01.01"), 0.001);
		assertEquals(1000.1, excerciseParseNumberDouble("1000.100"), 0.001);
		assertEquals(0.0, excerciseParseNumberDouble("0.0"), 0.001);
		assertEquals(999999.99, excerciseParseNumberDouble("999999.99"), 0.001);
		assertEquals(999999999.99, excerciseParseNumberDouble("999999999.99"), 0.001);
		assertEquals(999999999.99, excerciseParseNumberDouble("00999999999.99"), 0.001);
	}

	public void testParseNumberDoubleNegative() throws Exception {
		assertEquals(-1.1 , excerciseParseNumberDouble("-1.1"), 0.001);
		assertEquals(-1.01, excerciseParseNumberDouble("-01.01"), 0.001);
		assertEquals(-1.01, excerciseParseNumberDouble("-01.010"), 0.001);
		assertEquals(-1000.1, excerciseParseNumberDouble("-1000.100"), 0.001);
		assertEquals(0.0, excerciseParseNumberDouble("-0.0"), 0.001);
		assertEquals(-999999.99, excerciseParseNumberDouble("-999999.99"), 0.001);
		assertEquals(-999999999.99, excerciseParseNumberDouble("-999999999.99"), 0.001);
		assertEquals(-999999999.99, excerciseParseNumberDouble("-00999999999.99"), 0.001);
	}

	public void testParseNumberDoubleSci() throws Exception {
		assertEquals(-110.0 , excerciseParseNumberDouble("-1.1e2"), 0.001);
		assertEquals(-1.01e1, excerciseParseNumberDouble("-01.01e1"), 0.001);
		assertEquals(-1.01e3, excerciseParseNumberDouble("-01.010e3"), 0.001);
		assertEquals(-1000.1e4, excerciseParseNumberDouble("-1000.100e4"), 0.001);
		assertEquals(0.0e0, excerciseParseNumberDouble("-0.0e0"), 0.001);
		assertEquals(-999999.99e1, excerciseParseNumberDouble("-999999.99e1"), 0.001);
		assertEquals(-999999999.99e2, excerciseParseNumberDouble("-999999999.99e2"), 0.001);
		assertEquals(-999999999.99e3, excerciseParseNumberDouble("-00999999999.99e3"), 0.001);
	}
	public void testParseNumberDoubleNegativeSci() throws Exception {
		assertEquals(-1.1e-2, excerciseParseNumberDouble("-1.1e-2"), 0.001);
		assertEquals(-1.01e-1, excerciseParseNumberDouble("-01.01e-1"), 0.001);
		assertEquals(-1.01e-30, excerciseParseNumberDouble("-01.010e-30"), 0.001);
		assertEquals(-1000.1e-40, excerciseParseNumberDouble("-1000.100e-40"), 0.001);
		assertEquals(0.0e-0, excerciseParseNumberDouble("-0.0e-0"), 0.001);
		assertEquals(-999999.99e-1, excerciseParseNumberDouble("-999999.99e-1"), 0.001);
		assertEquals(-999999999.99e-2, excerciseParseNumberDouble("-999999999.99e-2"), 0.001);
		assertEquals(-999999999.99e-3, excerciseParseNumberDouble("-00999999999.99e-3"), 0.001);
	}

}
