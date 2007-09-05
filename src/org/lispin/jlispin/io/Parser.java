package org.lispin.jlispin.io;

import org.lispin.jlispin.classes.BuiltinClasses;
import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lcons;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.interp.LispinException;

public class Parser {
    private Lex _lexer;
    private Exp cursym;
    private Exp NIL;

    public Parser(SymbolTable table, InStream stream) {
        _lexer = new Lex(stream, table);
        NIL = table.getNil();
    }

    public void nextsym() throws LexException {
        cursym = _lexer.nextToken();
    }

    public Exp read() throws LispinException {
        Exp retval = NIL;
        nextsym();
        if (cursym.equals(_lexer.EOF)) {
            retval = _lexer.EOF;
        } else {
            retval = parseExpression();
        }
        return (retval);
    }

    public Exp parseList() throws LispinException {
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
              tree = new Lcons( tree, restOfList);
              tree.addClass(BuiltinClasses.PRINTWITHCOLON);
              return tree;
          }
          tree = new Lcons( tree, restOfList);
       }
       return tree ;
    }

    public Exp parseExpression() throws LispinException {
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
