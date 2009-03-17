package org.genyris.test.dl;

import junit.framework.TestCase;

import org.genyris.core.Bignum;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.SymbolTable;
import org.genyris.dl.Triple;
import org.genyris.exception.GenyrisException;
import org.genyris.test.interp.TestUtilities;

public class TripleTest extends TestCase {

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

    public void testToString() {
		assertEquals(new Triple(new Bignum(34), new SimpleSymbol("s"), new Bignum(99)).toString(),
				"(triple 34 s 99)");
	}

	public void testTriple() {
		assertTrue(new Triple(new Bignum(34), new SimpleSymbol("s"), new Bignum(99)) 
			!= new Triple(new Bignum(34), new SimpleSymbol("s"), new Bignum(99)));
		assertNotSame(new Triple(new Bignum(34), new SimpleSymbol("s"), new Bignum(99)) 
			, new Triple(new Bignum(34), new SimpleSymbol("s"), new Bignum(99)));
	}

	public void testGetBuiltinClassSymbol() {
		SymbolTable table =  new SymbolTable();
		table.init(new SimpleSymbol("nil"));
		assertEquals("Triple", new Triple(new Bignum(34), new SimpleSymbol("s")
			, new Bignum(99)).getBuiltinClassSymbol(table).toString());
	}

	public void testTripleFunction() throws Exception {
		excerciseEval("(triple 1 's 34)", "(triple 1 s 34)");
		excerciseBadEval("(triple 1 \"s\" 34)");
		excerciseEval("(triple 1 '|http://foo/| 23)", "(triple 1 |http://foo/| 23)");
		excerciseEval("((triple 1 '|http://foo/| 23)!classes)", "(<class Triple (Builtin) ()>)");
		excerciseEval("(equal? (triple 1 'S 23) (triple 1 'S 23))", "nil");
		excerciseEval("(equal? (triple 'S 'P 'O) (triple 'S 'P 'O))", "true");
		
	}
	public void testTripleAccessorsFunction() throws Exception {
		excerciseEval("((triple 1 's 34)(!subject))", "1");
		excerciseEval("((triple 1 's 34)(!predicate))", "s");
		excerciseEval("((triple 1 's 34)(!object))", "34");
	}
		public void testTripleAccessorsOnClassFunction() throws Exception {
		excerciseEval("((car (Bignum(!asTriples)))(!subject))", "<class Bignum (Builtin) ()>");
		excerciseEval("((car (Bignum(!asTriples)))(!predicate))", "superclasses");
		excerciseEval("(((car (Bignum(!asTriples)))(!object))!classname)", "Pair");
	}

}
