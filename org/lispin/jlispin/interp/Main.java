package org.lispin.jlispin.interp;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.InStream;
import org.lispin.jlispin.core.LexException;
import org.lispin.jlispin.core.Lsymbol;
import org.lispin.jlispin.core.ParseException;
import org.lispin.jlispin.core.Parser;
import org.lispin.jlispin.core.StdioInStream;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.core.UngettableInStream;

public class Main {

	public static void main(String[] args) {
		Interpreter interp = new Interpreter();
		
		Environment env = new Environment(null);
		SymbolTable table = new SymbolTable();
		env.defineVariable(new Lsymbol("cons"), new EagerProcedure(env, null, new ApplyCons()));
		env.defineVariable(SymbolTable.quote, SymbolTable.quote);
		env.defineVariable(SymbolTable.NIL, SymbolTable.NIL);

		InStream input = new UngettableInStream( new StdioInStream());
		Parser parser = new Parser(table, input);
		while( true ) {
			System.out.print("JLispin > ");
			try {
				Exp expression;
				Exp result;
				expression = parser.read();
				result = env.eval(expression);
				System.out.println(result.toString());
			}
			catch (LexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
