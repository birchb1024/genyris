package org.lispin.jlispin.test.interp;

import junit.framework.TestCase;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.InStream;
import org.lispin.jlispin.core.LexException;
import org.lispin.jlispin.core.Lsymbol;
import org.lispin.jlispin.core.ParseException;
import org.lispin.jlispin.core.Parser;
import org.lispin.jlispin.core.StringInStream;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.core.UngettableInStream;
import org.lispin.jlispin.interp.BuiltInCons;
import org.lispin.jlispin.interp.Environment;

public class EvalApplyTest extends TestCase {
	
	public void testLambda1() throws Exception {		
		Environment env = new Environment(null);
		env.defineVariable(new Lsymbol("cons"), new BuiltInCons());
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
		env.defineVariable(new Lsymbol("cons"), new BuiltInCons());
		env.defineVariable(SymbolTable.NIL, SymbolTable.NIL);
		InStream input = new UngettableInStream( new StringInStream(exp));
		Parser parser = new Parser(table, input);
		Exp expression = parser.read();
		Exp result = env.eval(expression);
		assertEquals(expected, result.toString());
	}
	
	public void testLambda2() throws Exception {		
		excerciseEval("((lambda (x) (cons x x)) 23)", "(23 . 23)");
		excerciseEval("((lambda (x y) (cons x y)) 5 nil)", "(5)");
	}
}
