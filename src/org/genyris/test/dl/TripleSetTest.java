package org.genyris.test.dl;

import junit.framework.TestCase;

import org.genyris.core.Bignum;
import org.genyris.core.Lstring;
import org.genyris.core.SimpleSymbol;
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

    private void excerciseEval(String exp, String expected) throws Exception {
        assertEquals(expected,  interpreter.eval(exp));
    }

    private void excerciseBadEval(String exp) {
        try {
			interpreter.eval(exp);
			fail();
		} catch (GenyrisException e) {}
    }
    
    public void testBasicTripleSet() {
    	TripleSet ts = new TripleSet();
    	assertEquals(ts, ts);
    	assertEquals(ts.empty(), true);

    	TripleSet ts2 = new TripleSet();
    	assertTrue(ts.equals(ts2));

    	Bignum subject = new Bignum(1);
    	ts.add(new Triple(subject, new SimpleSymbol("s"), new Lstring("$")));
    	ts2.add(new Triple(new Bignum(1), new SimpleSymbol("s"), new Lstring("$")));
    	assertFalse(ts.equals(ts2));

    	TripleSet result = ts.query(subject, null, null);
    	assertEquals(ts, result);
    }
    public void testInterpStore() throws Exception {
    	excerciseEval("((car ((tripleset)!classes))!vars)","(asTriples add subclasses classname superclasses select vars)");
    	excerciseEval("(defvar 'ts (tripleset))","(tripleset)");
    	excerciseEval("(ts!classes)","(<class TripleSet (Builtin) ()>)");
    	excerciseEval("(ts(!add (triple 's 'p 'o)))","(tripleset)");
    	excerciseBadEval("(ts(!add (triple 's 3 'o)))");
    	excerciseEval("(ts(!select 's nil nil)))","(tripleset)");
    	excerciseEval("(equal? ts (ts(!select 's nil nil)))","true");
    	excerciseEval("(ts(!asTriples))","((triple s p o))");
    }

    public void testInterpStoreConstruction() throws Exception {
    	excerciseEval("(tripleset '(s p o))","(tripleset)");
    	excerciseEval("(defvar 'ts (tripleset '(s p o)))","(tripleset)");
    	excerciseEval("(ts(!select 's nil nil)))","(tripleset)");
    	excerciseEval("(ts(!asTriples)))","((triple s p o))");
    }
    public void testInterpStoreConstructionMulti() throws Exception {
    	excerciseEval("(tripleset '(s p o) '(s b c))","(tripleset)");
    	excerciseEval("(defvar 'ts (tripleset '(s p o) '(s b c)))","(tripleset)");
    	excerciseEval("(ts(!select 's nil nil)))","(tripleset)");
    	excerciseEval("(length (ts(!asTriples))))","2");
    	excerciseEval("(ts(!asTriples)))","((triple s b c) (triple s p o))");
    }
    
    public void testInterpTripleDict() throws Exception {
    	excerciseEval("((dict(!a:3)(!b:5))(!asTriples))","((triple <dict (dict (a : 3) (b : 5))> b 5) (triple <dict (dict (a : 3) (b : 5))> a 3))");
    	excerciseEval("((dict(!a:3)(!b:5))(!asTripleSet))","(tripleset)");
    	excerciseEval("(defvar 'ts ((dict(!a:3)(!b:5))(!asTripleSet)))","(tripleset)");
    	excerciseEval("(ts(!asTriples))","((triple <dict (dict (a : 3) (b : 5))> a 3) (triple <dict (dict (a : 3) (b : 5))> b 5))");
    }

}
