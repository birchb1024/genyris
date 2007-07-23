package org.lispin.jlispin.io;

import org.lispin.jlispin.classes.BuiltinClasses;
import org.lispin.jlispin.core.Exp;
import org.lispin.jlispin.core.Lcons;
import org.lispin.jlispin.core.SymbolTable;
import org.lispin.jlispin.interp.LispinException;

public class Parser {
    private Lex _lexer;
    private Exp cursym;

    public Parser(SymbolTable table, InStream stream) {
        _lexer = new Lex(stream, table);
    }

    public void nextsym() throws LexException {
        cursym = _lexer.nextToken();
    }

    public Exp read() throws LispinException {
        Exp retval = SymbolTable.NIL;
        nextsym();
        if (cursym.equals(SymbolTable.EOF)) {
            retval = SymbolTable.EOF;
        } else {
            retval = parseExpression();
        }
        return (retval);
    }

    public Exp parseList() throws LispinException {
        Exp tree;

        nextsym();
        if( cursym.equals(SymbolTable.rightParen ) ) {
          tree = SymbolTable.NIL;
        }
       else if( cursym.equals(SymbolTable.cdr_char) ) {
           return SymbolTable.cdr_char;
       }
       else {
          tree = parseExpression();
          Exp restOfList = parseList();
          if(restOfList == SymbolTable.cdr_char) {
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
        Exp tree = SymbolTable.NIL;
        if (cursym.equals(SymbolTable.EOF)) {
            throw new ParseException("unexpected End of File");
        }
        if (cursym.equals(SymbolTable.rightParen)) {
            throw new ParseException("unexpected right paren");
        }
        if (cursym.equals(SymbolTable.leftParen)) {
            tree = parseList();
            if (!cursym.equals(SymbolTable.rightParen)) {
                throw new ParseException("missing right paren");
            }
        } else if (cursym == SymbolTable.raw_backquote) {
            nextsym();
            tree = new Lcons(SymbolTable.backquote, new Lcons(parseExpression(), SymbolTable.NIL));
        } else if (cursym == SymbolTable.raw_quote) {
            nextsym();
            tree = new Lcons(SymbolTable.quote, new Lcons(parseExpression(), SymbolTable.NIL));
        } else if (cursym == SymbolTable.raw_comma) {
            nextsym();
            tree = new Lcons(SymbolTable.comma, new Lcons(parseExpression(), SymbolTable.NIL));
        } else if (cursym == SymbolTable.raw_comma_at) {
            nextsym();
            tree = new Lcons(SymbolTable.comma_at, new Lcons(parseExpression(), SymbolTable.NIL));
        } else {
            tree = cursym;
        }
        return (tree);
    }
}
