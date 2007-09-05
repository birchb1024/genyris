package org.lispin.jlispin.io;

import java.math.BigDecimal;

import org.lispin.jlispin.core.Bignum;
import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Ldouble;
import org.lispin.jlispin.core.Lstring;
import org.lispin.jlispin.core.SymbolTable;

public class Lex {
	private Exp NIL;
	private static final char COMMENTCHAR = ';';
	private static final char BQUOTECHAR = '`';
	private static final char QUOTECHAR = '\'';
	private static final char COMMACHAR = ',';
	private static final char ATCHAR = '@';
	private static final char CDRCHAR = ':';
	private InStream _input;

	private SymbolTable _symbolTable;
    public Exp quote, EOF, raw_quote, raw_backquote, raw_comma_at;
    public Exp raw_comma, comma_at, comma, backquote;
    public Exp leftParen, rightParen, cdr_char;

	public Lex(InStream inputSource, SymbolTable table) {
		_input = inputSource;
		_symbolTable = table;
		NIL = table.getNil();
        quote = table.internString("quote");
        raw_quote = table.internString("'");
        raw_backquote = table.internString("`");
        raw_comma_at = table.internString(",@");
        raw_comma = table.internString(",");
        comma_at = table.internString("comma-at");
        comma = table.internString("comma");
        backquote = table.internString("backquote");
        EOF = table.internString("EOF");
        leftParen = table.internString("leftParen");
        rightParen = table.internString("righParen");
        cdr_char = table.internString("pair-delimiter");
	}

	public boolean hasData() throws LexException {
		return _input.hasData();
	}

	public BigDecimal parseDecimalNumber() throws LexException {
		StringBuffer collect = new StringBuffer();
		char ch;
		if (!_input.hasData()) {
			throw new LexException("unexpected end of file");
		}
		ch = _input.readNext();
		if (ch == '-') {
			collect.append(ch);
		}
		else {
			_input.unGet(ch);
		}
		while (_input.hasData()) {
			ch = _input.readNext();
			if ((ch <= '9' && ch >= '0') || (ch == '.')) {
				collect.append(ch);
			}
			else {
				_input.unGet(ch);
				break;
			}
		}
		try {
			return new BigDecimal(collect.toString());
		}
		catch (NumberFormatException e) {
			throw new LexException(e.getMessage());
		}
	}

	public Exp parseNumber() throws LexException {
		BigDecimal floatingValue;
		char nextChar;
		floatingValue = parseDecimalNumber();
		if (!this._input.hasData()) {
			return new Bignum(floatingValue);
		}

		BigDecimal mantissa;
		nextChar = _input.readNext();
		if (nextChar == 'e' || nextChar == 'E') {
			mantissa = parseDecimalNumber();
			double mantissaRaised = Math.pow(10, mantissa.intValue());
			return (new Ldouble(floatingValue.doubleValue() * mantissaRaised));
		}
		else {
			_input.unGet(nextChar);
			return new Bignum(floatingValue);
		}
	}

	// predicate
	private boolean isIdentCharacter(char c) {
		switch (c) {
		case '\f':
		case '\n':
		case '\t':
		case ' ':
		case '\r':
		case '(':
		case ')':
		case COMMENTCHAR:
		case BQUOTECHAR:
		case QUOTECHAR:
		case CDRCHAR:
		case '"':
			return false;
		default:
			return true;
		}
	}

	public Exp parseIdent() throws LexException {
		char ch;
		String collect = "";
		if (!_input.hasData()) {
			throw new LexException("unexpected end of file");
		}
		while (_input.hasData()) {
			ch = _input.readNext();
			if (isIdentCharacter(ch)) {
				if (ch == '\\')
					ch = _input.readNext();
				collect += ch;
			}
			else {
				_input.unGet(ch);
				break;
			}
		}
		return _symbolTable.internString(collect);
	}

	public Exp nextToken() throws LexException {
		char ch;
		boolean forever = true;
		do {
			if (!_input.hasData()) {
				return EOF;
			}
			ch = _input.readNext();
			switch (ch) {
			case '\f':
			case '\n':
			case '\t':
			case ' ':
			case '\r':
				break;
			case '-':
				if (!_input.hasData()) {
					return EOF;
				}
				ch = _input.readNext();
				if (ch >= '0' && ch <= '9') {
					_input.unGet(ch);
					_input.unGet('-');
					return parseNumber();
				}
				else {
					_input.unGet(ch);
					_input.unGet('-');
					return parseIdent();
				}
			case COMMENTCHAR:
				while (_input.hasData()) {
					ch = _input.readNext();
					if (ch == '\n') {
						break;
					}
				}
				break;
			case '"':
				_input.unGet(ch);
				return parseString();
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				_input.unGet(ch);
				return parseNumber();
			case '(':
				return leftParen;
			case ')':
				return rightParen;
			case ':': // TODO DRY
				return cdr_char;
			case QUOTECHAR:
				return raw_quote;
			case BQUOTECHAR:
				return backquote;
			case COMMACHAR: {
				if (_input.hasData()) {
					ch = _input.readNext();
					if (ch == ATCHAR) {
						return raw_comma_at;
					}
					else {
						_input.unGet(ch);
						ch = COMMACHAR;
						return raw_comma;
					}
				}
				else {
					return raw_comma;
				}

			}
			default:
				if ((ch >= ' ') && (ch <= '~')) {
					_input.unGet(ch);
					return parseIdent();
				}
				else {
					throw new LexException("invalid input character");
				}
			}
		} while (forever);
		return NIL;
	}

	public Exp parseString() throws LexException {
		char ch;
		String collect = "";
		if (!_input.hasData()) {
			throw new LexException("empty string");
		}
		ch = _input.readNext();
		if (ch != '\"') {
			throw new LexException("malformed string");
		}
		while (_input.hasData()) {
			ch = _input.readNext();
			if (ch == '"') {
				break;
			}
			else {
				if (ch == '\\') {
					if (!_input.hasData())
						throw new LexException("unexpected end of file");
					char ch2 = _input.readNext();
					switch (ch2) {
					case 'n':
						ch = '\n';
						break;
					case 'r':
						ch = '\r';
						break;
					case 't':
						ch = '\t';
						break;
					case 'f':
						ch = '\f';
						break;
					case '"':
						ch = '\"';
						break;
					case 'e':
						ch = '\033';
						break;
					case '\\':
						ch = '\\';
						break;
					default:
						ch = ch2;
						break;
					}
				}
				collect += ch;
			}
		}
		return new Lstring(collect);
	}
}
