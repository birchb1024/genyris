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
import org.genyris.core.PairEquals;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.StrinG;
import org.genyris.core.Symbol;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;

public class Parser {
	private Lex _lexer;

	private Exp cursym;

	private Exp NIL;

	private Exp _prefix;

	Internable _table;

	private Exp pushback;

	public Parser(Internable table, InStream stream) {
		this(table, stream, Constants.CDRCHAR, Constants.COMMENTCHAR);
	}

	public Parser(Internable table, InStream stream, char cdrCharacter,
			char commentChar) {
		_table = table;
		_lexer = new Lex(stream, table, cdrCharacter, commentChar);
		NIL = table.NIL();
		_prefix = table.PREFIX();
	}

	public void nextsym() throws GenyrisException {
		if (pushback == null) {
			cursym = _lexer.nextToken();
		} else {
			cursym = pushback;
			pushback = null;
		}

	}

	public void pushbacksym(Exp token) throws GenyrisException {
		if (pushback == null) {
			pushback = token;
		} else {
			throw new GenyrisException("Attempt to pushback to many: "
					+ token.toString());
		}

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
				throw new GenyrisException("bad prefix in " + input);
			}
		} catch (AccessException e) {
			return false;
		} catch (ClassCastException e) {
			return false;
		}
		return true;
	}

	private Exp collectPlings(Exp tree) throws GenyrisException {
		Exp old = cursym;
		nextsym();
		if (!(cursym instanceof Symbol)) {
			throw new ParseException("Bad indirection: " + cursym.toString());
		} else if (cursym == _lexer.DYNAMIC_TOKEN) {
			throw new ParseException("Bad indirection: " + cursym.toString());
		}
		tree = new Pair(tree, new Pair(
				new DynamicSymbol((SimpleSymbol) cursym), NIL));
		old = cursym;
		nextsym();
		if (cursym == _lexer.PLING_TOKEN) {
			return collectPlings(tree);
		} else {
			pushbacksym(cursym);
			cursym = old;
		}
		return tree;
	}

	public Exp parseList(Exp lhs) throws GenyrisException {
		Exp tree;
		nextsym();
		if (cursym.equals(_lexer.RIGHT_PAREN_TOKEN)) {
			tree = NIL;
		} else if (cursym.equals(_lexer.RIGHT_SQUARE_TOKEN)) {
			tree = NIL;
		} else if (cursym.equals(_lexer.RIGHT_CURLY_TOKEN)) {
			tree = NIL;
		} else if (cursym.equals(_lexer.CDR_TOKEN)) {
			return _lexer.CDR_TOKEN;
		} else {
			tree = parseExpression();
			Exp old = cursym;
			nextsym();
			if (cursym.equals(_lexer.PLING_TOKEN)) {
				Exp plings = collectPlings(tree);
				Exp restOfList = parseList(plings);
				if (restOfList == _lexer.CDR_TOKEN) {
					nextsym();
					restOfList = parseExpression();
					nextsym();
					tree = new PairEquals(plings, restOfList);
					return tree;
				}
				tree = new Pair(plings, restOfList);
				return tree;
			} else {
				pushbacksym(cursym);
				cursym = old;				
			}
			Exp restOfList = parseList(tree);
			if (restOfList == _lexer.CDR_TOKEN) {
				nextsym();
				restOfList = parseExpression();
				nextsym();
				tree = new PairEquals(tree, restOfList);
				return tree;
			}
			tree = new Pair(tree, restOfList);
		}
		return tree;
	}

	public Exp parseExpression() throws GenyrisException {
		Exp tree = NIL;
		if (cursym.equals(_lexer.PLING_TOKEN)) {
			throw new ParseException("unexpected !");
		}
		if (cursym.equals(_lexer.CDR_TOKEN)) {
			throw new ParseException("unexpected =");
		}
		if (cursym.equals(_lexer.EOF_TOKEN)) {
			throw new ParseException("unexpected End of File");
		}
		if (cursym.equals(_lexer.RIGHT_PAREN_TOKEN)) {
			throw new ParseException("unexpected right paren");
		}
		if (cursym.equals(_lexer.LEFT_PAREN_TOKEN)) {
			tree = parseList(NIL);
			if (!cursym.equals(_lexer.RIGHT_PAREN_TOKEN)) {
				throw new ParseException("missing right paren - found: "
						+ cursym);
			}
		} else if (cursym.equals(_lexer.LEFT_SQUARE_TOKEN)) {
			tree = parseList(NIL);
			tree = new Pair(_table.SQUARE(), tree);
			if (!cursym.equals(_lexer.RIGHT_SQUARE_TOKEN)) {
				throw new ParseException("missing right square brace - found: "
						+ cursym);
			}
		} else if (cursym.equals(_lexer.LEFT_CURLY_TOKEN)) { // TOD DRY - SQUARE
			tree = parseList(NIL);
			tree = new Pair(_table.CURLY(), tree);
			if (!cursym.equals(_lexer.RIGHT_CURLY_TOKEN)) {
				throw new ParseException("missing right curly brace - found: "
						+ cursym);
			}
		} else if (cursym == _lexer.BACKQUOTE_TOKEN) {
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
			if(cursym instanceof SimpleSymbol) {
				tree = new DynamicSymbol((SimpleSymbol) cursym); 
			} else {
				throw new ParseException("period found before non-symbol: " + cursym);
			}
		} else {
			tree = cursym;
		}
		return tree;
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

	public void setUsualPrefixes() throws GenyrisException {
		addPrefix("u", Constants.PREFIX_UTIL);
		addPrefix("web", Constants.PREFIX_WEB);
		addPrefix("email", Constants.PREFIX_EMAIL);
		addPrefix("g", Constants.PREFIX_SYNTAX);
		addPrefix("sys", Constants.PREFIX_SYSTEM);
		addPrefix("ver", Constants.PREFIX_VERSION);
		addPrefix("task", Constants.PREFIX_TASK);
		addPrefix("types", Constants.PREFIX_TYPES);
		addPrefix("date", Constants.PREFIX_DATE);
		addPrefix("java", Constants.PREFIX_JAVA);
	}

}
