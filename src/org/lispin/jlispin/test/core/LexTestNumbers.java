package org.lispin.jlispin.test.core;

import java.math.BigDecimal;

import org.genyris.core.*;
import org.lispin.jlispin.io.Lex;
import org.lispin.jlispin.io.LexException;
import org.lispin.jlispin.io.StringInStream;
import org.lispin.jlispin.io.UngettableInStream;

import junit.framework.TestCase;

public class LexTestNumbers extends TestCase {
	
	public SymbolTable _table = new SymbolTable();
	
	private BigDecimal excerciseParseDecimalNumber(String input) throws LexException
	{
		return new Lex(new UngettableInStream( new StringInStream(input)), _table).parseDecimalNumber();
	}
	private BigDecimal excerciseParseDecimalFraction(String input) throws LexException
	{
		return new Lex(new UngettableInStream( new StringInStream(input)), _table).parseDecimalNumber();
	}
	private BigDecimal excerciseParseNumberInt(String input) throws LexException
	{
		Exp result =  new Lex(new UngettableInStream( new StringInStream(input)), _table).parseNumber();
		return(BigDecimal)result.getJavaValue();
	}
	
	private BigDecimal excerciseParseNumberFloatingPoint(String input) throws LexException
	{
		Exp result =  new Lex(new UngettableInStream( new StringInStream(input)), _table).parseNumber();
		return (BigDecimal) result.getJavaValue();
	}

	private double excerciseParseNumberDouble(String input) throws LexException
	{
		Exp result =  new Lex(new UngettableInStream( new StringInStream(input)), _table).parseNumber();
		return ((Double) result.getJavaValue()).doubleValue();
	}

	public void testParseDecimalNumber1() throws Exception {
		assertEquals(new BigDecimal(1), excerciseParseDecimalNumber("1"));
		assertEquals(new BigDecimal(1), excerciseParseDecimalNumber("01"));
		assertEquals(new BigDecimal(1000), excerciseParseDecimalNumber("1000"));
		assertEquals(new BigDecimal(0), excerciseParseDecimalNumber("0"));
		assertEquals(new BigDecimal(999999), excerciseParseDecimalNumber("999999"));
		assertEquals(new BigDecimal(999999999), excerciseParseDecimalNumber("999999999"));
		assertEquals(new BigDecimal(999999999), excerciseParseDecimalNumber("00999999999"));
		}

	public void testParseDecimalFraction1() throws Exception {
		assertEquals(new BigDecimal("0.1").toString(), excerciseParseDecimalFraction(".1").toString());
		assertEquals(new BigDecimal("0.01"), excerciseParseDecimalFraction(".01"));
		assertEquals(new BigDecimal("0.1000"), excerciseParseDecimalFraction(".1000"));
		assertEquals(new BigDecimal("0.0"), excerciseParseDecimalFraction(".0"));
		assertEquals(new BigDecimal("0.12345"), excerciseParseDecimalFraction(".12345"));
		assertEquals(new BigDecimal("0.001"), excerciseParseDecimalFraction(".001"));
		assertEquals(new BigDecimal("0.000000012"), excerciseParseDecimalFraction(".000000012"));
		}

	public void testParseNumber1() throws Exception {
		assertEquals(new BigDecimal("1"), excerciseParseNumberInt("1"));
		assertEquals(new BigDecimal("1"), excerciseParseNumberInt("01"));
		assertEquals(new BigDecimal("1000"), excerciseParseNumberInt("1000"));
		assertEquals(new BigDecimal("0"), excerciseParseNumberInt("0"));
		assertEquals(new BigDecimal("999999"), excerciseParseNumberInt("999999"));
		assertEquals(new BigDecimal("999999999"), excerciseParseNumberInt("999999999"));
		assertEquals(new BigDecimal("999999999"), excerciseParseNumberInt("00999999999"));
	}

	public void testParseNumberNegative1() throws Exception {
		assertEquals(new BigDecimal("-1"), excerciseParseNumberInt("-1"));
		assertEquals(new BigDecimal("-1"), excerciseParseNumberInt("-01"));
		assertEquals(new BigDecimal("-1000"), excerciseParseNumberInt("-1000"));
		assertEquals(new BigDecimal("0"), excerciseParseNumberInt("-0"));
		assertEquals(new BigDecimal("-999999"), excerciseParseNumberInt("-999999"));
		assertEquals(new BigDecimal("-999999999"), excerciseParseNumberInt("-999999999"));
		assertEquals(new BigDecimal("-999999999"), excerciseParseNumberInt("-00999999999"));
	}

	public void testParseNumberDoubles() throws Exception {
		assertEquals(new BigDecimal("1.1") , excerciseParseNumberFloatingPoint("1.1"));
		assertEquals(new BigDecimal("1.2345") , excerciseParseNumberFloatingPoint("1.2345"));
		assertEquals(new BigDecimal("1.01"), excerciseParseNumberFloatingPoint("01.01"));
		assertEquals(new BigDecimal("1000.100"), excerciseParseNumberFloatingPoint("1000.100"));
		assertEquals(new BigDecimal("0.0"), excerciseParseNumberFloatingPoint("0.0"));
		assertEquals(new BigDecimal("999999.99"), excerciseParseNumberFloatingPoint("999999.99"));
		assertEquals(new BigDecimal("999999999.99"), excerciseParseNumberFloatingPoint("999999999.99"));
		assertEquals(new BigDecimal("999999999.99"), excerciseParseNumberFloatingPoint("00999999999.99"));
	}

	public void testParseNumberDoubleNegative() throws Exception {
		assertEquals(new BigDecimal("-1.1") , excerciseParseNumberFloatingPoint("-1.1"));
		assertEquals(new BigDecimal("-1.01"), excerciseParseNumberFloatingPoint("-01.01"));
		assertEquals(new BigDecimal("-1.010"), excerciseParseNumberFloatingPoint("-01.010"));
		assertEquals(new BigDecimal("-1000.100"), excerciseParseNumberFloatingPoint("-1000.100"));
		assertEquals(new BigDecimal("0.0"), excerciseParseNumberFloatingPoint("-0.0"));
		assertEquals(new BigDecimal("-999999.99"), excerciseParseNumberFloatingPoint("-999999.99"));
		assertEquals(new BigDecimal("-999999999.99"), excerciseParseNumberFloatingPoint("-999999999.99"));
		assertEquals(new BigDecimal("-999999999.99"), excerciseParseNumberFloatingPoint("-00999999999.99"));
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
		assertEquals(-1.1e-2, excerciseParseNumberDouble("-1.1e-2"), 0.00001);
		assertEquals(-1.01e-1, excerciseParseNumberDouble("-01.01e-1"), 0.000001);
		assertEquals(-1.01e-30, excerciseParseNumberDouble("-01.010e-30"), 0.000001);
		assertEquals(-1000.1e-40, excerciseParseNumberDouble("-1000.100e-40"), 0.001);
		assertEquals(0.0e-0, excerciseParseNumberDouble("-0.0e-0"), 0.001);
		assertEquals(-999999.99e-1, excerciseParseNumberDouble("-999999.99e-1"), 0.001);
		assertEquals(-999999999.99e-2, excerciseParseNumberDouble("-999999999.99e-2"), 0.001);
		assertEquals(-999999999.99e-3, excerciseParseNumberDouble("-00999999999.99e-3"), 0.001);
	}

}
