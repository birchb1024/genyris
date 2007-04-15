package org.lispin.jlispin.test.interp;

import junit.framework.TestCase;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.InStream;
import org.lispin.jlispin.core.Linteger;
import org.lispin.jlispin.core.Lsymbol;
import org.lispin.jlispin.core.Parser;
import org.lispin.jlispin.core.StringInStream;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.core.UngettableInStream;
import org.lispin.jlispin.interp.ApplyCons;
import org.lispin.jlispin.interp.EagerProcedure;
import org.lispin.jlispin.interp.Environment;

public class EvalApplyTest extends TestCase {
	
	public void testLambda1() throws Exception {		
		Environment env = new Environment(null);
		env.defineVariable(new Lsymbol("cons"), new EagerProcedure(env, null, new ApplyCons()));
		SymbolTable table = new SymbolTable();
		InStream input = new UngettableInStream( new StringInStream("((lambda (x) (cons x x)) 23)"));
		Parser parser = new Parser(table, input);
		Exp expression = parser.read();
		Exp result = env.eval(expression);
		assertEquals("(23 . 23)", result.toString());

	}
	
	void excerciseEval(String exp, String expected) throws Exception {
		Environment env = new Environment(null);
		SymbolTable table = new SymbolTable();
		env.defineVariable(new Lsymbol("cons"), new EagerProcedure(env, null, new ApplyCons()));
		env.defineVariable(SymbolTable.quote, SymbolTable.quote);
		env.defineVariable(SymbolTable.NIL, SymbolTable.NIL);
		Environment env2 = new Environment(env);
		env2.defineVariable(new Lsymbol("alpha"), new Linteger(23));
		env2.defineVariable(new Lsymbol("bravo"), new Linteger(45));
		InStream input = new UngettableInStream( new StringInStream(exp));
		Parser parser = new Parser(table, input);
		Exp expression = parser.read();
		Exp result = env2.eval(expression);
		assertEquals(expected, result.toString());
	}
	
	public void testLambdaVariables() throws Exception {		
		excerciseEval("alpha", "23");
		excerciseEval("bravo", "45");
		excerciseEval("(quote alpha)", "alpha");
		excerciseEval("(cons alpha bravo)", "(23 . 45)");
		excerciseEval("(cons alpha (cons bravo nil))", "(23 45)");
		}

	public void testLambdaBuitins() throws Exception {		
		excerciseEval("(cons 4 5)", "(4 . 5)");
		excerciseEval("(cons 4 (cons 5 nil))", "(4 5)");
		excerciseEval("(cons 4 nil)", "(4)");
		excerciseEval("(quote 99)", "99");
		excerciseEval("(quote (34)))", "(34)");
		excerciseEval("(quote (foo)))", "(foo)");
		}

	public void testLambda2() throws Exception {		
		excerciseEval("((lambda (x) (cons x x)) 23)", "(23 . 23)");
		excerciseEval("((lambda (x y) (cons x y)) 5 nil)", "(5)");
		excerciseEval("((lambda (x y) (quote x)) 23 45)", "x");
	}
	public void testSequence() throws Exception {		
		excerciseEval("((lambda (x) (cons x x) (cons 3 4)) 23)", "(3 . 4)");
		excerciseEval("((lambda (x y) (cons x x) (cons y x)) 23 45)", "(45 . 23)");
	}

	public void testSet() throws Exception {		
		excerciseEval("((lambda (x) (setq x 99) (cons x x)) 23)", "(99 . 99)");
	}


}
