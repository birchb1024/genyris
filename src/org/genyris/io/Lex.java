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
import org.genyris.core.Ldouble;
import org.genyris.core.Lstring;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.Symbol;
import org.genyris.core.SymbolTable;
import org.genyris.exception.GenyrisException;

public class Lex {
    private InStream _input;
    private PrefixMapper _mapper;

    private Internable _symbolTable;
    private char _cdrCharacter;

    public Exp quote, EOF, raw_quote, raw_dynamic, raw_backquote, raw_comma_at;
    public Exp raw_comma, comma_at, comma, backquote;
    public Exp leftParen, rightParen, cdr_char;

    private void init(InStream inputSource, Internable table, char cdrChar) {
        _mapper = new PrefixMapper();
        _input = inputSource;
        _symbolTable = table;
        quote = table.internString("quote");
        raw_quote = table.internString("'");
        raw_backquote = table.internString("`");
        raw_comma_at = table.internString(",@");
        raw_comma = table.internString(",");
        raw_dynamic = table.internString(Constants.DYNAMIC_SYMBOL);
        comma_at = table.internString(Constants.COMMA_AT);
        comma = table.internString(Constants.COMMA);
        backquote = table.internString(Constants.TEMPLATE);
        EOF = new SimpleSymbol(Constants.EOF);
        leftParen = table.internString("leftParen");
        rightParen = table.internString("righParen");
        _cdrCharacter = cdrChar;
        cdr_char = table.internString("pair-delimiter");
    }
    public Lex(InStream inputSource, Internable table, char cdrChar) {
        init(inputSource, table, cdrChar);
    }
    public Lex(InStream inputSource, SymbolTable table) {
        init( inputSource,  table, Constants.CDRCHAR);
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
        if(c == _cdrCharacter)
            return false;

        switch (c) {
            case '\f':
            case '\n':
            case '\t':
            case ' ':
            case '\r':
            case '(':
            case ')':
            case Constants.DYNAMICSCOPECHAR2:
            case Constants.COMMENTCHAR:
            case Constants.BQUOTECHAR:
            case Constants.QUOTECHAR:
            case '"':
                return false;
            default:
                return true;
        }
    }

    public Exp parseIdentEscaped() throws GenyrisException {
        char ch;
        String collect = "";
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
                collect += ch;
            }
            else {
                throw new LexException("unexpected end of escaped symbol");
            }
        }
        return _symbolTable.internSymbol(Symbol.symbolFactory(collect, true));
    }

    public Exp parseIdent() throws GenyrisException {
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
        return _symbolTable.internSymbol(_mapper.symbolFactory(collect));
    }

    public Exp nextToken() throws GenyrisException {
        char ch;
        do {
            if (!_input.hasData()) {
                return EOF;
            }
            ch = _input.readNext();
            if( ch == this._cdrCharacter )
                return cdr_char;

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
            case Constants.COMMENTCHAR:
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
            case Constants.DYNAMICSCOPECHAR2:
                return raw_dynamic;
            case Constants.QUOTECHAR:
                return raw_quote;
            case Constants.BQUOTECHAR:
                return backquote;
            case Constants.COMMACHAR: {
                if (_input.hasData()) {
                    ch = _input.readNext();
                    if (ch == Constants.ATCHAR) {
                        return raw_comma_at;
                    }
                    else {
                        _input.unGet(ch);
                        ch = Constants.COMMACHAR;
                        return raw_comma;
                    }
                }
                else {
                    return raw_comma;
                }

            }
            case Constants.SYMBOLESCAPE:
                return parseIdentEscaped();            	
            default:
                if ((ch >= ' ') && (ch <= '~')) {
                    _input.unGet(ch);
                    return parseIdent();
                }
                else {
                    throw new LexException("invalid input character");
                }
            }
        } while (true);
    }

    public Exp parseString() throws LexException {
        char ch;
        String collect = "";
        ch = _input.readNext();
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
                collect += ch;
            }
        }
        return new Lstring(collect);
    }
    public void addprefix(String prefix, String uri) throws GenyrisException {
        _mapper.addprefix(prefix, uri);

    }
}
