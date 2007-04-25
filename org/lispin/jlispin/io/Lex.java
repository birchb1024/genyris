package org.lispin.jlispin.io;

import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Ldouble;
import org.lispin.jlispin.core.Linteger;
import org.lispin.jlispin.core.Lstring;
import org.lispin.jlispin.core.SymbolTable;

public class Lex {

	private static final char COMMENTCHAR = ';';
	private static final char BQUOTECHAR = '`';
	private static final char QUOTECHAR = '\'';
	private static final char COMMACHAR = ',';
	private static final char ATCHAR = '@';
	
	private InStream _input;

	private SymbolTable _symbolTable;

	public Lex(InStream inputSource, SymbolTable symbolTable) {
		_input = inputSource;
		_symbolTable = symbolTable;
	}

	public boolean hasData() throws LexException {
		return _input.hasData();
	}
	public int parseDecimalNumber() throws LexException {
		int collect = 0;
		char ch;

		if (!_input.hasData()) {
			throw new LexException("unexpected end of file");
		}
		while (_input.hasData()) {
			ch = _input.readNext();
			if (ch <= '9' && ch >= '0') {
				collect = 10 * collect + (ch - '0'); // TODO integer
				// overflow
			}
			else {
				_input.unGet(ch);
				return collect;
			}
		}
		return collect;
	}

	public double parseDecimalFraction() throws LexException {
		double collect = 0;
		double power = 10;
		char ch;

		if (!_input.hasData()) {
			throw new LexException("unexpected end of file");
		}
		while (_input.hasData()) {
			ch = _input.readNext();
			if (ch >= '0' && ch <= '9') {
				collect = collect + (ch - '0') / power; // FIXME integer
				// overflow
				power = power * 10;
			}
			else {
				_input.unGet(ch);
				return collect;
			}
		}
		return collect;
	}

	public Exp parseNumber() throws LexException {
		int leading = 0;
		int mantissa = 0;
		int mant_sign = 1;
		int sign = 1;

		char nextChar;

		nextChar = _input.readNext();
		if (nextChar == '-') {
			sign = -1;
		}
		else
			_input.unGet(nextChar);
		leading = parseDecimalNumber();
		if (!this._input.hasData()) {
			return new Linteger(sign * leading);
		}
		nextChar = _input.readNext();
		double fraction = 0;
		if (nextChar == '.') {
			fraction = parseDecimalFraction();
		}
		else {
			_input.unGet(nextChar);
			return new Linteger(sign * leading);
		}

		double floatingValue = sign * (leading + fraction);

		if (!_input.hasData()) {
			return (new Ldouble(floatingValue));
		}
		nextChar = _input.readNext();
		if (nextChar == 'e') {
			nextChar = _input.readNext();
			if (nextChar == '-') {
				mant_sign = -1;
			}
			else {
				_input.unGet(nextChar);
			}
			mantissa = parseDecimalNumber();
			double mantissaRaised = Math.pow(10, mant_sign * mantissa);

			return (new Ldouble(floatingValue * mantissaRaised));
		}
		else {
			_input.unGet(nextChar);
			return (new Ldouble(floatingValue));
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
			if( !_input.hasData()) {
				return SymbolTable.EOF;
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

			case '(' : return SymbolTable.leftParen;
		    case ')' : return SymbolTable.rightParen;
		    case '.' : return SymbolTable.period;
		    case QUOTECHAR : return SymbolTable.raw_quote;
		    case BQUOTECHAR :return SymbolTable.backquote;
			case COMMACHAR : {
				 ch = _input.readNext();
				 if( ch == ATCHAR ) {
					 return SymbolTable.raw_comma_at;
				 }
				 else {
					 _input.unGet(ch);
					 ch = COMMACHAR;
					 return SymbolTable.raw_comma;
				 }
			 }
				// case FUNCCHAR : {
				// ch = input.lgetc();
				// if( ch == '\'' ) {
				// cursym = raw_func;
				// return;
				// }
				// else if( ch == USERCHAR2 ) {
				// cursym = raw_uchar2;
				// return;
				// }
				// else if( ch == USERCHAR1 ) {
				// cursym = raw_uchar1;
				// return;
				// }
				// else if( ch == '|' )
				// {
				// do {
				// ch = input.lgetc();
				// if( ch == '|' )
				// {
				// ch = input.lgetc();
				// if( ch == '#') // comment ended
				// {
				// break;;
				// }
				// }
				// } while( ch != ((int)EOF) );
				// break;
				// }
				// else {
				// input.lungetc(ch);
				// ch = FUNCCHAR;
				// cursym = c_ident();
				// return;
				// }
				// }
				// break;
				//
				// }
				// return;
				//
				//


			default:
				if( (ch >= ' ') && (ch <= '~') ) {
					_input.unGet(ch);
					return parseIdent();
				}
				else {
					throw new LexException("invalid input character");
				}
			}
		} while (forever);
		return SymbolTable.NIL;
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
