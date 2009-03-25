// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.interp;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.genyris.core.Bignum;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.exception.GenyrisException;
import org.genyris.format.BasicFormatter;
import org.genyris.format.Formatter;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.StandardEnvironment;
import org.genyris.io.InStream;
import org.genyris.io.Parser;
import org.genyris.io.StringInStream;
import org.genyris.io.UngettableInStream;

public class EvalApplyTest extends TestCase {

    public void testLambda1() throws Exception {
        Interpreter interp = new Interpreter();
        Environment env = interp.getGlobalEnv();
        Internable table = interp.getSymbolTable();
        InStream input = new UngettableInStream( new StringInStream("((lambda (x) (cons x x)) 23)"));
        Parser parser = new Parser(table, input);
        Exp expression = parser.read();
        Exp result = expression.eval(env);
        StringWriter out = new StringWriter();
        Formatter formatter = new BasicFormatter(out);
        result.acceptVisitor(formatter);
        assertEquals("(23 : 23)", out.getBuffer().toString());
    }

    void excerciseEval(String exp, String expected) throws Exception {
        excerciseEval(exp, expected, "");
    }

    void excerciseEval(String exp, String expected, String exceptionExpected) throws Exception {
        Interpreter interp = new Interpreter();
        Environment env = interp.getGlobalEnv();
        Environment env2 = new StandardEnvironment(env);
        env2.defineVariable(interp.intern("alpha"), new Bignum(23));
        env2.defineVariable(interp.intern("bravo"), new Bignum(45));
        InStream input = new UngettableInStream( new StringInStream(exp));
        Parser parser = new Parser(interp.getSymbolTable(), input);
        Exp expression = parser.read();

            try {
                Exp result;
                result = expression.eval(env2);

                StringWriter out = new StringWriter();
                Formatter formatter = new BasicFormatter(out);
                result.acceptVisitor(formatter);
                assertEquals(expected, out.getBuffer().toString());

            }
            catch (GenyrisException e) {
                assertEquals(exceptionExpected, e.getMessage());
            }

    }

    public void testLambdaVariables() throws Exception {
        excerciseEval("alpha", "23");
        excerciseEval("bravo", "45");
        excerciseEval("(quote alpha)", "alpha");
        excerciseEval("(cons alpha bravo)", "(23 : 45)");
        excerciseEval("(cons alpha (cons bravo nil))", "(23 45)");
        }

    public void testLambdaBuitins() throws Exception {
        excerciseEval("(cons 4 5)", "(4 : 5)");
        excerciseEval("(cons 4 (cons 5 nil))", "(4 5)");
        excerciseEval("(cons 4 nil)", "(4)");
        excerciseEval("(quote 99)", "99");
        excerciseEval("(quote (34)))", "(34)");
        excerciseEval("(quote (foo)))", "(foo)");
        excerciseEval("(car (quote (1 : 2)))", "1");
        excerciseEval("(cdr (quote (1 : 2)))", "2");
        excerciseEval("(rplaca (quote (1 : 2)) 99)", "(99 : 2)");
        excerciseEval("(rplacd (quote (1 : 2)) 100)", "(1 : 100)");
        excerciseEval("(quote (foo)))", "(foo)");
        }

    public void testLambdaArguments() throws Exception {
        excerciseEval("((lambda () (cons 44 44)))", "(44 : 44)");
        excerciseEval("((lambda (x y) (cons x x)) 23)", "(23 : 23)", 
        		"Too few arguments supplied to proc: anonymous lambda");
    }

    public void testLambda2() throws Exception {
        excerciseEval("((lambda () (cons 44 44)))", "(44 : 44)");
        excerciseEval("((lambda (x) (cons x x)) 23)", "(23 : 23)");
        excerciseEval("((lambda (x y) (cons x y)) 5 nil)", "(5)");
        excerciseEval("((lambda (x y) (quote x)) 23 45)", "x");
    }

    public void testLambdaq1() throws Exception {
        excerciseEval("((lambdaq (s) (cons 1 s)) foo)", "(1 : foo)");
        excerciseEval("((lambdaq (s) (cons 1 s)) (cons foo bar))", "(1 cons foo bar)");
    }

    public void testSequence() throws Exception {
        excerciseEval("((lambda (x) (cons x x) (cons 3 4)) 23)", "(3 : 4)");
        excerciseEval("((lambda (x y) (cons x x) (cons y x)) 23 45)", "(45 : 23)");
    }

    public void testDefine() throws Exception {
        excerciseEval("((lambda () (defvar (quote charlie) 99) charlie))", "99");
    }

    public void testSet() throws Exception {
        excerciseEval("(set (quote alpha) 99)", "99");
        excerciseEval("((lambda (x) (set (quote x) 99) (cons x x)) 23)", "(99 : 99)");
    }

    public void testCond() throws Exception {
        excerciseEval("(cond)", "nil");
        excerciseEval("(cond (true 2))", "2");
        excerciseEval("(cond (nil 2))", "nil");
        excerciseEval("(cond (nil (cons 8 9)) (22 (cons 1 2)))", "(1 : 2)");
        excerciseEval("(cond (nil (cons 8 9)) (22 (cons 1 2) 44))", "44");
        }

}
