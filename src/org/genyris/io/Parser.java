// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io;

import org.genyris.core.Constants;
import org.genyris.core.DynamicSymbol;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.Pair;
import org.genyris.core.PairWithcolons;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.StrinG;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;

public class Parser {
	private Lex _lexer;

	private Exp cursym;

	private Exp NIL;

	private Exp _prefix;

	Internable _table;

	public Parser(Internable table, InStream stream) {
		this(table, stream, Constants.CDRCHAR);
	}

	public Parser(Internable table, InStream stream, char cdrCharacter) {
		_table = table;
		_lexer = new Lex(stream, table, cdrCharacter);
		NIL = table.NIL();
		_prefix = table.PREFIX();
	}

	public void nextsym() throws GenyrisException {
		cursym = _lexer.nextToken();
	}

	private Exp readAux() throws GenyrisException {
		Exp retval = NIL;
		nextsym();
		if (cursym.equals(_lexer.EOF_TOKEN)) {
			retval = _table.EOF();
		} else {
			retval = parseExpression();
		}
		return (retval);
	}

	public Exp read() throws GenyrisException {
		Exp input = readAux();
		while (processOrder(input)) {
			input = readAux();
		}
		return input;
	}

	private boolean processOrder(Exp input) throws GenyrisException {
		// process parser orders
		try {
			if (!input.isPair()) {
				return false;
			}
			if (input.car() != _prefix) {
				return false;
			}
			Exp arg0 = input.cdr().car();
			if (input.cdr().cdr() != NIL) {
				StrinG arg1 = (StrinG) input.cdr().cdr().car();
				_lexer.addprefix(((SimpleSymbol) arg0).getPrintName(), arg1
						.toString());
			} else {
				// Use empty string as prefix
				_lexer.addprefix("", arg0.toString());
			}
		} catch (AccessException e) {
			return false;
		} catch (ClassCastException e) {
			return false;
		}
		return true;
	}

	public Exp parseList() throws GenyrisException {
		Exp tree;
		nextsym();
		if (cursym.equals(_lexer.RIGHT_PAREN_TOKEN)) {
			tree = NIL;
		} else if(cursym.equals(_lexer.RIGHT_SQUARE_TOKEN)) {
			tree = NIL;
		} else if(cursym.equals(_lexer.RIGHT_CURLY_TOKEN)) {
			tree = NIL;
		} else if (cursym.equals(_lexer.COLON_TOKEN)) {
			return _lexer.COLON_TOKEN;
		} else {
			tree = parseExpression();
			Exp restOfList = parseList();
			if (restOfList == _lexer.COLON_TOKEN) {
				nextsym();
				restOfList = parseExpression();
				nextsym();
				tree = new PairWithcolons(tree, restOfList);
				return tree;
			}
			tree = new Pair(tree, restOfList);
		}
		return tree;
	}

	public Exp parseExpression() throws GenyrisException {
		Exp tree = NIL;
		if (cursym.equals(_lexer.COLON_TOKEN)) {
			throw new ParseException("unexpected colon");
		}
		if (cursym.equals(_lexer.EOF_TOKEN)) {
			throw new ParseException("unexpected End of File");
		}
		if (cursym.equals(_lexer.RIGHT_PAREN_TOKEN)) {
			throw new ParseException("unexpected right paren");
		}
		if (cursym.equals(_lexer.LEFT_PAREN_TOKEN)) {
			tree = parseList();
			if (!cursym.equals(_lexer.RIGHT_PAREN_TOKEN)) {
				throw new ParseException("missing right paren - found: " + cursym);
			}
		} else if (cursym.equals(_lexer.LEFT_SQUARE_TOKEN)) {
			tree = parseList();
			tree = new Pair(_table.SQUARE(), tree);
			if (!cursym.equals(_lexer.RIGHT_SQUARE_TOKEN)) {
				throw new ParseException("missing right square brace - found: " + cursym);
			}						
		} else if (cursym.equals(_lexer.LEFT_CURLY_TOKEN)) { // TOD DRY - SQUARE
			tree = parseList();
			tree = new Pair(_table.CURLY(), tree);
			if (!cursym.equals(_lexer.RIGHT_CURLY_TOKEN)) {
				throw new ParseException("missing right curly brace - found: " + cursym);
			}						
		} else	if (cursym == _lexer.BACKQUOTE_TOKEN) {
			nextsym();
			tree = new Pair(_table.TEMPLATE(), new Pair(parseExpression(), NIL));
		} else if (cursym == _lexer.QUOTE_TOKEN) {
			nextsym();
			tree = new Pair(_table.QUOTE(), new Pair(parseExpression(), NIL));
		} else if (cursym == _lexer.COMMA_TOKEN) {
			nextsym();
			tree = new Pair(_table.COMMA(), new Pair(parseExpression(), NIL));
		} else if (cursym == _lexer.COMMA_AT_TOKEN) {
			nextsym();
			tree = new Pair(_table.COMMA_AT(), new Pair(parseExpression(), NIL));
		} else if (cursym == _lexer.DYNAMIC_TOKEN) {
			nextsym();
			tree = new DynamicSymbol((SimpleSymbol) cursym); // todo validate
																// cast
		} else {
			tree = cursym;
		}
		return (tree);
	}

	public void addPrefix(String prefix, String expansion)
			throws GenyrisException {
		_lexer.addprefix(prefix, expansion);
	}

	public static Exp parseSingleExpressionFromString(Internable table,
			String script) throws GenyrisException {
		InStream input = new UngettableInStream(new StringInStream(script));
		Parser parser = new Parser(table, input);
		Exp expression = parser.read();
		return expression;
	}

	public void resetAfterError() {
		_lexer.resetAfterError();	
	}
}
