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
        excerciseEval("C" , "<class C (Thing) (C1 )>");
        }

    public void testTagWithColon() throws Exception {
        excerciseEval("(class Miles)", "<class Miles (Thing) ()>");
        excerciseEval("(define x 45)", "45");
        excerciseEval("(x:Miles)", "45");
        excerciseEval("(x _classes)", "(<class Miles (Thing) ()> <class Bignum (Builtin) ()>)");
        }

    public void testTagWithTag() throws Exception {
        excerciseEval("(class Miles)", "<class Miles (Thing) ()>");
        excerciseEval("(define x 45)", "45");
        excerciseEval("(tag x Miles)", "45");
        excerciseEval("(x _classes)", "(<class Miles (Thing) ()> <class Bignum (Builtin) ()>)");
        }
}
