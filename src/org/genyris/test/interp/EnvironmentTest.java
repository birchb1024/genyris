// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.test.interp;

import junit.framework.TestCase;

import org.genyris.core.Bignum;
import org.genyris.core.NilSymbol;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.Symbol;
import org.genyris.core.URISymbol;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.StandardEnvironment;

public class EnvironmentTest extends TestCase {
	
	public void testEnvDefine() throws Exception {		
		Interpreter interp = new Interpreter();
		Environment env = new StandardEnvironment(interp.getSymbolTable(), new NilSymbol());
		Symbol sym = new SimpleSymbol("answer");
		Bignum val = new Bignum(42);
		env.defineVariable(sym, val);
		assertEquals(val, env.lookupVariableValue(sym));		
	}
	
	public void testEnvDefineQualified() throws Exception {		
		Interpreter interp = new Interpreter();
		Environment env = new StandardEnvironment(interp.getSymbolTable(), new NilSymbol());
		Symbol sym = new URISymbol("http://foo/bar#answer");
		Bignum val = new Bignum(42);
		env.defineVariable(sym, val);
		assertEquals(val, env.lookupVariableValue(sym));		
	}
	public void testEnvNested() throws Exception {		
		Interpreter interp = new Interpreter();
		Environment env1 = new StandardEnvironment(interp.getSymbolTable(), new NilSymbol());
		SimpleSymbol sym = new SimpleSymbol("answer");
		Bignum val = new Bignum(42);
		env1.defineVariable(sym, val);
		assertEquals(val, env1.lookupVariableValue(sym));		
		
		Environment env2 = new StandardEnvironment(env1);
		Bignum val2 = new Bignum(99);
		env2.defineVariable(sym, val2);

		assertEquals(val2, env2.lookupVariableValue(sym));		
	}
	
	public void testEnvNestedSets() throws Exception {
		Interpreter interp = new Interpreter();
		Environment env1 = new StandardEnvironment(interp.getSymbolTable(), new NilSymbol());
		SimpleSymbol sym1 = new SimpleSymbol("answer");
		Bignum val = new Bignum(42);
		env1.defineVariable(sym1, val);
		assertEquals(val, env1.lookupVariableValue(sym1));		
		
		Environment env2 = new StandardEnvironment(env1);
		SimpleSymbol sym2 = new SimpleSymbol("question");
		Bignum val2 = new Bignum(99);
		env2.defineVariable(sym2, val2);
		assertEquals(val2, env2.lookupVariableValue(sym2));		
		
		Bignum val3 = new Bignum(33);
		env1.setVariableValue(sym1, val3);
		assertEquals(val3, env1.lookupVariableValue(sym1));	
		
		Bignum val4 = new Bignum(44);
		env2.setVariableValue(sym2, val4);
		assertEquals(val4, env2.lookupVariableValue(sym2));	
		
	}
	
	public void testEnvEvalSelf() throws Exception {		
		Environment env = new StandardEnvironment(new NilSymbol());
		Bignum int42 = new Bignum(42);
		
		assertEquals(int42, int42.eval(env));
		Bignum double4p2 = new Bignum(4.2);
		assertEquals(double4p2, double4p2.eval(env));
	
	}

	public void testEnvEvalVariables() throws Exception {		
		Environment env = new StandardEnvironment(new NilSymbol());
		SimpleSymbol answer = new SimpleSymbol("answer");
		Bignum int42 = new Bignum(42);
		env.defineVariable(answer, int42);
		assertEquals(int42, int42.eval(env));
		assertEquals(int42, answer.eval(env));
	}

	
}
