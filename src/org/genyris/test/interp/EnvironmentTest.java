// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.interp;

import junit.framework.TestCase;

import org.genyris.core.FullyQualifiedSymbol;
import org.genyris.core.Ldouble;
import org.genyris.core.Linteger;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.NilSymbol;
import org.genyris.core.Symbol;
import org.genyris.interp.Environment;
import org.genyris.interp.Evaluator;
import org.genyris.interp.Interpreter;
import org.genyris.interp.StandardEnvironment;

public class EnvironmentTest extends TestCase {
	
	public void testEnvDefine() throws Exception {		
		Interpreter interp = new Interpreter();
		Environment env = new StandardEnvironment(interp, new NilSymbol());
		SimpleSymbol sym = new SimpleSymbol("answer");
		Linteger val = new Linteger(42);
		env.defineVariable(sym, val);
		assertEquals(val, env.lookupVariableValue(sym));		
	}
	
	public void testEnvDefineQualified() throws Exception {		
		Interpreter interp = new Interpreter();
		Environment env = new StandardEnvironment(interp, new NilSymbol());
		Symbol sym = new FullyQualifiedSymbol("http://foo/bar#answer");
		Linteger val = new Linteger(42);
		env.defineVariable(sym, val);
		assertEquals(val, env.lookupVariableValue(sym));		
	}
	public void testEnvNested() throws Exception {		
		Interpreter interp = new Interpreter();
		Environment env1 = new StandardEnvironment(interp, new NilSymbol());
		SimpleSymbol sym = new SimpleSymbol("answer");
		Linteger val = new Linteger(42);
		env1.defineVariable(sym, val);
		assertEquals(val, env1.lookupVariableValue(sym));		
		
		Environment env2 = new StandardEnvironment(env1);
		Linteger val2 = new Linteger(99);
		env2.defineVariable(sym, val2);

		assertEquals(val2, env2.lookupVariableValue(sym));		
	}
	
	public void testEnvNestedSets() throws Exception {
		Interpreter interp = new Interpreter();
		Environment env1 = new StandardEnvironment(interp, new NilSymbol());
		SimpleSymbol sym1 = new SimpleSymbol("answer");
		Linteger val = new Linteger(42);
		env1.defineVariable(sym1, val);
		assertEquals(val, env1.lookupVariableValue(sym1));		
		
		Environment env2 = new StandardEnvironment(env1);
		SimpleSymbol sym2 = new SimpleSymbol("question");
		Linteger val2 = new Linteger(99);
		env2.defineVariable(sym2, val2);
		assertEquals(val2, env2.lookupVariableValue(sym2));		
		
		Linteger val3 = new Linteger(33);
		env1.setVariableValue(sym1, val3);
		assertEquals(val3, env1.lookupVariableValue(sym1));	
		
		Linteger val4 = new Linteger(44);
		env2.setVariableValue(sym2, val4);
		assertEquals(val4, env2.lookupVariableValue(sym2));	
		
	}
	
	public void testEnvEvalSelf() throws Exception {		
		Environment env = new StandardEnvironment(new NilSymbol());
		Linteger int42 = new Linteger(42);
		
		assertEquals(int42, Evaluator.eval(env, int42));
		Ldouble double4p2 = new Ldouble(4.2);
		assertEquals(double4p2, Evaluator.eval(env, double4p2));
	
	}

	public void testEnvEvalVariables() throws Exception {		
		Environment env = new StandardEnvironment(new NilSymbol());
		SimpleSymbol answer = new SimpleSymbol("answer");
		Linteger int42 = new Linteger(42);
		env.defineVariable(answer, int42);
		assertEquals(int42, Evaluator.eval(env, int42));
		assertEquals(int42, Evaluator.eval(env, answer));
	}

	
}
