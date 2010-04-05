package org.genyris.test.java;


public class JavaDummy {
	//
	// Class for testing the FFI code with.
	//
	public static int staticField = 123;
	public int intField;
	public long longField;
	public char charField;
	public float floatField;
	public double doubleField;
	public boolean booleanField;
	public int byteField;
	public int shortField;
	public String stringField;

	public int privateField;

	public JavaDummy() {
		byteField = 0xFF;
		shortField = 2;
		intField = 42;
		longField = 42000000;
		floatField = (float) 4.2;
		doubleField = 4.2e42;
		charField = 'G';
		booleanField = false;
		stringField = "FOO!";
		
		privateField = 42;
		privateField = privateField*2; // stop compiler warning
	}
	
	public int getInt() {
		return intField;
	}
	public void setInt(int i) {
		intField = i;
	}
	public static String[] staticMethod1(int count) {
		String[] retval = new String[count];
		for(int i=0;i<count;i++) {
			retval[i] = Integer.toString(i);
		}
		return retval;
	}
	public static Integer[] staticMethod2(int count) {
		Integer[] retval = new Integer[count];
		for(int i=0;i<count;i++) {
			retval[i] = new Integer(i);
		}
		return retval;
	}

	public String[] method1(String[] args) {
		return args;
	}
	public static int failmethod1() throws Exception {
		int x = 45;
		if(x == 45) throw new Exception("death in failmethod1()");
		return 43; 
	}
	public int failmethod2() throws Exception {
		int x = 45;
		if(x == 45) throw new Exception("death in failmethod2()");
		return 43; 
	}
}
