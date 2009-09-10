package org.genyris.test.dl;

import junit.framework.TestCase;

import org.genyris.core.Bignum;
import org.genyris.core.Exp;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.StrinG;
import org.genyris.dl.Triple;
import org.genyris.dl.TripleStore;
import org.genyris.exception.GenyrisException;
import org.genyris.test.interp.TestUtilities;

public class TripleStoreTest extends TestCase {

    private TestUtilities interpreter;

    protected void setUp() throws Exception {
        super.setUp();
        interpreter = new TestUtilities();
    }

    private void eval(String exp, String expected) throws Exception {
        assertEquals(expected, interpreter.eval(exp));
    }

    private void excerciseBadEval(String exp) {
        try {
            interpreter.eval(exp);
            fail();
        } catch (GenyrisException e) {
        }
    }

    public void testBasicTripleStore1() throws GenyrisException {
        TripleStore ts = new TripleStore();
        assertEquals(ts, ts);
        assertEquals(ts.empty(), true);
        TripleStore ts2 = new TripleStore();
        assertTrue(ts.equals(ts2));

        Bignum subject = new Bignum(1);
        SimpleSymbol predicate = new SimpleSymbol("s");
        Exp object = new StrinG("$");
        ts.add(new Triple(subject, predicate, object));
        assertTrue(ts.contains(subject, predicate, object));

        
        ts2.add(new Triple(new Bignum(1), new SimpleSymbol("s"), new StrinG(
                "$")));
        assertFalse(ts.equals(ts2));

        TripleStore result = ts.select(subject, null, null, null, null);
        assertEquals(ts, result);
    }

    public void testBasicTripleStore2() {
        TripleStore ts = new TripleStore();
        assertEquals(ts, ts);
        assertEquals(ts.empty(), true);

        Bignum subject = new Bignum(12);
        SimpleSymbol predicate = new SimpleSymbol("s");
        StrinG object = new StrinG("$");
        ts.add(new Triple(subject, predicate, object));
        ts.add(new Triple(subject, new SimpleSymbol("s"), new StrinG("$")));

        assertEquals(ts.empty(), false);

        ts.remove(new Triple(subject, predicate, object));

        assertEquals(ts.empty(), false);
    }

    public void testFormatting() throws Exception {
        eval("(triple 'a 'b \"west\")", "(triple a b \"west\")");
        eval("(triple \"X\" 'b \"west\")", "(triple \"X\" b \"west\")");
        eval("(triple \"X\" 'b 123)", "(triple \"X\" b 123)");
        eval("(triple '(2=3) 'b 123)", "(triple (2 = 3) b 123)");
        eval("(triple '(2=3) 'b (dict (.z =99)))", "(triple (2 = 3) b (dict (.z = 99)))");
        eval("(triple '(2=3) 'b (list 1 2 3 4 5))", "(triple (2 = 3) b (1 2 3 4 5))");
    }
    public void testInterpStore() throws Exception {
        eval("(null? (member? 'asTriples ((car ((triplestore).classes)).vars)))","nil");
        eval("(defvar 'ts (triplestore))", "(triplestore)");
        eval("(ts.classes)", "(<class Triplestore (Builtin) ()>)");
        eval("(ts(.add (triple 's 'p 'o)))", "(triplestore)");
        excerciseBadEval("(ts(.add (triple 's 3 'o)))");
        eval("(ts(.select 's nil nil)))", "(triplestore)");
        eval("(equal? ts (ts(.select nil nil nil)))", "true");
        eval("(equal? ts (ts(.select 's nil nil)))", "true");
        eval("(equal? ts (ts(.select 's 'p nil)))", "true");
        eval("(equal? ts (ts(.select 's 'p 'o)))", "true");
        eval("(equal? ts (ts(.select 'X 'p 'o)))", "nil");
        eval("(equal? ts (ts(.select 's 'X 'o)))", "nil");
        eval("(equal? ts (ts(.select 's 'p 'X)))", "nil");
        eval("(ts(.asTriples))", "((triple s p o))");
    }

    public void testInterpStoreMulti() throws Exception {
        eval("(null? (member? 'asTriples ((car ((triplestore).classes)).vars)))","nil");
        eval("(defvar 'ts (triplestore))", "(triplestore)");
        eval("(ts.classes)", "(<class Triplestore (Builtin) ()>)");
        eval("(ts(.add (triple 's 'p 'o1)))", "(triplestore)");
        eval("(ts(.add (triple 's 'p 'o2)))", "(triplestore)");
        eval("(ts(.add (triple 'x 'p 'z)))", "(triplestore)");
        excerciseBadEval("(ts(.add (triple 's 3 'o)))");
        eval("(ts(.select 's nil nil)))", "(triplestore)");
        eval("(equal? ts (ts(.select nil nil nil)))", "true");
        eval("(equal? ts (ts(.select 's nil nil)))", "nil");
        eval("(equal? ts (ts(.select 's 'p nil)))", "nil");
        eval("(equal? ts (ts(.select 's 'p 'o1)))", "nil");
        eval("(equal? ts (ts(.select 'X 'p 'o)))", "nil");
        eval("(equal? ts (ts(.select 's 'X 'o)))", "nil");
        eval("(equal? ts (ts(.select 's 'p 'X)))", "nil");
        eval("(ts(.asTriples))", "((triple x p z) (triple s p o2) (triple s p o1))");
        eval("((ts(.select 's nil nil))(.asTriples))", "((triple s p o2) (triple s p o1))");
        eval("((ts(.select 's 'p nil))(.asTriples))", "((triple s p o2) (triple s p o1))");
        eval("((ts(.select 's 'p 'o1))(.asTriples))", "((triple s p o1))");
        eval("((ts(.select 's 'p 'o2))(.asTriples))", "((triple s p o2))");
        eval("((ts(.select nil 'p 'o2))(.asTriples))", "((triple s p o2))");
        eval("((ts(.select nil nil 'o2))(.asTriples))", "((triple s p o2))");
        eval("((ts(.select nil 'p nil))(.asTriples))", "((triple x p z) (triple s p o2) (triple s p o1))");
    }

    public void testInterpCondition() throws Exception {
        eval("(defvar 'isObject99 (lambda (s o p) (equal? p 99)))",
                "<EagerProc: <anonymous lambda>>");
        eval("(defvar 'ts (triplestore))", "(triplestore)");
        eval("(ts(.add (triple 's 'p 'o)))", "(triplestore)");
        eval("(ts(.add (triple 's 'p 'o2)))", "(triplestore)");
        eval("(ts(.add (triple 'x 'p 'o)))", "(triplestore)");
        eval("(ts(.add (triple 's 'p 99)))", "(triplestore)");
        eval("(ts(.add (triple 'x 'p 99)))", "(triplestore)");
        eval("(defvar 'result (ts(.select nil nil nil isObject99))))",
                "(triplestore)");
        eval("(defvar 'result (ts(.select 's nil nil isObject99))))",
                "(triplestore)");
        eval("(result(.asTriples))", "((triple s p 99))");
        eval("(defvar 'result (ts(.select 's 'p nil isObject99))))",
                "(triplestore)");
        eval("(result(.asTriples))", "((triple s p 99))");
        eval("(defvar 'result (ts(.select 's 'p 99 isObject99))))",
                "(triplestore)");
        eval("(result(.asTriples))", "nil");
    }

    public void testInterpConditionWithVar() throws Exception {
        eval("(defvar 'ninenine 99)", "99");
        eval("(defvar 'isObject99 (lambda (s p o) (equal? o 99)))",
                "<EagerProc: <anonymous lambda>>");
        eval("(defvar 'ts (triplestore))", "(triplestore)");
        eval("(ts(.add (triple 's 'p 'o)))", "(triplestore)");
        eval("(ts(.add (triple 'x 'p 'o)))", "(triplestore)");
        eval("(ts(.add (triple 's 'p ninenine)))", "(triplestore)");
        eval("(ts(.add (triple 'x 'p ninenine)))", "(triplestore)");
        eval("(defvar 'result (ts(.select 's 'p ninenine isObject99))))",
                "(triplestore)");
        eval("(result(.asTriples))", "((triple s p 99))");
        eval("(print (ts(.asTriples))))","true");
        eval("((SetList.equal?)(ts(.asTriples)) (list (triple 's 'p 'o) (triple 'x 'p 'o) (triple 'x 'p ninenine) (triple 's 'p ninenine)))","true");
    }

    public void testInterpStoreConstruction() throws Exception {
        eval("(defvar 'noop (lambda (&rest args)))",
                "<EagerProc: <anonymous lambda>>");
        eval("(triplestore '(s p o))", "(triplestore)");
        eval("(defvar 'ts (triplestore '(s p o)))", "(triplestore)");
        eval("(ts(.select 's nil nil noop)))", "(triplestore)");
        eval("(ts(.asTriples)))", "((triple s p o))");
    }

    public void testInterpStoreRemove() throws Exception {
        eval("(defvar 'ts (triplestore '(s p o)))", "(triplestore)");
        eval("(ts(.asTriples)))", "((triple s p o))");
        eval("(ts(.remove (triple 's 'p 'o))))", "(triplestore)");
        eval("(ts(.asTriples)))", "nil");
    }

    public void testInterpStoreConstructionMulti() throws Exception {
        eval("(defvar 'noop (lambda (&rest args)))",
                "<EagerProc: <anonymous lambda>>");
        eval("(triplestore '(s p o) '(s b c))", "(triplestore)");
        eval("(defvar 'ts (triplestore '(s p o) '(s b c)))", "(triplestore)");
        eval("(ts(.select 's nil nil noop)))", "(triplestore)");
        eval("(length (ts(.asTriples))))", "2");
        eval("((SetList.equal?) (ts(.asTriples)) (list (tripleq s p o) (tripleq s b c)))","true");
    }

    public void testInterpTripleDict() throws Exception {
        eval(
                "((dict(.a = 3)(.b = 5))(.asTriples))",
                "((triple (dict (.a = 3) (.b = 5)) b 5) (triple (dict (.a = 3) (.b = 5)) a 3))");
        eval("(defvar 'thedict (dict(.a =3)(.b =5)))", "(dict (.a = 3) (.b = 5))");
        eval("(thedict(.asTripleStore))", "(triplestore)");
        eval("(defvar 'ts (thedict(.asTripleStore)))", "(triplestore)");
        eval("((SetList.equal?) (ts(.asTriples)) (list (triple thedict 'a 3) (triple thedict 'b 5)))", "true");
    }
    public void testInterpTriplesClasses() throws Exception {
        eval("(23(.asTriples))","((triple 23 type <class Bignum (Builtin) ()>))");
        eval("(\"X\"(.asTriples))","((triple \"X\" type <class String (Builtin) ()>))");
        eval("('(a =e)(.asTriples))","((triple (a = e) type <class PairEqual (Builtin) ()>))");
        eval("('sym(.asTriples))","((triple sym type <class SimpleSymbol (Symbol) (|http://www.genyris.org/lang/syntax#Keyword|)>))");

        }
    public void testInterpTripleStoreClasses() throws Exception {
        eval("((23(.asTripleStore))(.asTriples))","((triple 23 type <class Bignum (Builtin) ()>))");
        eval("((\"X\"(.asTripleStore))(.asTriples))","((triple \"X\" type <class String (Builtin) ()>))");
    }
}
