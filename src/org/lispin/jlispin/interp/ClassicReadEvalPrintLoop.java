package org.lispin.jlispin.interp;

import java.io.PrintWriter;
import java.io.Writer;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lsymbol;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.format.IndentedFormatter;
import org.lispin.jlispin.io.ConvertEofInStream;
import org.lispin.jlispin.io.InStream;
import org.lispin.jlispin.io.IndentStream;
import org.lispin.jlispin.io.Parser;
import org.lispin.jlispin.io.StdioInStream;
import org.lispin.jlispin.io.UngettableInStream;

public class ClassicReadEvalPrintLoop {

	public static void main(String[] args) {
		Interpreter interpreter;
		Lsymbol NIL;
		try {
			interpreter = new Interpreter();
			NIL = interpreter.getNil();
			interpreter.init(true);
			InStream input = new UngettableInStream(new ConvertEofInStream(
					new IndentStream(
							new UngettableInStream(new StdioInStream()), true)));
			Parser parser = interpreter.newParser(input);
			Writer output = new PrintWriter(System.out);
			IndentedFormatter formatter = new IndentedFormatter(output, 3, interpreter.getNil());
			System.out.println("\n*** JLispin is listening...");
			Exp expression = null;
			do {
				try {
					System.out.print("\n> ");
					expression = parser.read();
					;
					if (expression.equals(interpreter.getSymbolTable().internString("EOF"))) {
						System.out.println("Bye..");
						break;
					}

					Exp result = interpreter.evalInGlobalEnvironment(expression);

					result.acceptVisitor(formatter);
					
					output.write(" ;");
					Exp klasses = result.getClasses(NIL);
					while(klasses != NIL){
						Environment klass = (Environment) klasses.car();
						output.write(" " + klass.lookupVariableShallow(SymbolTable.classname).toString());
						klasses = klasses.cdr();
					}
					output.flush();
				}
				catch (LispinException e) {
					System.out.println("*** Error: " + e.getMessage());
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			} while (true);
		}
		catch (LispinException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.exit(-1);
		}

	}

}
