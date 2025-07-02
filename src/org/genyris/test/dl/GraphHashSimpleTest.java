package org.genyris.test.dl;

import junit.framework.TestCase;

import org.genyris.core.*;
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

	private void AssertInterpretEquals(String input, String expectedOutput) throws Exception {
		assertEquals(expectedOutput, interpreter.eval(input));
	}

	private void BadAssertInterpretEquals(String exp) {
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

		Symbol subject = new SimpleSymbol("1".toString());
		SimpleSymbol predicate = new SimpleSymbol("s");
		Exp object = new StrinG("$");
		ts.add(new Triple(subject, predicate, object));
		assertTrue(ts.contains(subject, predicate, object) != null);

		ts2.add(new Triple(new SimpleSymbol("1"), new SimpleSymbol("s"),
				new StrinG("$")));
		assertFalse(ts.equals(ts2));

		AbstractGraph result = ts.select(subject, null, null, null, null);
		assertEquals(ts, result);
	}

	public void testBasicGraph2() {
		AbstractGraph ts = new GraphHashSimple();
		assertEquals(ts, ts);
		assertEquals(ts.empty(), true);

		Symbol S1 = new SimpleSymbol("12");
		SimpleSymbol P1 = new SimpleSymbol("s");
		StrinG O1 = new StrinG("$");

		ts.add(new Triple(S1, P1, O1));
		assertEquals(ts.length(),1);
		assertEquals(ts.empty(), false);

		ts.add(new Triple(S1, P1, O1));
		assertEquals(1, ts.length());
		assertEquals(false, ts.empty());

		Symbol P2 = new SimpleSymbol("s");
		Exp O2 = new StrinG("$");
		ts.add(new Triple(S1, P2, O2));
		assertEquals(2, ts.length());
		assertEquals(false, ts.empty());

		ts.remove(new Triple(S1, P1, O1));
		assertEquals(1, ts.length());
		assertEquals(false, ts.empty());

		ts.remove(new Triple(S1, P2, O2));
		assertEquals(0, ts.length());
		assertEquals(true, ts.empty());

	}

	public void testGetOK() {
		AbstractGraph ts = new GraphHashSimple();
		assertEquals(ts, ts);
		assertEquals(ts.empty(), true);

		SimpleSymbol S1 = new SimpleSymbol("12");
		SimpleSymbol P1 = new SimpleSymbol("s");
		StrinG O1 = new StrinG("$");
		ts.add(new Triple(S1, P1, O1));
		ts.add(new Triple(S1, new SimpleSymbol("s"), new StrinG("$")));

		assertEquals(ts.empty(), false);

		try {
			assertEquals(ts.get(S1, P1), O1);
		} catch (GenyrisException e) {
			fail();
		}

		assertEquals(ts.empty(), false);
	}

	public void testGetNone() {
		AbstractGraph ts = new GraphHashSimple();
		assertEquals(ts, ts);
		assertEquals(ts.empty(), true);

		SimpleSymbol subject = new SimpleSymbol(12);
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

		SimpleSymbol subject = new SimpleSymbol(12);
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

		Symbol subject = new SimpleSymbol("12");
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

		SimpleSymbol subject = new SimpleSymbol(12);
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

		Symbol subject = new SimpleSymbol("12");
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

		SimpleSymbol subject = new SimpleSymbol(12);
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

		SimpleSymbol subject = new SimpleSymbol(12);
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
		AssertInterpretEquals("(triple ^a ^b 'west')", "(triple a b 'west')");
		AssertInterpretEquals("(triple 'X' ^b 'west')", "(triple X b 'west')");
		AssertInterpretEquals("(triple 'X' ^b 123)", "(triple X b 123)");
		BadAssertInterpretEquals("(triple ^(2=3) ^b 123)");
		BadAssertInterpretEquals("(triple ^(2=3) ^b (dict (.z =99)))");
		BadAssertInterpretEquals("(triple ^(2=3) ^b (list 1 2 3 4 5))");
	}

	public void testInterpStore() throws Exception {
		AssertInterpretEquals(
				"(null? (member? ^.asTriples ((car ((graph).classes)).vars)))","nil");
		AssertInterpretEquals("(defvar ^ts (graph))", "(graph)");
		AssertInterpretEquals("(ts.classes)", "(<class Graph (Builtin)>)");
		AssertInterpretEquals("(ts(.add (triple ^s ^p ^o)))", "(graph)");
		AssertInterpretEquals("(ts(.add (triple ^s 3 ^o)))", "(graph)");
		AssertInterpretEquals("(ts(.asTriples))", "((triple s 3 o) (triple s p o))");

		AssertInterpretEquals("(ts(.select ^s nil nil)))", "(graph)");
		AssertInterpretEquals("(equal? ts (ts(.select nil nil nil)))", "true");
		AssertInterpretEquals("(equal? 2 ((ts(.select ^s nil nil))(.length)))", "true");
		AssertInterpretEquals("(equal? 1 ((ts(.select ^s ^p nil))(.length)))", "true");
		AssertInterpretEquals("(equal? ts (ts(.select ^s ^p ^o)))", "nil");
		AssertInterpretEquals("(equal? ts (ts(.select ^X ^p ^o)))", "nil");
		AssertInterpretEquals("(equal? ts (ts(.select ^s ^X ^o)))", "nil");
		AssertInterpretEquals("(equal? ts (ts(.select ^s ^p ^X)))", "nil");
	}

	public void testInterpStoreMulti() throws Exception {
		AssertInterpretEquals(
				"(null? (member? ^.asTriples ((car ((graph).classes)).vars)))",
				"nil");
		AssertInterpretEquals("(defvar ^ts (graph))", "(graph)");
		AssertInterpretEquals("(ts.classes)", "(<class Graph (Builtin)>)");
		AssertInterpretEquals("(ts(.add (triple ^s ^p ^o1)))", "(graph)");
		AssertInterpretEquals("(ts(.add (triple ^s ^p ^o2)))", "(graph)");
		AssertInterpretEquals("(ts(.add (triple ^x ^p ^z)))", "(graph)");
		BadAssertInterpretEquals("(ts(.add (triple ^s ^(1 = 2) ^o)))");
		AssertInterpretEquals("(ts(.select ^s nil nil)))", "(graph)");
		AssertInterpretEquals("(equal? ts (ts(.select nil nil nil)))", "true");
		AssertInterpretEquals("(equal? ts (ts(.select ^s nil nil)))", "nil");
		AssertInterpretEquals("(equal? ts (ts(.select ^s ^p nil)))", "nil");
		AssertInterpretEquals("(equal? ts (ts(.select ^s ^p ^o1)))", "nil");
		AssertInterpretEquals("(equal? ts (ts(.select ^X ^p ^o)))", "nil");
		AssertInterpretEquals("(equal? ts (ts(.select ^s ^X ^o)))", "nil");
		AssertInterpretEquals("(equal? ts (ts(.select ^s ^p ^X)))", "nil");
		// #TODO AssertInterpretEquals("(member? (ts(.asTriples)) ^(((triple s p o2) (triple s p o1) (triple x p z))((triple x p z) (triple s p o2) (triple s p o1))))", "true");
		// Either sort the reult before comparing or test member? on a list of triples.

		AssertInterpretEquals("((ts(.select ^s nil nil))(.asTriples))",
				"((triple s p o2) (triple s p o1))");
		AssertInterpretEquals("((ts(.select ^s ^p nil))(.asTriples))",
				"((triple s p o2) (triple s p o1))");
		AssertInterpretEquals("((ts(.select ^s ^p ^o1))(.asTriples))", "((triple s p o1))");
		AssertInterpretEquals("((ts(.select ^s ^p ^o2))(.asTriples))", "((triple s p o2))");

		// #TODO random order makes flaky test -AssertInterpretEquals("(ts(.asTriples))", "((triple x p z) (triple s p o2) (triple s p o1))");
		AssertInterpretEquals("((ts(.select nil ^p ^o2))(.asTriples))", "((triple s p o2))");

		AssertInterpretEquals("((ts(.select nil nil ^o2))(.asTriples))", "((triple s p o2))");
		// #TODO random order makes flaky test - AssertInterpretEquals("((ts(.select nil ^p nil))(.asTriples))","((triple x p z) (triple s p o2) (triple s p o1))");
	}

	public void testInterpEquals1() throws Exception {
		AssertInterpretEquals("(defvar ^ts1 (graph))", "(graph)");
		AssertInterpretEquals("(ts1(.add (triple ^s ^p ^o)))", "(graph)");
		AssertInterpretEquals("(ts1(.add (triple ^s ^p ^o2)))", "(graph)");
		AssertInterpretEquals("(ts1(.add (triple ^x ^p ^o)))", "(graph)");
		AssertInterpretEquals("(ts1(.add (triple ^s ^p 99)))", "(graph)");
		AssertInterpretEquals("(ts1(.add (triple ^x ^p 99)))", "(graph)");

		AssertInterpretEquals("(defvar ^ts2 (graph))", "(graph)");
		AssertInterpretEquals("(ts2(.add (triple ^s ^p ^o)))", "(graph)");
		AssertInterpretEquals("(ts2(.add (triple ^s ^p ^o2)))", "(graph)");
		AssertInterpretEquals("(ts2(.add (triple ^x ^p ^o)))", "(graph)");
		AssertInterpretEquals("(ts2(.add (triple ^s ^p 99)))", "(graph)");
		AssertInterpretEquals("(ts2(.add (triple ^x ^p 99)))", "(graph)");

		AssertInterpretEquals("(equal? ts1 ts2)", "true");
		AssertInterpretEquals("(equal? ts2 ts1)", "true");
	}

	public void testInterpEquals2() throws Exception {
		AssertInterpretEquals("(defvar ^ts1 (graph))", "(graph)");
		AssertInterpretEquals("(ts1(.add (triple ^s ^p ^o)))", "(graph)");
		AssertInterpretEquals("(ts1(.add (triple ^s ^p ^o2)))", "(graph)");
		AssertInterpretEquals("(ts1(.add (triple ^x ^p ^o)))", "(graph)");
		AssertInterpretEquals("(ts1(.add (triple ^s ^p 99)))", "(graph)");
		AssertInterpretEquals("(ts1(.add (triple ^x ^p 99)))", "(graph)");

		AssertInterpretEquals("(defvar ^ts2 (graph))", "(graph)");
		AssertInterpretEquals("(ts2(.add (triple ^s ^p ^o)))", "(graph)");
		AssertInterpretEquals("(ts2(.add (triple ^s ^p ^o2)))", "(graph)");
		AssertInterpretEquals("(ts2(.add (triple ^x ^p ^o)))", "(graph)");
		AssertInterpretEquals("(ts2(.add (triple ^s ^p 99)))", "(graph)");
		AssertInterpretEquals("(ts2(.add (triple ^x ^p 99)))", "(graph)");

		AssertInterpretEquals("(equal? ts1 ts2)", "true");
		AssertInterpretEquals("(equal? ts2 ts1)", "true");
	}

	public void testInterpEquals3() throws Exception {
		AssertInterpretEquals("(defvar ^ts1 (graph))", "(graph)");
		AssertInterpretEquals("(defvar ^ts2 (graph))", "(graph)");
		AssertInterpretEquals("(equal? ts1 ts2)", "true");
		AssertInterpretEquals("(equal? ts2 ts1)", "true");
	}

	public void testInterpEquals4() throws Exception {
		AssertInterpretEquals("(defvar ^ts1 (graph))", "(graph)");
		AssertInterpretEquals("(ts1(.add (triple ^s ^p ^o)))", "(graph)");
		AssertInterpretEquals("(ts1(.add (triple ^s ^p ^o2)))", "(graph)");
		AssertInterpretEquals("(ts1(.add (triple ^x ^p ^o)))", "(graph)");
		AssertInterpretEquals("(ts1(.add (triple ^s ^p 99)))", "(graph)");

		AssertInterpretEquals("(defvar ^ts2 (graph))", "(graph)");
		AssertInterpretEquals("(ts2(.add (triple ^s ^p ^o)))", "(graph)");
		AssertInterpretEquals("(ts2(.add (triple ^s ^p ^o2)))", "(graph)");
		AssertInterpretEquals("(ts2(.add (triple ^x ^p ^o)))", "(graph)");
		AssertInterpretEquals("(ts2(.add (triple ^s ^p 99)))", "(graph)");
		AssertInterpretEquals("(ts2(.add (triple ^x ^p 99)))", "(graph)");

		AssertInterpretEquals("(equal? ts1 ts2)", "nil");
		AssertInterpretEquals("(equal? ts2 ts1)", "nil");
	}

	public void testInterpEquals5() throws Exception {
		AssertInterpretEquals("(defvar ^ts1 (graph))", "(graph)");
		AssertInterpretEquals("(ts1(.add (triple ^s ^p ^o)))", "(graph)");

		AssertInterpretEquals("(defvar ^ts2 (graph))", "(graph)");

		AssertInterpretEquals("(equal? ts1 ts2)", "nil");
		AssertInterpretEquals("(equal? ts2 ts1)", "nil");
	}

	public void testInterpCondition() throws Exception {
		// #TODO AssertInterpretEquals("(defvar ^isObject99 (lambda (s o p) (equal? p 99)))", "<EagerProc: <anonymous lambda>>");
		AssertInterpretEquals("(defvar ^ts (graph))", "(graph)");
		AssertInterpretEquals("(ts(.add (triple ^s ^p ^o)))", "(graph)");
		AssertInterpretEquals("(ts(.add (triple ^s ^p ^o2)))", "(graph)");
		AssertInterpretEquals("(ts(.add (triple ^x ^p ^o)))", "(graph)");
		AssertInterpretEquals("(ts(.add (triple ^s ^p 99)))", "(graph)");
		AssertInterpretEquals("(ts(.add (triple ^x ^p 99)))", "(graph)");

		AssertInterpretEquals("(length (ts(.asTriples)))", "5");


		/* #TODO
		AssertInterpretEquals("(defvar ^result (ts(.select nil nil nil isObject99))))",
				"(graph)");
		AssertInterpretEquals("(defvar ^result (ts(.select ^s nil nil isObject99))))",
				"(graph)");
		AssertInterpretEquals("(result(.asTriples))", "((triple s p 99))");
		AssertInterpretEquals("(defvar ^result (ts(.select ^s ^p nil isObject99))))",
				"(graph)");
		AssertInterpretEquals("(result(.asTriples))", "((triple s p 99))");
		AssertInterpretEquals("(defvar ^result (ts(.select ^s ^p 99 isObject99))))",
				"(graph)");
		AssertInterpretEquals("(result(.asTriples))", "((triple s p 99))");
		*/
	}

	public void testInterpConditionWithVar() throws Exception {
		AssertInterpretEquals("(defvar ^ninenine 99)", "99");
		AssertInterpretEquals("(defvar ^isObject99 (lambda (s p o) (equal? o 99)))",
				"<EagerProc: <anonymous lambda>>");
		AssertInterpretEquals("(defvar ^ts (graph))", "(graph)");
		AssertInterpretEquals("(ts(.add (triple ^s ^p ^o)))", "(graph)");
		AssertInterpretEquals("(ts(.add (triple ^x ^p ^o)))", "(graph)");
		AssertInterpretEquals("(ts(.add (triple ^s ^p ninenine)))", "(graph)");
		AssertInterpretEquals("(ts(.add (triple ^x ^p ninenine)))", "(graph)");
		AssertInterpretEquals("(defvar ^result (ts(.select ^s ^p ninenine isObject99))))",
				"(graph)");
		AssertInterpretEquals("(result(.asTriples))", "((triple s p 99))");
		AssertInterpretEquals(
				"((SetList.equal?)(ts(.asTriples)) (list (triple ^s ^p ^o) (triple ^x ^p ^o) (triple ^x ^p ninenine) (triple ^s ^p ninenine)))",
				"true");
	}

	public void testInterpStoreConstruction() throws Exception {
		AssertInterpretEquals("(defvar ^noop (lambda (&rest args)))",
				"<EagerProc: <anonymous lambda>>");
		AssertInterpretEquals("(graph ^(s p o))", "(graph)");
		AssertInterpretEquals("(defvar ^ts (graph ^(s p o)))", "(graph)");
		AssertInterpretEquals("(ts(.select ^s nil nil noop)))", "(graph)");
		AssertInterpretEquals("(ts(.asTriples)))", "((triple s p o))");
	}

	public void testInterpStoreRemove() throws Exception {
		AssertInterpretEquals("(defvar ^ts (graph ^(s p o)))", "(graph)");
		AssertInterpretEquals("(ts(.asTriples)))", "((triple s p o))");
		AssertInterpretEquals("(ts(.remove (triple ^s ^p ^o))))", "(graph)");
		AssertInterpretEquals("(ts(.asTriples)))", "nil");
	}

	public void testInterpStoreConstructionMulti() throws Exception {
		AssertInterpretEquals("(defvar ^noop (lambda (&rest args)))",
				"<EagerProc: <anonymous lambda>>");
		AssertInterpretEquals("(graph ^(s p o) ^(s b c))", "(graph)");
		AssertInterpretEquals("(defvar ^ts (graph ^(s p o) ^(s b c)))", "(graph)");
		AssertInterpretEquals("(ts(.select ^s nil nil noop)))", "(graph)");
		AssertInterpretEquals("(length (ts(.asTriples))))", "2");
		AssertInterpretEquals(
				"((SetList.equal?) (ts(.asTriples)) (list (tripleq s p o) (tripleq s b c)))",
				"true");
	}

	public void testInterpTripleDict() throws Exception {
		AssertInterpretEquals("((dict(.a = 3)(.b = 5))(.asTriples 100))","((triple 100 b 5) (triple 100 a 3))");
		AssertInterpretEquals("(defvar ^thedict (dict(.a =3)(.b =5)))","(dict (.a = 3) (.b = 5))");
		AssertInterpretEquals("((thedict(.asGraph 101))(.asTriples))", "((triple 101 b 5) (triple 101 a 3))");
	}

	public void testInterpTriplesClasses() throws Exception {
		AssertInterpretEquals("(23(.asTriples .self))", "((triple 23 type <class Bignum (Builtin)>))");
		AssertInterpretEquals("('X'(.asTriples .self))",
				"((triple X type <class String (Builtin)>))");
		BadAssertInterpretEquals("(^(a =e)(.asTriples .self))");
		AssertInterpretEquals("(^sym(.asTriples .self))",
				"((triple sym type <class SimpleSymbol (Symbol)>))");

	}

	public void testInterpGraphClasses() throws Exception {
		AssertInterpretEquals("((23(.asGraph .self))(.asTriples))",
				"((triple 23 type <class Bignum (Builtin)>))");
		AssertInterpretEquals("(('X'(.asGraph .self))(.asTriples))",
				"((triple X type <class String (Builtin)>))");
	}
}
