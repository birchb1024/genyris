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
        eval("(defvar '$global 999)");
        checkEval("$global", "999");

        eval("(defvar 'Standard-Class (dict (_classname : 'Standard-Class)))");
        checkEval("Standard-Class", "(dict (_classname : Standard-Class))");
        checkEval("(defvar 'Account (dict (_classes : (list Standard-Class)) (_print : (lambda () (cons $global _balance))) ))",
                "(dict (_print : <EagerProc: <org.genyris.interp.ClassicFunction>>))");

        checkEval("(Account " +
            "(defvar '_new " +
                "(lambda (initial-balance) " +
                "(dict " +
                    "(_classes: (cons Account nil)) " +
                    "(_balance:  initial-balance))))) ",
                    "<EagerProc: <org.genyris.interp.ClassicFunction>>" );

        checkEval("(defvar 'bb  (Account (_new 1000)))"
                ,"(dict (_balance : 1000))");

        checkEval("(bb(_print))", "(999 : 1000)");

        checkEval("(bb((lambda () _balance)))", "1000");
    }


    public void testInheritance() throws GenyrisException {

        eval("(defvar 'Standard-Class (dict))");

        checkEval("(defvar 'Base-1 " +
            "(dict " +
                "(_classes: (cons Standard-Class nil)) " +
                "(_toString: \"Base-1 toString\"))) "
                , "(dict (_toString : \"Base-1 toString\"))" );

        checkEval("(Base-1 _toString)", "\"Base-1 toString\"");

        checkEval("(defvar 'Base-2 " +
            "(dict " +
                "(_classes : (cons Standard-Class nil)) " +
                "(_log : \"Base-2 log\"))) ",
                "(dict (_log : \"Base-2 log\"))");

        checkEval("(Base-2 _log)", "\"Base-2 log\"");

        eval("(defvar 'Class-1 " +
                "(dict " +
                    "(_classes : (cons Standard-Class nil)) " +
                    "(_superclasses : (cons Base-1 nil)) " +
                    "(_print: \"Class-1 print\")"  +
                    "(_new: " +
                        "(lambda (_a) " +
                            "(dict " +
                                "(_classes: (cons Class-1 nil)) " +
                                "(_a: _a)))))) " );
        checkEval("(Class-1 _print)", "\"Class-1 print\"");
        checkEval("(Class-1 _toString)", "\"Base-1 toString\"");

        eval("(defvar 'Class-2 " +
            "(dict" +
                "(_classes: (cons Standard-Class nil))" +
                "(_superclasses: (cons Base-2 nil))" +
                "(_draw: \"Class-2 draw\")))" );

        checkEval("(Class-2 _draw)", "\"Class-2 draw\"");
        checkEval("(Class-2 _log)", "\"Base-2 log\"");

        eval("(defvar 'object " +
                "(dict" +
                "(_classes : (cons Class-1 (cons Class-2 nil)))))" );
        checkEval("(object _log)", "\"Base-2 log\"");
        checkEval("(object _draw)", "\"Class-2 draw\"");
        checkEval("(object _print)", "\"Class-1 print\"");
        checkEval("(object _toString)", "\"Base-1 toString\"");

        checkEval("(object (defvar '_local 23))", "23");
        checkEval("(object _local)", "23");
        checkEval("(object (set '_local 900))", "900");
        checkEval("(object _local)", "900");

        checkEvalBad("(object (set '_log 757))");
        checkEval("(object (defvar '_log 757))", "757");
        checkEval("(Base-2 _log)", "\"Base-2 log\"");
        checkEval("(object _log)", "757");
    }
}
