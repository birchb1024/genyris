package org.genyris.test.dl;

import junit.framework.TestCase;

import org.genyris.core.Bignum;
import org.genyris.core.StrinG;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.Symbol;
import org.genyris.dl.Triple;
import org.genyris.dl.TripleSet;
import org.genyris.exception.GenyrisException;
import org.genyris.test.interp.TestUtilities;

public class TripleSetTest extends TestCase {

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

	public void testBasicTripleSet1() throws GenyrisException {
		TripleSet ts = new TripleSet();
		assertEquals(ts, ts);
		assertEquals(ts.empty(), true);
		TripleSet ts2 = new TripleSet();
		assertTrue(ts.equals(ts2));

		Bignum subject = new Bignum(1);
		ts.add(new Triple(subject, new SimpleSymbol("s"), new StrinG("$")));
		ts2.add(new Triple(new Bignum(1), new SimpleSymbol("s"), new StrinG(
				"$")));
		assertFalse(ts.equals(ts2));

		TripleSet result = ts.select(subject, null, null, null, null);
		assertEquals(ts, result);
	}

	public void testBasicTripleSet2() {
		TripleSet ts = new TripleSet();
		assertEquals(ts, ts);
		assertEquals(ts.empty(), true);

		Bignum subject = new Bignum(12);
		Symbol predicate = new SimpleSymbol("s");
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
        eval("(triple '(2:3) 'b 123)", "(triple (2 : 3) b 123)");
        eval("(triple '(2:3) 'b (dict (!z:99)))", "(triple (2 : 3) b (dict (z : 99)))");
        eval("(triple '(2:3) 'b (list 1 2 3 4 5))", "(triple (2 : 3) b (1 2 3 4 5))");
    }
	public void testInterpStore() throws Exception {
		eval("(null? (member? 'asTriples ((car ((tripleset)!classes))!vars)))","nil");
		eval("(defvar 'ts (tripleset))", "(tripleset)");
		eval("(ts!classes)", "(<class TripleSet (Builtin) ()>)");
		eval("(ts(!add (triple 's 'p 'o)))", "(tripleset)");
		excerciseBadEval("(ts(!add (triple 's 3 'o)))");
		eval("(ts(!select 's nil nil)))", "(tripleset)");
		eval("(equal? ts (ts(!select nil nil nil)))", "true");
		eval("(equal? ts (ts(!select 's nil nil)))", "true");
		eval("(equal? ts (ts(!select 's 'p nil)))", "true");
		eval("(equal? ts (ts(!select 's 'p 'o)))", "true");
		eval("(equal? ts (ts(!select 'X 'p 'o)))", "nil");
		eval("(equal? ts (ts(!select 's 'X 'o)))", "nil");
		eval("(equal? ts (ts(!select 's 'p 'X)))", "nil");
		eval("(ts(!asTriples))", "((triple s p o))");
	}

	public void testInterpCondition() throws Exception {
		eval("(defvar 'isObject99 (lambda (s o p) (equal? p 99)))",
				"<EagerProc: <anonymous lambda>>");
		eval("(defvar 'ts (tripleset))", "(tripleset)");
		eval("(ts(!add (triple 's 'p 'o)))", "(tripleset)");
		eval("(ts(!add (triple 'x 'p 'o)))", "(tripleset)");
		eval("(ts(!add (triple 's 'p 99)))", "(tripleset)");
		eval("(ts(!add (triple 'x 'p 99)))", "(tripleset)");
		eval("(defvar 'result (ts(!select nil nil nil isObject99))))",
				"(tripleset)");
		eval("(defvar 'result (ts(!select 's nil nil isObject99))))",
				"(tripleset)");
		eval("(result(!asTriples))", "((triple s p 99))");
		eval("(defvar 'result (ts(!select 's 'p nil isObject99))))",
				"(tripleset)");
		eval("(result(!asTriples))", "((triple s p 99))");
		eval("(defvar 'result (ts(!select 's 'p 99 isObject99))))",
				"(tripleset)");
		eval("(result(!asTriples))", "nil");
	}

	public void testInterpConditionWithVar() throws Exception {
		eval("(defvar 'ninenine 99)", "99");
		eval("(defvar 'isObject99 (lambda (s p o) (equal? o 99)))",
				"<EagerProc: <anonymous lambda>>");
		eval("(defvar 'ts (tripleset))", "(tripleset)");
		eval("(ts(!add (triple 's 'p 'o)))", "(tripleset)");
		eval("(ts(!add (triple 'x 'p 'o)))", "(tripleset)");
		eval("(ts(!add (triple 's 'p ninenine)))", "(tripleset)");
		eval("(ts(!add (triple 'x 'p ninenine)))", "(tripleset)");
		eval("(defvar 'result (ts(!select 's 'p ninenine isObject99))))",
				"(tripleset)");
		eval("(result(!asTriples))", "((triple s p 99))");
		eval("(print (ts(!asTriples))))","true");
		eval("((SetList!equal?)(ts(!asTriples)) (list (triple 's 'p 'o) (triple 'x 'p 'o) (triple 'x 'p ninenine) (triple 's 'p ninenine)))","true");
	}

	public void testInterpStoreConstruction() throws Exception {
		eval("(defvar 'noop (lambda (&rest args)))",
				"<EagerProc: <anonymous lambda>>");
		eval("(tripleset '(s p o))", "(tripleset)");
		eval("(defvar 'ts (tripleset '(s p o)))", "(tripleset)");
		eval("(ts(!select 's nil nil noop)))", "(tripleset)");
		eval("(ts(!asTriples)))", "((triple s p o))");
	}

	public void testInterpStoreRemove() throws Exception {
		eval("(defvar 'ts (tripleset '(s p o)))", "(tripleset)");
		eval("(ts(!asTriples)))", "((triple s p o))");
		eval("(ts(!remove (triple 's 'p 'o))))", "(tripleset)");
		eval("(ts(!asTriples)))", "nil");
	}

	public void testInterpStoreConstructionMulti() throws Exception {
		eval("(defvar 'noop (lambda (&rest args)))",
				"<EagerProc: <anonymous lambda>>");
		eval("(tripleset '(s p o) '(s b c))", "(tripleset)");
		eval("(defvar 'ts (tripleset '(s p o) '(s b c)))", "(tripleset)");
		eval("(ts(!select 's nil nil noop)))", "(tripleset)");
		eval("(length (ts(!asTriples))))", "2");
		eval("((SetList!equal?) (ts(!asTriples)) (list (tripleq s p o) (tripleq s b c)))","true");
	}

	public void testInterpTripleDict() throws Exception {
		eval(
				"((dict(!a:3)(!b:5))(!asTriples))",
				"((triple (dict (a : 3) (b : 5)) b 5) (triple (dict (a : 3) (b : 5)) a 3))");
		eval("((dict(!a:3)(!b:5))(!asTripleSet))", "(tripleset)");
		eval("(defvar 'ts ((dict(!a:3)(!b:5))(!asTripleSet)))", "(tripleset)");
		eval(
				"(ts(!asTriples))",
				"((triple (dict (a : 3) (b : 5)) a 3) (triple (dict (a : 3) (b : 5)) b 5))");
	}
    public void testInterpTriplesClasses() throws Exception {
        eval("(23(!asTriples))","((triple 23 type <class Bignum (Builtin) ()>))");
        eval("(\"X\"(!asTriples))","((triple \"X\" type <class String (Builtin) ()>))");
        eval("('(a:e)(!asTriples))","((triple (a : e) type <class Pair (Builtin) (ListOfLines PRINTWITHCOLON)>))");
        eval("('sym(!asTriples))","((triple sym type <class SimpleSymbol (Symbol) (|http://www.genyris.org/lang/syntax#Keyword|)>))");
        
        }
    public void testInterpTripleSetClasses() throws Exception {
        eval("((23(!asTripleSet))(!asTriples))","((triple 23 type <class Bignum (Builtin) ()>))");
        eval("((\"X\"(!asTripleSet))(!asTriples))","((triple \"X\" type <class String (Builtin) ()>))");
    }
}
