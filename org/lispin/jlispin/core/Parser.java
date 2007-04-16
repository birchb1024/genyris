package org.lispin.jlispin.core;

public class Parser {
	
	private Lex _lexer;
	private Exp cursym;
	
	public Parser(SymbolTable table, InStream stream) {
		_lexer = new Lex(stream, table);
	}
	
	public void nextsym() throws LexException {
	    cursym = _lexer.nextToken();	
	}
	
	public Exp read() throws LexException, ParseException {
	    Exp retval = SymbolTable.NIL;

	    nextsym();
	    if( cursym.equals(SymbolTable.EOF) ) {
	       retval = SymbolTable.EOF;
	    }
	    else {
	       retval = parseExpression();
	    }
	    return(retval);
	}
	
	public Exp parseList() throws LexException, ParseException {
		Exp tree;

		nextsym();
		if( cursym.equals(SymbolTable.rightParen ) ) {
	      tree = SymbolTable.NIL;
		}
	   else if( cursym.equals(SymbolTable.period) ) {
		   nextsym();
		   tree = parseExpression();
		   nextsym();
	   }
	   else {
	      tree = parseExpression();
	      tree = new Lcons( tree, parseList() );
	   }
	   return( tree );
	}
	
	public Exp parseExpression() throws LexException, ParseException {
		Exp tree = SymbolTable.NIL;

		if (cursym.equals(SymbolTable.EOF)) {
			throw new ParseException("unexpected End of File");
		}
		if (cursym.equals(SymbolTable.rightParen)) {
			throw new ParseException("unexpected End of right paren");
		}
		if (cursym.equals(SymbolTable.leftParen)) {
			tree = parseList();
			if (cursym.equals(SymbolTable.rightParen)) {
				// OK
			} else {
				throw new ParseException("missing right paren");
			}
		}
		else if (cursym == SymbolTable.raw_quote) {
			nextsym();
			tree = new Lcons(SymbolTable.quote, new Lcons(parseExpression(),
					SymbolTable.NIL));
		} else if (cursym == SymbolTable.raw_comma) {
			nextsym();
			tree = new Lcons(SymbolTable.comma, new Lcons(parseExpression(),
					SymbolTable.NIL));
		} else if (cursym == SymbolTable.raw_comma_at) {
			nextsym();
			tree = new Lcons(SymbolTable.comma_at, new Lcons(parseExpression(),
					SymbolTable.NIL));
		}
		// else lif( equal( cursym, rpar ) ) {
		// serr("unexpected )");
		// tree = NIL;
		// }
		// else if( cursym == raw_func ) {
		// nextsym();
		// tree = cons( func_quote, cons( c_s_exp(), NIL) );
		// }
		// else if( cursym == raw_uchar1 ) {
		// nextsym();
		// tree = cons( uchar1, cons( c_s_exp(), NIL) );
		// }
		// else if( cursym == raw_uchar2 ) {
		// nextsym();
		// tree = cons( uchar2, cons( c_s_exp(), NIL) );
		// }
		// else lif( equal( cursym, lpar ) ) {
		// tree = c_m_list();
		// lif( equal( cursym, rpar ) ) {
		// }
		// else {
		// serr(" missing )");
		// }
		// }
		else {
			tree = cursym;
		}
		return (tree);

	}

}
