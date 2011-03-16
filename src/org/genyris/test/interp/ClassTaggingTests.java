// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.interp;

import org.genyris.exception.GenyrisException;

import junit.framework.TestCase;

public class ClassTaggingTests extends TestCase {

	private TestUtilities interpreter;

	protected void setUp() throws Exception {
		super.setUp();
		interpreter = new TestUtilities();
	}

	private void excerciseEval(String exp, String expected) throws Exception {
		assertEquals(expected, interpreter.eval(exp));
	}

	private void exceptionEval(String exp, String expected) throws Exception {
		try {
			interpreter.eval(exp);
		} catch (GenyrisException e) {
			assertEquals(expected, e.getMessage());
		}
	}

	public void testDefineClass() throws Exception {
		excerciseEval("(class C)", "<class C (Thing)>");
		excerciseEval("(class C1 (C))", "<class C1 (C)>");
		excerciseEval("C", "<class C (Thing)>");
	}

	public void testTagWithTag() throws Exception {
		excerciseEval("(class Miles)", "<class Miles (Thing)>");
		excerciseEval("(define x 45)", "45");
		excerciseEval("(tag Miles x)", "45");
		excerciseEval("(x .classes)",
				"(<class Miles (Thing)> <class Bignum (Builtin)>)");
	}

	public void testRemoveTag() throws Exception {
		excerciseEval("(class Miles)", "<class Miles (Thing)>");
		excerciseEval("(define x 45)", "45");
		excerciseEval("(tag Miles x)", "45");
		excerciseEval("(x .classes)",
				"(<class Miles (Thing)> <class Bignum (Builtin)>)");
		excerciseEval("(remove-tag Miles x)", "45");
		excerciseEval("(x .classes)", "(<class Bignum (Builtin)>)");
	}

	public void testIsInstance() throws Exception {
		excerciseEval("(class A)", "<class A (Thing)>");
		excerciseEval("(class B(A))", "<class B (A)>");
		excerciseEval("(define x 45)", "45");
		excerciseEval("(tag B x)", "45");
		excerciseEval("(is-instance? x Thing)", "true");
		excerciseEval("(is-instance? x Builtin)", "true");
		excerciseEval("(is-instance? x Bignum)", "true");
		excerciseEval("(is-instance? x B)", "true");
		excerciseEval("(is-instance? x A)", "true");
	}

	public void testIsInstanceMultipleInheritance() throws Exception {
		excerciseEval("(class A)", "<class A (Thing)>");
		excerciseEval("(class B1(A))", "<class B1 (A)>");
		excerciseEval("(class B2(A))", "<class B2 (A)>");
		excerciseEval("(class B3(A))", "<class B3 (A)>");
		excerciseEval("(class C(B1 B2))", "<class C (B2 B1)>");
		excerciseEval("(define x 45)", "45");
		excerciseEval("(tag C x)", "45");
		excerciseEval("(is-instance? x Thing)", "true");
		excerciseEval("(is-instance? x Builtin)", "true");
		excerciseEval("(is-instance? x Bignum)", "true");
		excerciseEval("(is-instance? x B1)", "true");
		excerciseEval("(is-instance? x B2)", "true");
		excerciseEval("(is-instance? x B3)", "nil");
		excerciseEval("(is-instance? x C)", "true");
	}

	public void testTypeCheckedFunctions() throws Exception {
		excerciseEval("(class A)", "<class A (Thing)>");
		excerciseEval("(class B1(A))", "<class B1 (A)>");
		excerciseEval("(class B2(A))", "<class B2 (A)>");
		excerciseEval("(class B3(A))", "<class B3 (A)>");
		excerciseEval("(class C(B1 B2))", "<class C (B2 B1)>");
		excerciseEval("(define x 45)", "45");
		excerciseEval("(tag C x)", "45");
		excerciseEval("(def fn((a =A)) 42)", "<EagerProc: <fn>>");
		exceptionEval(
				"(fn 23)",
				"Type mismatch in function call for (a = A) because validator error: object 23 is not tagged with A");
		excerciseEval("(fn x)", "42");

		excerciseEval("(def fn1((a =Bignum)) 42)", "<EagerProc: <fn1>>");
		excerciseEval("(fn1 x)", "42");
		excerciseEval("(def fn2((a =Builtin)) 42)", "<EagerProc: <fn2>>");
		excerciseEval("(fn2 x)", "42");
		excerciseEval("(def fn3((a =Thing)) 42)", "<EagerProc: <fn3>>");
		excerciseEval("(fn3 x)", "42");

	}

	public void testTypeCheckedFunctionsWithReturn() throws Exception {

		excerciseEval("(def fn (a = Bignum) a)", "<EagerProc: <fn>>");
		excerciseEval("(fn 42)", "42");
		exceptionEval("(fn ^x42)",
				"return type validator error: object x42 is not tagged with Bignum");

		excerciseEval("(class A)", "<class A (Thing)>");
		excerciseEval("(class B1(A))", "<class B1 (A)>");
		excerciseEval("(class B2(A))", "<class B2 (A)>");
		excerciseEval("(class B3(A))", "<class B3 (A)>");
		excerciseEval("(class C(B1 B2))", "<class C (B2 B1)>");
		excerciseEval("(define x 45)", "45");
		excerciseEval("(tag C x)", "45");
		excerciseEval("(class CC() (def .valid?(x) true))",
				"<class CC (Thing)>");
		exceptionEval(
				"(setq 12 CC)",
				"set expects a org.genyris.core.Symbol at position 0 got <12> a org.genyris.core.Bignum");
		excerciseEval("(class XX() (def .valid?(x) nil))", "<class XX (Thing)>");
		exceptionEval(
				"(setq 12 XX)",
				"set expects a org.genyris.core.Symbol at position 0 got <12> a org.genyris.core.Bignum");

		excerciseEval("(def fn((a =A) = Bignum) 42)", "<EagerProc: <fn>>");
		exceptionEval(
				"(fn 23)",
				"Type mismatch in function call for (a = A) because validator error: object 23 is not tagged with A");
		excerciseEval("(fn x)", "42");

		excerciseEval("(def fn1((a =Bignum) = String) 42)",
				"<EagerProc: <fn1>>");
		exceptionEval("(fn1 x)",
				"return type validator error: object 42 is not tagged with String");
		excerciseEval("(def fn2((a =Builtin)= Bignum) 42)",
				"<EagerProc: <fn2>>");
		excerciseEval("(fn2 x)", "42");
		excerciseEval("(def fn3((a =Thing)= Builtin) a)", "<EagerProc: <fn3>>");
		excerciseEval("(fn3 42)", "42");
		excerciseEval("(fn3 'x')", "'x'");
		excerciseEval("(fn3 ^x)", "x");
		excerciseEval("(fn3 ^(3))", "(3)");

	}

	public void testTypeDefect() throws Exception {

		excerciseEval("(define west 23)", "23");
		exceptionEval("(eval (cons ^west 12))",
				"Arguments to 23 must be a list.");
		excerciseEval("(define d (dict))", "(dict)");
		exceptionEval("(eval (cons ^d 12))",
				"Arguments to <dict (dict)> must be a list.");

	}
}
