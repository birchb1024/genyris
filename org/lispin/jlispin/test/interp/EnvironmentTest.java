package org.lispin.jlispin.test.interp;

import junit.framework.TestCase;

import org.lispin.jlispin.core.Ldouble;
import org.lispin.jlispin.core.Linteger;
import org.lispin.jlispin.core.Lsymbol;
import org.lispin.jlispin.interp.Environment;
import org.lispin.jlispin.interp.Evaluator;
import org.lispin.jlispin.interp.StandardEnvironment;

public class EnvironmentTest extends TestCase {
	
	public void testEnvBasics() throws Exception {		
		Environment env = new StandardEnvironment(null);
		Lsymbol sym = new Lsymbol("answer");
		Linteger val = new Linteger(42);
		env.defineVariable(sym, val);
		assertEquals(val, env.lookupVariableValue(sym));		
	}
	
	public void testEnvNested() throws Exception {		
		Environment env1 = new StandardEnvironment(null);
		Lsymbol sym = new Lsymbol("answer");
		Linteger val = new Linteger(42);
		env1.defineVariable(sym, val);
		assertEquals(val, env1.lookupVariableValue(sym));		
		
		Environment env2 = new StandardEnvironment(env1);
		Linteger val2 = new Linteger(99);
		env2.defineVariable(sym, val2);

		assertEquals(val2, env2.lookupVariableValue(sym));		
	}
	
	public void testEnvNestedSets() throws Exception {		
		Environment env1 = new StandardEnvironment(null);
		Lsymbol sym1 = new Lsymbol("answer");
		Linteger val = new Linteger(42);
		env1.defineVariable(sym1, val);
		assertEquals(val, env1.lookupVariableValue(sym1));		
		
		Environment env2 = new StandardEnvironment(env1);
		Lsymbol sym2 = new Lsymbol("question");
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
		Environment env = new StandardEnvironment(null);
		Linteger int42 = new Linteger(42);
		
		assertEquals(int42, Evaluator.eval(env, int42));
		Ldouble double4p2 = new Ldouble(4.2);
		assertEquals(double4p2, Evaluator.eval(env, double4p2));
	
	}

	public void testEnvEvalVariables() throws Exception {		
		Environment env = new StandardEnvironment(null);
		Lsymbol answer = new Lsymbol("answer");
		Linteger int42 = new Linteger(42);
		env.defineVariable(answer, int42);
		assertEquals(int42, Evaluator.eval(env, int42));
		assertEquals(int42, Evaluator.eval(env, answer));
	}

	
}
