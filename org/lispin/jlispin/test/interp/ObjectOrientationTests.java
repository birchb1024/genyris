package org.lispin.jlispin.test.interp;

import org.lispin.jlispin.interp.LispinException;

import junit.framework.TestCase;

public class ObjectOrientationTests extends TestCase {
	
	private TestUtilities interpreter;

	protected void setUp() throws Exception {
		super.setUp();
		interpreter = new TestUtilities();
	}
	
	private void checkEval(String exp, String expected) throws LispinException {
		assertEquals(expected, interpreter.eval(exp));
	}
	
	private void checkEvalBad(String exp) throws LispinException {
		try {
			interpreter.eval(exp);
			fail("expecting exception");
		}
		catch (LispinException e) {
		}
	}

	private void eval(String script) throws LispinException {
		interpreter.eval(script);
	}
	
	public void testExcerciseEval() throws LispinException {
		eval("(defvar '$global 999)");
		checkEval("$global", "999");

		eval("(defvar 'Standard-Class (dict))");
		checkEval("Standard-Class", "<CallableEnvironment<dict nil>>");
		checkEval("(defvar 'Account (dict (.classes (cons Standard-Class nil)) (.print (lambda () (cons $global .balance))) ))",
				"<CallableEnvironment<dict ((.print <EagerProc: <org.lispin.jlispin.interp.ClassicFunction>>) (.classes (<CallableEnvironment<dict nil>>)))>>");
		
		eval("(Account " + 
		    "(defvar '.new " + 
		        "(lambda (initial-balance) " + 
		          "(dict " + 
		            "(.classes (cons Account nil)) " + 
		            "(.balance initial-balance))))) " );
		
		eval("(defvar 'bb  (Account (.new 1000)))");
  
		checkEval("(bb(.print))", "(999 ^ 1000)");

		checkEval("(bb((lambda () .balance)))", "1000");
	}

	
	public void testInheritance() throws LispinException {

		eval("(defvar 'Standard-Class (dict))");
		
		eval("(defvar 'Base-1 " + 
		    "(dict " + 
		        "(.classes (cons Standard-Class nil)) " + 
		        "(.toString \"Base-1 toString\"))) " );
		
		checkEval("(Base-1 .toString)", "\"Base-1 toString\"");
		        		
		eval("(defvar 'Base-2 " + 
		    "(dict " + 
		        "(.classes (cons Standard-Class nil)) " + 
		        "(.log \"Base-2 log\"))) " );
		        
		checkEval("(Base-2 .log)", "\"Base-2 log\"");

		eval("(defvar 'Class-1 " + 
				"(dict " + 
					"(.classes (cons Standard-Class nil)) " + 
					"(.superclasses (cons Base-1 nil)) " + 
					"(.print \"Class-1 print\")"  + 
					"(.new " + 
						"(lambda (_a) " + 
							"(dict " + 
								"(.classes (cons Class-1 nil)) " + 
								"(.a _a)))))) " );
		checkEval("(Class-1 .print)", "\"Class-1 print\"");
		checkEval("(Class-1 .toString)", "\"Base-1 toString\"");
		
		eval("(defvar 'Class-2 " + 
		    "(dict" + 
		    	"(.classes (cons Standard-Class nil))" + 
		    	"(.superclasses (cons Base-2 nil))" + 
		    	"(.draw \"Class-2 draw\")))" );
		        		
		checkEval("(Class-2 .draw)", "\"Class-2 draw\"");
		checkEval("(Class-2 .log)", "\"Base-2 log\"");

		eval("(defvar 'object " + 
			    "(dict" + 
			      "(.classes (cons Class-1 (cons Class-2 nil)))))" );
		checkEval("(object .log)", "\"Base-2 log\"");
		checkEval("(object .draw)", "\"Class-2 draw\"");
		checkEval("(object .print)", "\"Class-1 print\"");
		checkEval("(object .toString)", "\"Base-1 toString\"");

		checkEval("(object (defvar '.local 23))", "23");
		checkEval("(object .local)", "23");
		checkEval("(object (set '.local 900))", "900");
		checkEval("(object .local)", "900");
		
		checkEvalBad("(object (set '.log 757))");
		checkEval("(object (defvar '.log 757))", "757");
		checkEval("(Base-2 .log)", "\"Base-2 log\""); 
		checkEval("(object .log)", "757"); 
	}
}
