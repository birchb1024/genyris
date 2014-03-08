// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.interp;

import org.genyris.exception.GenyrisException;

import junit.framework.TestCase;

public class ObjectOrientationTests extends TestCase {

    private TestUtilities interpreter;

    protected void setUp() throws Exception {
        super.setUp();
        interpreter = new TestUtilities();
    }

    private void checkEval(String exp, String expected) throws GenyrisException {
        assertEquals(expected, interpreter.eval(exp));
    }

    private void checkEvalBad(String exp) throws GenyrisException {
        try {
            interpreter.eval(exp);
            fail("expecting exception");
        }
        catch (GenyrisException e) {
        }
    }

    private void eval(String script) throws GenyrisException {
        interpreter.eval(script);
    }

    public void testExcerciseEval() throws GenyrisException {
        eval("(defvar ^global 999)");
        checkEval("global", "999");

        eval("(defvar ^Standard-Class (dict (.classname = ^Standard-Class)))");
        checkEval("Standard-Class", "(dict (.classname = Standard-Class))");
        checkEval("(defvar ^Account (dict (.classes = (list Standard-Class)) (.print = (lambda () (cons global .balance))) ))",
                "(dict (.print = <EagerProc: <anonymous lambda>>))");

        checkEval("(Account " +
            "(defvar ^.new " +
                "(lambda (initial-balance) " +
                "(dict " +
                    "(.classes = (cons Account nil)) " +
                    "(.balance =  initial-balance))))) ",
                    "<EagerProc: <anonymous lambda>>" );

        checkEval("(defvar ^bb  (Account (.new 1000)))"
                ,"(dict (.balance = 1000))");

        checkEval("(bb(.print))", "(999 = 1000)");

        checkEval("(bb((lambda () .balance)))", "1000");
    }


    public void testInheritance() throws GenyrisException {

        eval("(defvar ^Standard-Class (dict))");

        checkEval("(defvar ^Base-1 " +
            "(dict " +
                "(.classes = (cons Standard-Class nil)) " +
                "(.toString = 'Base-1 toString'))) "
                , "(dict (.toString = 'Base-1 toString'))" );

        checkEval("(Base-1 .toString)", "'Base-1 toString'");

        checkEval("(defvar ^Base-2 " +
            "(dict " +
                "(.classes = (cons Standard-Class nil)) " +
                "(.log = 'Base-2 log'))) ",
                "(dict (.log = 'Base-2 log'))");

        checkEval("(Base-2 .log)", "'Base-2 log'");

        eval("(defvar ^Class-1 " +
                "(dict " +
                    "(.classes  =  (cons Standard-Class nil)) " +
                    "(.superclasses  =  (cons Base-1 nil)) " +
                    "(.print  =  'Class-1 print')"  +
                    "(.new  =  " +
                        "(lambda (.a) " +
                            "(dict " +
                                "(.classes  =  (cons Class-1 nil)) " +
                                "(.a  =  .a)))))) " );
        checkEval("(Class-1 .print)", "'Class-1 print'");
        checkEval("(Class-1 .toString)", "'Base-1 toString'");

        eval("(defvar ^Class-2 " +
            "(dict" +
                "(.classes  =  (cons Standard-Class nil))" +
                "(.superclasses  =  (cons Base-2 nil))" +
                "(.draw  =  'Class-2 draw')))" );

        checkEval("(Class-2 .draw)", "'Class-2 draw'");
        checkEval("(Class-2 .log)", "'Base-2 log'");

        eval("(defvar ^object " +
                "(dict" +
                "(.classes  =  (cons Class-1 (cons Class-2 nil)))))" );
        checkEval("(object .log)", "'Base-2 log'");
        checkEval("(object .draw)", "'Class-2 draw'");
        checkEval("(object .print)", "'Class-1 print'");
        checkEval("(object .toString)", "'Base-1 toString'");

        checkEval("(object (defvar ^.local 23))", "23");
        checkEval("(object .local)", "23");
        checkEval("(object (set ^.local 900))", "900");
        checkEval("(object .local)", "900");

        checkEvalBad("(object (set ^.log 757))");
        checkEval("(object (defvar ^.log 757))", "757");
        checkEval("(Base-2 .log)", "'Base-2 log'");
        checkEval("(object .log)", "757");
    }
}
