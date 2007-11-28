// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.interp;

import junit.framework.TestCase;

public class ClassTaggingTests extends TestCase {

    private TestUtilities interpreter;

    protected void setUp() throws Exception {
        super.setUp();
        interpreter = new TestUtilities();
        interpreter._interpreter.init(true);
    }

    private void excerciseEval(String exp, String expected) throws Exception {
        assertEquals(expected, interpreter.eval(exp));
    }

    public void testDefineClass() throws Exception {
        excerciseEval("(class C)", "<class C (Thing) ()>");
        excerciseEval("(class C1 (C))" , "<class C1 (C) ()>");
        excerciseEval("C" , "<class C (Thing) (C1)>");
        }

    public void testTagWithTag() throws Exception {
        excerciseEval("(class Miles)", "<class Miles (Thing) ()>");
        excerciseEval("(define x 45)", "45");
        excerciseEval("(tag x Miles)", "45");
        excerciseEval("(x _classes)", "(<class Miles (Thing) ()> <class Bignum (Builtin) ()>)");
        }
    public void testIsInstance() throws Exception {
        excerciseEval("(class A)", "<class A (Thing) ()>");
        excerciseEval("(class B(A))", "<class B (A) ()>");
        excerciseEval("(define x 45)", "45");
        excerciseEval("(tag x B)", "45");
        excerciseEval("(is-instance? x Thing)", "true");
        excerciseEval("(is-instance? x Builtin)", "true");
        excerciseEval("(is-instance? x Bignum)", "true");
        excerciseEval("(is-instance? x B)", "true");
        excerciseEval("(is-instance? x A)", "true");
        }
    public void testIsInstanceMultipleInheritance() throws Exception {
        excerciseEval("(class A)", "<class A (Thing) ()>");
        excerciseEval("(class B1(A))", "<class B1 (A) ()>");
        excerciseEval("(class B2(A))", "<class B2 (A) ()>");
        excerciseEval("(class B3(A))", "<class B3 (A) ()>");
        excerciseEval("(class C(B1 B2))", "<class C (B2 B1) ()>");
        excerciseEval("(define x 45)", "45");
        excerciseEval("(tag x C)", "45");
        excerciseEval("(is-instance? x Thing)", "true");
        excerciseEval("(is-instance? x Builtin)", "true");
        excerciseEval("(is-instance? x Bignum)", "true");
        excerciseEval("(is-instance? x B1)", "true");
        excerciseEval("(is-instance? x B2)", "true");
        excerciseEval("(is-instance? x B3)", "nil");
        excerciseEval("(is-instance? x C)", "true");
        }
    public void testTypeCheckedFunctions() throws Exception {
        excerciseEval("(class A)", "<class A (Thing) ()>");
        excerciseEval("(class B1(A))", "<class B1 (A) ()>");
        excerciseEval("(class B2(A))", "<class B2 (A) ()>");
        excerciseEval("(class B3(A))", "<class B3 (A) ()>");
        excerciseEval("(class C(B1 B2))", "<class C (B2 B1) ()>");
        excerciseEval("(define x 45)", "45");
        excerciseEval("(tag x C)", "45");
        excerciseEval("(def fn((a:A)) 42)", "<EagerProc: <org.genyris.interp.ClassicFunction>>");
        // excerciseEval("(fn 23)", "exception");
        excerciseEval("(fn x)", "42");

        excerciseEval("(def fn1((a:Bignum)) 42)", "<EagerProc: <org.genyris.interp.ClassicFunction>>");
        excerciseEval("(fn1 x)", "42");
        excerciseEval("(def fn2((a:Builtin)) 42)", "<EagerProc: <org.genyris.interp.ClassicFunction>>");
        excerciseEval("(fn2 x)", "42");
        excerciseEval("(def fn3((a:Thing)) 42)", "<EagerProc: <org.genyris.interp.ClassicFunction>>");
        excerciseEval("(fn3 x)", "42");

    }
}
