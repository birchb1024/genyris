package org.lispin.jlispin.test.interp;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Linteger;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.format.BasicFormatter;
import org.lispin.jlispin.interp.EagerProcedure;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.LazyProcedure;
import org.lispin.jlispin.interp.LispinException;
import org.lispin.jlispin.interp.StandardEnvironment;
import org.lispin.jlispin.interp.builtin.CarFunction;
import org.lispin.jlispin.interp.builtin.CdrFunction;
import org.lispin.jlispin.interp.builtin.ConditionalFunction;
import org.lispin.jlispin.interp.builtin.ConsFunction;
import org.lispin.jlispin.interp.builtin.DefineFunction;
import org.lispin.jlispin.interp.builtin.QuoteFunction;
import org.lispin.jlispin.interp.builtin.ReplaceCarFunction;
import org.lispin.jlispin.interp.builtin.ReplaceCdrFunction;
import org.lispin.jlispin.interp.builtin.SetFunction;
import org.lispin.jlispin.io.InStream;
import org.lispin.jlispin.io.Parser;
import org.lispin.jlispin.io.StringInStream;
import org.lispin.jlispin.io.UngettableInStream;

public class EvalApplyTest extends TestCase {
	
	public void testLambda1() throws Exception {		
		SymbolTable table = new SymbolTable();
		Environment env = new StandardEnvironment(null);
		env.defineVariable(table.internString("cons"), new EagerProcedure(env, null, new ConsFunction()));
		InStream input = new UngettableInStream( new StringInStream("((lambda (x) (cons x x)) 23)"));
		Parser parser = new Parser(table, input);
		Exp expression = parser.read();
		Exp result = env.eval(expression);
		StringWriter out = new StringWriter();
		BasicFormatter formatter = new BasicFormatter(out);
		result.acceptVisitor(formatter);
		assertEquals("(23 . 23)", out.getBuffer().toString());

	}
	
	void excerciseEval(String exp, String expected) throws Exception {
		excerciseEval(exp, expected, "");
	}

	void excerciseEval(String exp, String expected, String exceptionExpected) throws Exception {
		Environment env = new StandardEnvironment(null);
		SymbolTable table = new SymbolTable();
		env.defineVariable(table.internString("car"), new EagerProcedure(env, null, new CarFunction()));
		env.defineVariable(table.internString("cdr"), new EagerProcedure(env, null, new CdrFunction()));
		env.defineVariable(table.internString("rplaca"), new EagerProcedure(env, null, new ReplaceCarFunction()));
		env.defineVariable(table.internString("rplacd"), new EagerProcedure(env, null, new ReplaceCdrFunction()));
		env.defineVariable(table.internString("cons"), new EagerProcedure(env, null, new ConsFunction()));
		env.defineVariable(table.internString("quote"), new LazyProcedure(env, null, new QuoteFunction()));
		env.defineVariable(table.internString("defvar"), new EagerProcedure(env, null, new DefineFunction()));
		env.defineVariable(table.internString("set"), new EagerProcedure(env, null, new SetFunction()));
		env.defineVariable(table.internString("cond"), new LazyProcedure(env, null, new ConditionalFunction()));
		env.defineVariable(SymbolTable.NIL, SymbolTable.NIL);
		env.defineVariable(SymbolTable.T, SymbolTable.T);
		Environment env2 = new StandardEnvironment(env);
		env2.defineVariable(table.internString("alpha"), new Linteger(23));
		env2.defineVariable(table.internString("bravo"), new Linteger(45));
		InStream input = new UngettableInStream( new StringInStream(exp));
		Parser parser = new Parser(table, input);
		Exp expression = parser.read();

			try {
				Exp result;
				result = env2.eval(expression);
				
				StringWriter out = new StringWriter();
				BasicFormatter formatter = new BasicFormatter(out);
				result.acceptVisitor(formatter);
				assertEquals(expected, out.getBuffer().toString());

			} 
			catch (LispinException e) {
				assertEquals(exceptionExpected, e.getMessage());
			}

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
		excerciseEval("(car (quote (1 . 2)))", "1");
		excerciseEval("(cdr (quote (1 . 2)))", "2");
		excerciseEval("(rplaca (quote (1 . 2)) 99)", "(99 . 2)");
		excerciseEval("(rplacd (quote (1 . 2)) 100)", "(1 . 100)");
		excerciseEval("(quote (foo)))", "(foo)");
		}

	public void testLambdaArguments() throws Exception {		
		excerciseEval("((lambda () (cons 44 44)) 23)", "(44 . 44)");
		excerciseEval("((lambda (x y) (cons x x)) 23)", "(23 . 23)", "Too few arguments supplied to proc: org.lispin.jlispin.interp.ClassicFunction");
	}

	public void testLambda2() throws Exception {		
		excerciseEval("((lambda () (cons 44 44)) 23)", "(44 . 44)");
		excerciseEval("((lambda (x) (cons x x)) 23)", "(23 . 23)");
		excerciseEval("((lambda (x y) (cons x y)) 5 nil)", "(5)");
		excerciseEval("((lambda (x y) (quote x)) 23 45)", "x");
	}
	
	public void testLambdaq1() throws Exception {		
		excerciseEval("((lambdaq (s) (cons 1 s)) foo)", "(1 . foo)");
		excerciseEval("((lambdaq (s) (cons 1 s)) (cons foo bar))", "(1 cons foo bar)");
	}

	public void testSequence() throws Exception {		
		excerciseEval("((lambda (x) (cons x x) (cons 3 4)) 23)", "(3 . 4)");
		excerciseEval("((lambda (x y) (cons x x) (cons y x)) 23 45)", "(45 . 23)");
	}

	public void testDefine() throws Exception {		
		excerciseEval("((lambda () (defvar (quote charlie) 99) charlie))", "99");
	}

	public void testSet() throws Exception {		
		excerciseEval("(set (quote alpha) 99)", "99");
		excerciseEval("((lambda (x) (set (quote x) 99) (cons x x)) 23)", "(99 . 99)");
	}
	
	public void testCond() throws Exception {		
		excerciseEval("(cond)", "nil");
		excerciseEval("(cond (t 2))", "2");
		excerciseEval("(cond (nil 2))", "nil");
		excerciseEval("(cond (nil (cons 8 9)) (22 (cons 1 2)))", "(1 . 2)");
		excerciseEval("(cond (nil (cons 8 9)) (22 (cons 1 2) 44))", "44");
		}

}
