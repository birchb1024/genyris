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
import org.genyris.core.SymbolTable;
import org.genyris.exception.GenyrisException;

public class Parser {
    private Lex _lexer;
    private Exp cursym;
    private Exp NIL;

    public Parser(SymbolTable table, InStream stream) {
        _lexer = new Lex(stream, table, Constants.CDRCHAR);
        NIL = table.getNil();
    }
    public Parser(SymbolTable table, InStream stream, char cdrCharacter) {
        _lexer = new Lex(stream, table, cdrCharacter);
        NIL = table.getNil();
    }

    public void nextsym() throws LexException {
        cursym = _lexer.nextToken();
    }

    public Exp read() throws GenyrisException {
        Exp retval = NIL;
        nextsym();
        if (cursym.equals(_lexer.EOF)) {
            retval = _lexer.EOF;
        } else {
            retval = parseExpression();
        }
        return (retval);
    }

    public Exp parseList() throws GenyrisException {
        Exp tree;

        nextsym();
        if( cursym.equals(_lexer.rightParen ) ) {
          tree = NIL;
        }
       else if( cursym.equals(_lexer.cdr_char) ) {
           return _lexer.cdr_char;
       }
       else {
          tree = parseExpression();
          Exp restOfList = parseList();
          if(restOfList == _lexer.cdr_char) {
              nextsym();
              restOfList = parseExpression();
              nextsym();
              tree = new LconsWithcolons( tree, restOfList);
              return tree;
          }
          tree = new Lcons( tree, restOfList);
       }
       return tree ;
    }

    public Exp parseExpression() throws GenyrisException {
        Exp tree = NIL;
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
        } else {
            tree = cursym;
        }
        return (tree);
    }
}
