// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io;

import org.genyris.core.Constants;
import org.genyris.core.Exp;
import org.genyris.core.Lcons;
import org.genyris.core.LconsWithcolons;
import org.genyris.core.Lstring;
import org.genyris.core.SimpleSymbol;
import org.genyris.core.SymbolTable;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;

public class Parser {
    private Lex _lexer;
    private Exp cursym;
    private Exp NIL, GLOBAL_EOF;
    private Exp _prefix;

    public Parser(SymbolTable table, InStream stream) {
        this(table, stream, Constants.CDRCHAR);
    }

    public Parser(SymbolTable table, InStream stream, char cdrCharacter) {
        _lexer = new Lex(stream, table, cdrCharacter);
        NIL = table.getNil();
        GLOBAL_EOF = table.internPlainString("EOF");
        _prefix = table.internPlainString(Constants.PREFIX);
    }

    public void nextsym() throws GenyrisException {
        cursym = _lexer.nextToken();
    }

    private Exp readAux() throws GenyrisException {
        Exp retval = NIL;
        nextsym();
        if (cursym.equals(_lexer.EOF)) {
            retval = GLOBAL_EOF;
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
            if (!input.listp()) {
                return false;
            }
            if (input.car() != _prefix) {
                return false;
            }
            Exp arg0 = input.cdr().car();
            if(input.cdr().cdr() != NIL) { 
                Lstring arg1 = (Lstring)input.cdr().cdr().car();
                _lexer.addprefix(((SimpleSymbol)arg0).getPrintName(), arg1.toString());
            }
            else {
                // Use empty string as prefix
            	_lexer.addprefix("", arg0.toString());                
            }
        }
        catch (AccessException e) {
            return false;
        }
        catch (ClassCastException e) {
            return false;
        }
        return true;
    }

    public Exp parseList() throws GenyrisException {
        Exp tree;
        nextsym();
        if (cursym.equals(_lexer.rightParen)) {
            tree = NIL;
        } else if (cursym.equals(_lexer.cdr_char)) {
            return _lexer.cdr_char;
        } else {
            tree = parseExpression();
            Exp restOfList = parseList();
            if (restOfList == _lexer.cdr_char) {
                nextsym();
                restOfList = parseExpression();
                nextsym();
                tree = new LconsWithcolons(tree, restOfList);
                return tree;
            }
            tree = new Lcons(tree, restOfList);
        }
        return tree;
    }

    public Exp parseExpression() throws GenyrisException {
        Exp tree = NIL;
        if (cursym.equals(_lexer.cdr_char)) {
            throw new ParseException("unexpected colon");
        }
        if (cursym.equals(_lexer.EOF)) {
            throw new ParseException("unexpected End of File");
        }
        if (cursym.equals(_lexer.rightParen)) {
            throw new ParseException("unexpected right paren");
        }
        if (cursym.equals(_lexer.leftParen)) {
            tree = parseList();
            if (!cursym.equals(_lexer.rightParen)) {
                throw new ParseException("missing right paren");
            }
        } else if (cursym == _lexer.raw_backquote) {
            nextsym();
            tree = new Lcons(_lexer.backquote, new Lcons(parseExpression(), NIL));
        } else if (cursym == _lexer.raw_quote) {
            nextsym();
            tree = new Lcons(_lexer.quote, new Lcons(parseExpression(), NIL));
        } else if (cursym == _lexer.raw_comma) {
            nextsym();
            tree = new Lcons(_lexer.comma, new Lcons(parseExpression(), NIL));
        } else if (cursym == _lexer.raw_comma_at) {
            nextsym();
            tree = new Lcons(_lexer.comma_at, new Lcons(parseExpression(), NIL));
        } else if (cursym == _lexer.raw_dynamic) {
            nextsym();
            tree = new Lcons(_lexer.raw_dynamic, new Lcons(parseExpression(), NIL));
        } else {
            tree = cursym;
        }
        return (tree);
    }
    
    public void addPrefix(String prefix, String expansion) throws GenyrisException {
    	_lexer.addprefix(prefix, expansion);
    }
}
