package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.io.InStream;
import org.lispin.jlispin.io.Parser;
import org.lispin.jlispin.io.StdioInStream;
import org.lispin.jlispin.io.UngettableInStream;

public class ClassicReadEvalPrintLoop {

	public static void main(String[] args) {
		Interpreter interpreter = new Interpreter();
		
		InStream input = new UngettableInStream( new StdioInStream());
		Parser parser = interpreter.newParser(input);
		System.out.println("*** JLispin is listening...");
		do {
			try {
				System.out.print("> ");
				Exp expression;
				expression = parser.read();
				if( expression.equals(SymbolTable.EOF)) return;
				System.out.println(interpreter.eval(expression).toString());
			}
			catch (LispinException e) {
				e.printStackTrace();
			}			
		} while(true);

	}

}
