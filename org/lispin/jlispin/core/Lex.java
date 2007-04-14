package org.lispin.jlispin.core;

public class Lex {

	private InStream _input;

	public Lex(InStream inputSource) {
		_input = inputSource;
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
				collect = 10 * collect + (ch - '0'); // FIXME integer overflow
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
				collect = collect + (ch - '0') / power; // FIXME integer overflow
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
			return new Linteger(sign*leading);
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
}
