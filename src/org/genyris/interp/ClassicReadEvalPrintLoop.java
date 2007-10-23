package org.genyris.interp;

import java.io.PrintWriter;
import java.io.Writer;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Lsymbol;
import org.genyris.format.IndentedFormatter;
import org.genyris.io.ConvertEofInStream;
import org.genyris.io.InStream;
import org.genyris.io.IndentStream;
import org.genyris.io.Parser;
import org.genyris.io.StdioInStream;
import org.genyris.io.UngettableInStream;

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
			IndentedFormatter formatter = new IndentedFormatter(output, 3, interpreter);
			System.out.println("\n*** JLispin is listening...");
			Exp expression = null;
			do {
				try {
					System.out.print("\n> ");
					expression = parser.read();
					;
					if (expression.equals(interpreter.getSymbolTable().internString(Constants.EOF))) {
						System.out.println("Bye..");
						break;
					}

					Exp result = interpreter.evalInGlobalEnvironment(expression);

					result.acceptVisitor(formatter);
					
					output.write(" ;");
					Exp klasses = result.getClasses(NIL);
					while(klasses != NIL){
						Environment klass = (Environment) klasses.car();
						output.write(" " + klass.lookupVariableShallow(interpreter.getSymbolTable().internString(Constants.CLASSNAME)).toString());
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
