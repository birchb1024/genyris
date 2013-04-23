// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io;

import java.math.BigDecimal;

import org.genyris.core.Bignum;
import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Internable;
import org.genyris.core.StrinG;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.Symbol;
import org.genyris.core.SymbolTable;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;

public class Lex {
	private InStream _input;

	private PrefixMapper _mapper;

	private Internable _symbolTable;

	private char _cdrCharacter, _commentCharacter;

	public Symbol EOF_TOKEN, QUOTE_TOKEN, DYNAMIC_TOKEN, BACKQUOTE_TOKEN,
			COMMA_AT_TOKEN;

	public Symbol COMMA_TOKEN;

	public Symbol LEFT_PAREN_TOKEN, RIGHT_PAREN_TOKEN, CDR_TOKEN;
	public Symbol LEFT_SQUARE_TOKEN, RIGHT_SQUARE_TOKEN;
	public Symbol LEFT_CURLY_TOKEN, RIGHT_CURLY_TOKEN;
	public Symbol PLING_TOKEN;

	private void init(InStream inputSource, Internable table, char cdrChar, char commentChar) {
		_mapper = new PrefixMapper();
		_input = inputSource;
		_symbolTable = table;
		_cdrCharacter = cdrChar;
		_commentCharacter = commentChar;

		QUOTE_TOKEN = new SimpleSymbol("QuoteToken");
		BACKQUOTE_TOKEN = new SimpleSymbol("BackquoteToken");
		COMMA_AT_TOKEN = new SimpleSymbol("COMMA_AT_TOKEN");
		COMMA_TOKEN = new SimpleSymbol("COMMA_TOKEN");
		DYNAMIC_TOKEN = new SimpleSymbol("DYNAMIC_TOKEN");
		EOF_TOKEN = new SimpleSymbol("EOF_TOKEN");
		LEFT_PAREN_TOKEN = new SimpleSymbol("leftParenToken");
		RIGHT_PAREN_TOKEN = new SimpleSymbol("righParenToken");
		LEFT_SQUARE_TOKEN = new SimpleSymbol("leftSquareToken");
		RIGHT_SQUARE_TOKEN = new SimpleSymbol("rightSquareToken");
		LEFT_CURLY_TOKEN = new SimpleSymbol("leftCurlyToken");
		RIGHT_CURLY_TOKEN = new SimpleSymbol("rightCurlyToken");
		CDR_TOKEN = new SimpleSymbol("pair-delimiterToken");
		PLING_TOKEN = new SimpleSymbol("plingToken");
	}

	public Lex(InStream inputSource, Internable table, char cdrChar, char commentChar) {
		init(inputSource, table, cdrChar, commentChar);
	}

	public Lex(InStream inputSource, SymbolTable table) {
		init(inputSource, table, Constants.CDRCHAR, Constants.COMMENTCHAR);
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
		} else {
			_input.unGet(ch);
		}
		while (_input.hasData()) {
			ch = _input.readNext();
			if ((ch <= '9' && ch >= '0') || (ch == '.')) {
				collect.append(ch);
			} else {
				_input.unGet(ch);
				break;
			}
		}
		try {
			return new BigDecimal(collect.toString());
		} catch (NumberFormatException e) {
			throw new LexException("NumberFormatException on " + collect.toString());
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
			return (new Bignum(floatingValue.doubleValue() * mantissaRaised));
		} else {
			_input.unGet(nextChar);
			return new Bignum(floatingValue);
		}
	}

	private boolean isNotIdentEscapeChar(char c) {
		switch (c) {
		case '\f':
		case '\n':
		case '\t':
		case '\r':
			return false;
		default:
			return true;
		}
	}

	private boolean isIdentCharacter(char c) {
		if (c == _cdrCharacter)
			return false;

		if (c == _commentCharacter)
			return false;

		switch (c) {
		case '\f':
		case '\n':
		case '\t':
		case ' ':
		case '\r':
		case '(':
		case ')':
		case '[':
		case ']':
		case '{':
		case '}':
		case Constants.DYNAMICSCOPECHAR2:
		case Constants.BQUOTECHAR:
		case Constants.QUOTECHAR:
		case '\'':
		case '"':
		case '!':
			return false;
		default:
			return true;
		}
	}

	public Exp parseIdentEscaped() throws GenyrisException {
		char ch;
		StringBuffer collect = new StringBuffer();
		if (!_input.hasData()) {
			throw new LexException("unexpected end of file");
		}
		while (_input.hasData()) {
			ch = _input.readNext();
			if (isNotIdentEscapeChar(ch)) {
				if (ch == Constants.SYMBOLESCAPE)
					break;
				if (ch == '\\')
					ch = _input.readNext();
				collect.append(ch);
			} else {
				throw new LexException("unexpected end of escaped symbol");
			}
		}
		return _symbolTable.internSymbol(Symbol.symbolFactory(collect.toString(), true));
	}

	public Exp parseIdent() throws GenyrisException {
		char ch;
		StringBuffer collect = new StringBuffer("");
		if (!_input.hasData()) {
			throw new LexException("unexpected end of file");
		}
		while (_input.hasData()) {
			ch = _input.readNext();
			if (isIdentCharacter(ch)) {
				if (ch == '\\')
					ch = _input.readNext();
				collect.append(ch);
			} else {
				_input.unGet(ch);
				break;
			}
		}
		return _symbolTable.internSymbol(_mapper.symbolFactory(collect.toString()));
	}

	public Exp nextToken() throws GenyrisException {
		char ch;
		do {
			if (!_input.hasData()) {
				return EOF_TOKEN;
			}
			ch = _input.readNext();
			if (ch == this._cdrCharacter)
				return CDR_TOKEN;

			if (ch == this._commentCharacter) {
				while (_input.hasData()) {
					ch = _input.readNext();
					if (ch == '\n') {
						break;
					}
				}
			}

			switch (ch) {
			case '\f':
			case '\n':
			case '\t':
			case ' ':
			case '\r':
				break;
			case '-':
				if (!_input.hasData()) {
					return EOF_TOKEN;
				}
				ch = _input.readNext();
				if (ch >= '0' && ch <= '9') {
					_input.unGet(ch);
					_input.unGet('-');
					return parseNumber();
				} else {
					_input.unGet(ch);
					_input.unGet('-');
					return parseIdent();
				}
			case '"':
				_input.unGet(ch);
				return parseString('"');
			case '\'':
				_input.unGet(ch);
				return parseString('\'');
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
			case '!':
				return PLING_TOKEN;
			case '(':
				return LEFT_PAREN_TOKEN;
			case ')':
				return RIGHT_PAREN_TOKEN;
			case '[':
				return LEFT_SQUARE_TOKEN;
			case ']':
				return RIGHT_SQUARE_TOKEN;
			case '{':
				return LEFT_CURLY_TOKEN;
			case '}':
				return RIGHT_CURLY_TOKEN;
			case Constants.DYNAMICSCOPECHAR2:
				return DYNAMIC_TOKEN;
			case Constants.QUOTECHAR:
				return QUOTE_TOKEN;
			case Constants.BQUOTECHAR:
				return BACKQUOTE_TOKEN;
			case Constants.COMMACHAR: {
				if (_input.hasData()) {
					ch = _input.readNext();
					if (ch == Constants.ATCHAR) {
						return COMMA_AT_TOKEN;
					} else {
						_input.unGet(ch);
						ch = Constants.COMMACHAR;
						return COMMA_TOKEN;
					}
				} else {
					return COMMA_TOKEN;
				}

			}
			case Constants.SYMBOLESCAPE:
				return parseIdentEscaped();
			default:
				if ((ch >= ' ') && (ch <= '~')) {
					_input.unGet(ch);
					return parseIdent();
				} else {
					throw new LexException("invalid input character");
				}
			}
		} while (true);
	}

	public Exp parseString(char quotechar) throws LexException {
		char ch;
		StringBuffer collect = new StringBuffer();
		ch = _input.readNext();
		while (_input.hasData()) {
			ch = _input.readNext();
			if (ch == quotechar) {
				break;
			} else {
				if (ch == '\\') {
					if (!_input.hasData())
						throw new LexException("unexpected end of file");
					char ch2 = _input.readNext();
					switch (ch2) {
					case 'a':
						ch = '\u0007';
						break;
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
				collect.append(ch);
			}
		}
		return new StrinG(collect, quotechar);
	}

	public void addprefix(String prefix, String uri) throws GenyrisException {
		_mapper.addprefix(prefix, uri);

	}

	public void resetAfterError() {
		_input.resetAfterError();
	}

    public void withinExpression(Environment env) {
        _input.withinExpression(env);       
    }

    public void beginningExpression() {
        _input.beginningExpression();
    }

    public int getLineNumber() {
        return _input.getLineNumber();
    }


}
