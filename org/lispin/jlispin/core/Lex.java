package org.lispin.jlispin.core;

public class Lex {

	private static final char COMMENTCHAR = ';';

	private static final char BQUOTECHAR = '`';

	private static final char QUOTECHAR = '\'';

	private InStream _input;

	private SymbolTable _symbolTable;

	public Lex(InStream inputSource, SymbolTable symbolTable) {
		_input = inputSource;
		_symbolTable = symbolTable;
	}

	public int parseDecimalNumber() throws LexException {
		int collect = 0;
		char ch;

		if (!_input.hasData()) {
			throw new LexException("unexpected end of file");
		}
		while (_input.hasData()) {
			ch = _input.lgetc();
			if (ch <= '9' && ch >= '0') {
				collect = 10 * collect + (ch - '0'); // FIXME integer
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
			ch = _input.lgetc();
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

		nextChar = _input.lgetc();
		if (nextChar == '-') {
			sign = -1;
		}
		else
			_input.unGet(nextChar);
		leading = parseDecimalNumber();
		if (!this._input.hasData()) {
			return new Linteger(sign * leading);
		}
		nextChar = _input.lgetc();
		double fraction = 0;
		if (nextChar == '.') {
			fraction = parseDecimalFraction();
		}

		double floatingValue = sign * (leading + fraction);

		if (!_input.hasData()) {
			return (new Ldouble(floatingValue));
		}
		nextChar = _input.lgetc();
		if (nextChar == 'e') {
			nextChar = _input.lgetc();
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
			return (new Linteger(sign * leading));
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
			ch = _input.lgetc();
			if (isIdentCharacter(ch)) {
				if (ch == '\\')
					ch = _input.lgetc();
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
			ch = _input.lgetc();
			switch (ch) {
			case '\f':
			case '\n':
			case '\t':
			case ' ':
			case '\r':
				break;
			case '-':
				ch = _input.lgetc();
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

				case COMMENTCHAR :
					while( _input.hasData()) {
						ch = _input.lgetc();
						if( ch == '\n' ) {
							break;
						}							
					}
					break;

				
				
			case '"' :
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

				// case '(' : return lpar;
				// case ')' : return rpar;
				// case '.' : return period;
				// case QUOTECHAR : return raw_quote;
				// case BQUOTECHAR :return back_quote;
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
				// case COMMACHAR : {
				// ch = input.lgetc();
				// if( ch == ATCHAR ) {
				// cursym = raw_comma_at;
				// }
				// else {
				// input.lungetc(ch);
				// ch = COMMACHAR;
				// cursym = raw_comma;
				// }
				// }
				// return;
				//
				//

				//
				// case EOF :
				// cursym = beof;
				// return;
				//
				//
				default:
						_input.unGet(ch);
						return parseIdent();

				// cursym = c_ident();
				// return;
				// }
				// else {
				// c_warn_header("Ignoring Illegal Character", linecount);
				//		                fprintf(consoleStderr,"Ignoring Illegal Character: %d %c\n",ch, ch);
				//		            }
				//			 }
			}
		} while (forever);
		return Symbol.NIL;
	}

public Exp parseString() {
	return null;
}
//		EXP LispinLex::c_string()
//		{
//		int i=0;
//		char string[LEN_STRING] = "";
//		EXP first = NIL;
//		EXP tmp = NIL,last = NIL;
//
//			while( (ch = input.lgetc()) != '"') {
//				if( ch == ((int)EOF))
//					break;
//				if( ch == '\134') {
//					int ch2;
//
//					ch2 = input.lgetc();
//					if( ch2 == EOF) break;
//					switch( ch2) {
//						case 'n' : ch = '\n'; break;
//						case 'r' : ch = '\r'; break;
//						case 't' : ch = '\t'; break;
//						case 'f' : ch = '\f'; break;
//						case '"' : ch = '\"'; break;
//						case 'e' : ch = '\033'; break;
//						case '\134' : ch = '\134'; break; /* ? */
//						default: ch = ch2; /* ungetc(ch2,input); */ break;
//					}
//				}
//				if( i < LEN_STRING -1)
//					string[i++] = (char)ch;
//				else {
//					string[i] = 0;
//					tmp = newbscell(newscell(string));
//					tmp->reg.zpair.zcdr = NIL;
//					if( first == NIL )  {
//						first = tmp;
//						last = tmp;
//					}
//					last->reg.zpair.zcdr = tmp;
//					last = tmp;
//					i = 0;
//					string[i++] = (char)ch;
//				}
//			}
//			string[i] = 0;
//			if( first == NIL ) {
//				first = newscell(string);
//			}
//			else if( i != 0 ) {
//				tmp = newbscell(newscell(string));
//				tmp->reg.zpair.zcdr = NIL;
//				last->reg.zpair.zcdr = tmp;
//			}
//			return( first );
//		



}
