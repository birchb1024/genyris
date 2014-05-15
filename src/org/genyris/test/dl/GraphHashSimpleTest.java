package org.genyris.test.dl;

import junit.framework.TestCase;

import org.genyris.core.Bignum;
import org.genyris.core.Exp;
import org.genyris.core.NilSymbol;
import org.genyris.core.Pair;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.StrinG;
import org.genyris.dl.AbstractGraph;
import org.genyris.dl.Triple;
import org.genyris.dl.GraphHashSimple;
import org.genyris.exception.GenyrisException;
import org.genyris.test.interp.TestUtilities;

public class GraphHashSimpleTest extends TestCase {

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

	public void testBasicGraph1() throws GenyrisException {
		GraphHashSimple ts = new GraphHashSimple();
		assertEquals(ts, ts);
		assertEquals(ts.empty(), true);
		AbstractGraph ts2 = new GraphHashSimple();
		assertTrue(ts.equals(ts2));

		Bignum subject = new Bignum(1);
		SimpleSymbol predicate = new SimpleSymbol("s");
		Exp object = new StrinG("$");
		ts.add(new Triple(subject, predicate, object));
		assertTrue(ts.contains(subject, predicate, object));

		ts2.add(new Triple(new Bignum(1), new SimpleSymbol("s"),
				new StrinG("$")));
		assertFalse(ts.equals(ts2));

		AbstractGraph result = ts.select(subject, null, null, null, null);
		assertEquals(ts, result);
	}

	public void testBasicGraph2() {
		AbstractGraph ts = new GraphHashSimple();
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

	public void testGetOK() {
		AbstractGraph ts = new GraphHashSimple();
		assertEquals(ts, ts);
		assertEquals(ts.empty(), true);

		Bignum subject = new Bignum(12);
		SimpleSymbol predicate = new SimpleSymbol("s");
		StrinG object = new StrinG("$");
		ts.add(new Triple(subject, predicate, object));
		ts.add(new Triple(subject, new SimpleSymbol("s"), new StrinG("$")));

		assertEquals(ts.empty(), false);

		try {
			assertEquals(ts.get(subject, predicate), object);
		} catch (GenyrisException e) {
			fail();
		}

		assertEquals(ts.empty(), false);
	}

	public void testGetNone() {
		AbstractGraph ts = new GraphHashSimple();
		assertEquals(ts, ts);
		assertEquals(ts.empty(), true);

		Bignum subject = new Bignum(12);
		SimpleSymbol predicate = new SimpleSymbol("s");
		try {
			ts.get(subject, predicate);
			fail();
		} catch (GenyrisException e) {
		}
	}

	public void testGetTooMany() {
		AbstractGraph ts = new GraphHashSimple();
		assertEquals(ts, ts);
		assertEquals(ts.empty(), true);

		Bignum subject = new Bignum(12);
		SimpleSymbol predicate = new SimpleSymbol("s");
		StrinG object = new StrinG("$1");
		ts.add(new Triple(subject, predicate, object));
		ts.add(new Triple(subject, predicate, new StrinG("$2")));
		ts.add(new Triple(subject, new SimpleSymbol("s"), new StrinG("$")));

		assertEquals(ts.empty(), false);

		try {
			ts.get(subject, predicate);
			fail();
		} catch (GenyrisException e) {
		}

		assertEquals(ts.empty(), false);
	}

	public void testGetListOne() {
		Exp NIL = new NilSymbol();
		AbstractGraph ts = new GraphHashSimple();
		assertEquals(ts, ts);
		assertEquals(ts.empty(), true);

		Bignum subject = new Bignum(12);
		SimpleSymbol predicate = new SimpleSymbol("s");
		StrinG object = new StrinG("$");
		ts.add(new Triple(subject, predicate, object));
		assertEquals(ts.getList(subject, predicate, NIL), new Pair(object, NIL));
	}

	public void testGetListNone() {
		Exp NIL = new NilSymbol();
		AbstractGraph ts = new GraphHashSimple();
		assertEquals(ts, ts);
		assertEquals(ts.empty(), true);

		Bignum subject = new Bignum(12);
		SimpleSymbol predicate = new SimpleSymbol("s");
		StrinG object = new StrinG("$");
		ts.add(new Triple(subject, predicate, object));
		assertEquals(ts.getList(new SimpleSymbol("s2"), predicate, NIL), NIL);
	}

	public void testGetListMany() {
		Exp NIL = new NilSymbol();
		AbstractGraph ts = new GraphHashSimple();
		assertEquals(ts, ts);
		assertEquals(ts.empty(), true);

		Bignum subject = new Bignum(12);
		SimpleSymbol predicate = new SimpleSymbol("s");
		StrinG object1 = new StrinG("$1");
		StrinG object2 = new StrinG("$2");
		ts.add(new Triple(subject, predicate, object1));
		ts.add(new Triple(subject, predicate, object2));
		if (ts.getList(subject, predicate, NIL).equals(
				new Pair(object1, new Pair(object2, NIL)))
				|| ts.getList(subject, predicate, NIL).equals(
						new Pair(object2, new Pair(object1, NIL)))) {
			;
		} else {
			fail();
		}
	}

	public void testPutOK() {
		AbstractGraph ts = new GraphHashSimple();
		assertEquals(ts, ts);
		assertEquals(ts.empty(), true);

		Bignum subject = new Bignum(12);
		SimpleSymbol predicate = new SimpleSymbol("s");
		StrinG object1 = new StrinG("$1");
		StrinG object2 = new StrinG("$2");

		ts.add(new Triple(subject, predicate, object1));
		ts.add(new Triple(subject, predicate, object1));
		ts.add(new Triple(subject, predicate, object2));
		ts.add(new Triple(subject, predicate, object2));
		ts.put(subject, predicate, object2);
		ts.put(subject, predicate, object2);
		try {
			assertEquals(ts.get(subject, predicate), object2);
			assertEquals(ts.length(), 1);
		} catch (GenyrisException e) {
			fail();
		}

	}

	public void testPutWasEmpty() {
		AbstractGraph ts = new GraphHashSimple();
		assertEquals(ts, ts);
		assertEquals(ts.empty(), true);

		Bignum subject = new Bignum(12);
		SimpleSymbol predicate = new SimpleSymbol("s");
		StrinG object = new StrinG("$2");

		ts.put(subject, predicate, object);
		try {
			assertEquals(ts.get(subject, predicate), object);
			assertEquals(ts.length(), 1);
		} catch (GenyrisException e) {
			fail();
		}

	}

	public void testFormatting() throws Exception {
		eval("(triple ^a ^b 'west')", "(triple a b 'west')");
		eval("(triple 'X' ^b 'west')", "(triple 'X' b 'west')");
		eval("(triple 'X' ^b 123)", "(triple 'X' b 123)");
		eval("(triple ^(2=3) ^b 123)", "(triple (2 = 3) b 123)");
		eval("(triple ^(2=3) ^b (dict (.z =99)))",
				"(triple (2 = 3) b (dict (.z = 99)))");
		eval("(triple ^(2=3) ^b (list 1 2 3 4 5))",
				"(triple (2 = 3) b (1 2 3 4 5))");
	}

	public void testInterpStore() throws Exception {
		eval(
				"(null? (member? ^.asTriples ((car ((graph).classes)).vars)))",
				"nil");
		eval("(defvar ^ts (graph))", "(graph)");
		eval("(ts.classes)", "(<class Graph (Builtin)>)");
		eval("(ts(.add (triple ^s ^p ^o)))", "(graph)");
		excerciseBadEval("(ts(.add (triple ^s 3 ^o)))");
		eval("(ts(.select ^s nil nil)))", "(graph)");
		eval("(equal? ts (ts(.select nil nil nil)))", "true");
		eval("(equal? ts (ts(.select ^s nil nil)))", "true");
		eval("(equal? ts (ts(.select ^s ^p nil)))", "true");
		eval("(equal? ts (ts(.select ^s ^p ^o)))", "true");
		eval("(equal? ts (ts(.select ^X ^p ^o)))", "nil");
		eval("(equal? ts (ts(.select ^s ^X ^o)))", "nil");
		eval("(equal? ts (ts(.select ^s ^p ^X)))", "nil");
		eval("(ts(.asTriples))", "((triple s p o))");
	}

	public void testInterpStoreMulti() throws Exception {
		eval(
				"(null? (member? ^.asTriples ((car ((graph).classes)).vars)))",
				"nil");
		eval("(defvar ^ts (graph))", "(graph)");
		eval("(ts.classes)", "(<class Graph (Builtin)>)");
		eval("(ts(.add (triple ^s ^p ^o1)))", "(graph)");
		eval("(ts(.add (triple ^s ^p ^o2)))", "(graph)");
		eval("(ts(.add (triple ^x ^p ^z)))", "(graph)");
		excerciseBadEval("(ts(.add (triple ^s 3 ^o)))");
		eval("(ts(.select ^s nil nil)))", "(graph)");
		eval("(equal? ts (ts(.select nil nil nil)))", "true");
		eval("(equal? ts (ts(.select ^s nil nil)))", "nil");
		eval("(equal? ts (ts(.select ^s ^p nil)))", "nil");
		eval("(equal? ts (ts(.select ^s ^p ^o1)))", "nil");
		eval("(equal? ts (ts(.select ^X ^p ^o)))", "nil");
		eval("(equal? ts (ts(.select ^s ^X ^o)))", "nil");
		eval("(equal? ts (ts(.select ^s ^p ^X)))", "nil");
		eval("(ts(.asTriples))",
				"((triple x p z) (triple s p o2) (triple s p o1))");
		eval("((ts(.select ^s nil nil))(.asTriples))",
				"((triple s p o2) (triple s p o1))");
		eval("((ts(.select ^s ^p nil))(.asTriples))",
				"((triple s p o2) (triple s p o1))");
		eval("((ts(.select ^s ^p ^o1))(.asTriples))", "((triple s p o1))");
		eval("((ts(.select ^s ^p ^o2))(.asTriples))", "((triple s p o2))");
		eval("((ts(.select nil ^p ^o2))(.asTriples))", "((triple s p o2))");
		eval("((ts(.select nil nil ^o2))(.asTriples))", "((triple s p o2))");
		eval("((ts(.select nil ^p nil))(.asTriples))",
				"((triple x p z) (triple s p o2) (triple s p o1))");
	}

	public void testInterpEquals1() throws Exception {
		eval("(defvar ^ts1 (graph))", "(graph)");
		eval("(ts1(.add (triple ^s ^p ^o)))", "(graph)");
		eval("(ts1(.add (triple ^s ^p ^o2)))", "(graph)");
		eval("(ts1(.add (triple ^x ^p ^o)))", "(graph)");
		eval("(ts1(.add (triple ^s ^p 99)))", "(graph)");
		eval("(ts1(.add (triple ^x ^p 99)))", "(graph)");

		eval("(defvar ^ts2 (graph))", "(graph)");
		eval("(ts2(.add (triple ^s ^p ^o)))", "(graph)");
		eval("(ts2(.add (triple ^s ^p ^o2)))", "(graph)");
		eval("(ts2(.add (triple ^x ^p ^o)))", "(graph)");
		eval("(ts2(.add (triple ^s ^p 99)))", "(graph)");
		eval("(ts2(.add (triple ^x ^p 99)))", "(graph)");

		eval("(equal? ts1 ts2)", "true");
		eval("(equal? ts2 ts1)", "true");
	}

	public void testInterpEquals2() throws Exception {
		eval("(defvar ^ts1 (graph))", "(graph)");
		eval("(ts1(.add (triple ^s ^p ^o)))", "(graph)");
		eval("(ts1(.add (triple ^s ^p ^o2)))", "(graph)");
		eval("(ts1(.add (triple ^x ^p ^o)))", "(graph)");
		eval("(ts1(.add (triple ^s ^p 99)))", "(graph)");
		eval("(ts1(.add (triple ^x ^p 99)))", "(graph)");

		eval("(defvar ^ts2 (graph))", "(graph)");
		eval("(ts2(.add (triple ^s ^p ^o)))", "(graph)");
		eval("(ts2(.add (triple ^s ^p ^o2)))", "(graph)");
		eval("(ts2(.add (triple ^x ^p ^o)))", "(graph)");
		eval("(ts2(.add (triple ^s ^p 99)))", "(graph)");
		eval("(ts2(.add (triple ^x ^p 99)))", "(graph)");

		eval("(equal? ts1 ts2)", "true");
		eval("(equal? ts2 ts1)", "true");
	}

	public void testInterpEquals3() throws Exception {
		eval("(defvar ^ts1 (graph))", "(graph)");
		eval("(defvar ^ts2 (graph))", "(graph)");
		eval("(equal? ts1 ts2)", "true");
		eval("(equal? ts2 ts1)", "true");
	}

	public void testInterpEquals4() throws Exception {
		eval("(defvar ^ts1 (graph))", "(graph)");
		eval("(ts1(.add (triple ^s ^p ^o)))", "(graph)");
		eval("(ts1(.add (triple ^s ^p ^o2)))", "(graph)");
		eval("(ts1(.add (triple ^x ^p ^o)))", "(graph)");
		eval("(ts1(.add (triple ^s ^p 99)))", "(graph)");

		eval("(defvar ^ts2 (graph))", "(graph)");
		eval("(ts2(.add (triple ^s ^p ^o)))", "(graph)");
		eval("(ts2(.add (triple ^s ^p ^o2)))", "(graph)");
		eval("(ts2(.add (triple ^x ^p ^o)))", "(graph)");
		eval("(ts2(.add (triple ^s ^p 99)))", "(graph)");
		eval("(ts2(.add (triple ^x ^p 99)))", "(graph)");

		eval("(equal? ts1 ts2)", "nil");
		eval("(equal? ts2 ts1)", "nil");
	}

	public void testInterpEquals5() throws Exception {
		eval("(defvar ^ts1 (graph))", "(graph)");
		eval("(ts1(.add (triple ^s ^p ^o)))", "(graph)");

		eval("(defvar ^ts2 (graph))", "(graph)");

		eval("(equal? ts1 ts2)", "nil");
		eval("(equal? ts2 ts1)", "nil");
	}

	public void testInterpCondition() throws Exception {
		eval("(defvar ^isObject99 (lambda (s o p) (equal? p 99)))",
				"<EagerProc: <anonymous lambda>>");
		eval("(defvar ^ts (graph))", "(graph)");
		eval("(ts(.add (triple ^s ^p ^o)))", "(graph)");
		eval("(ts(.add (triple ^s ^p ^o2)))", "(graph)");
		eval("(ts(.add (triple ^x ^p ^o)))", "(graph)");
		eval("(ts(.add (triple ^s ^p 99)))", "(graph)");
		eval("(ts(.add (triple ^x ^p 99)))", "(graph)");
		eval("(defvar ^result (ts(.select nil nil nil isObject99))))",
				"(graph)");
		eval("(defvar ^result (ts(.select ^s nil nil isObject99))))",
				"(graph)");
		eval("(result(.asTriples))", "((triple s p 99))");
		eval("(defvar ^result (ts(.select ^s ^p nil isObject99))))",
				"(graph)");
		eval("(result(.asTriples))", "((triple s p 99))");
		eval("(defvar ^result (ts(.select ^s ^p 99 isObject99))))",
				"(graph)");
		eval("(result(.asTriples))", "((triple s p 99))");
	}

	public void testInterpConditionWithVar() throws Exception {
		eval("(defvar ^ninenine 99)", "99");
		eval("(defvar ^isObject99 (lambda (s p o) (equal? o 99)))",
				"<EagerProc: <anonymous lambda>>");
		eval("(defvar ^ts (graph))", "(graph)");
		eval("(ts(.add (triple ^s ^p ^o)))", "(graph)");
		eval("(ts(.add (triple ^x ^p ^o)))", "(graph)");
		eval("(ts(.add (triple ^s ^p ninenine)))", "(graph)");
		eval("(ts(.add (triple ^x ^p ninenine)))", "(graph)");
		eval("(defvar ^result (ts(.select ^s ^p ninenine isObject99))))",
				"(graph)");
		eval("(result(.asTriples))", "((triple s p 99))");
		eval(
				"((SetList.equal?)(ts(.asTriples)) (list (triple ^s ^p ^o) (triple ^x ^p ^o) (triple ^x ^p ninenine) (triple ^s ^p ninenine)))",
				"true");
	}

	public void testInterpStoreConstruction() throws Exception {
		eval("(defvar ^noop (lambda (&rest args)))",
				"<EagerProc: <anonymous lambda>>");
		eval("(graph ^(s p o))", "(graph)");
		eval("(defvar ^ts (graph ^(s p o)))", "(graph)");
		eval("(ts(.select ^s nil nil noop)))", "(graph)");
		eval("(ts(.asTriples)))", "((triple s p o))");
	}

	public void testInterpStoreRemove() throws Exception {
		eval("(defvar ^ts (graph ^(s p o)))", "(graph)");
		eval("(ts(.asTriples)))", "((triple s p o))");
		eval("(ts(.remove (triple ^s ^p ^o))))", "(graph)");
		eval("(ts(.asTriples)))", "nil");
	}

	public void testInterpStoreConstructionMulti() throws Exception {
		eval("(defvar ^noop (lambda (&rest args)))",
				"<EagerProc: <anonymous lambda>>");
		eval("(graph ^(s p o) ^(s b c))", "(graph)");
		eval("(defvar ^ts (graph ^(s p o) ^(s b c)))", "(graph)");
		eval("(ts(.select ^s nil nil noop)))", "(graph)");
		eval("(length (ts(.asTriples))))", "2");
		eval(
				"((SetList.equal?) (ts(.asTriples)) (list (tripleq s p o) (tripleq s b c)))",
				"true");
	}

	public void testInterpTripleDict() throws Exception {
		eval("((dict(.a = 3)(.b = 5))(.asTriples))",
				"((triple (dict (.a = 3) (.b = 5)) b 5) (triple (dict (.a = 3) (.b = 5)) a 3))");
		eval("(defvar ^thedict (dict(.a =3)(.b =5)))",
				"(dict (.a = 3) (.b = 5))");
		eval("(thedict(.asGraph))", "(graph)");
		eval("(defvar ^ts (thedict(.asGraph)))", "(graph)");
		eval(
				"((SetList.equal?) (ts(.asTriples)) (list (triple thedict ^a 3) (triple thedict ^b 5)))",
				"true");
	}

	public void testInterpTriplesClasses() throws Exception {
		eval("(23(.asTriples))", "((triple 23 type <class Bignum (Builtin)>))");
		eval("('X'(.asTriples))",
				"((triple 'X' type <class String (Builtin)>))");
		eval("(^(a =e)(.asTriples))",
				"((triple (a = e) type <class PairEqual (Pair)>))");
		eval("(^sym(.asTriples))",
				"((triple sym type <class SimpleSymbol (Symbol)>))");

	}

	public void testInterpGraphClasses() throws Exception {
		eval("((23(.asGraph))(.asTriples))",
				"((triple 23 type <class Bignum (Builtin)>))");
		eval("(('X'(.asGraph))(.asTriples))",
				"((triple 'X' type <class String (Builtin)>))");
	}
}
