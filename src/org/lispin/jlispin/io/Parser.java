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
    private SymbolTable _symbolTable;

    public Parser(SymbolTable table, InStream stream) {
        _lexer = new Lex(stream, table);
        NIL = table.getNil();
        _symbolTable = table;
    }

    public void nextsym() throws LexException {
        cursym = _lexer.nextToken();
    }

    public Exp read() throws LispinException {
        Exp retval = NIL;
        nextsym();
        if (cursym.equals(_symbolTable.EOF)) {
            retval = _symbolTable.EOF;
        } else {
            retval = parseExpression();
        }
        return (retval);
    }

    public Exp parseList() throws LispinException {
        Exp tree;

        nextsym();
        if( cursym.equals(_symbolTable.rightParen ) ) {
          tree = NIL;
        }
       else if( cursym.equals(_symbolTable.cdr_char) ) {
           return _symbolTable.cdr_char;
       }
       else {
          tree = parseExpression();
          Exp restOfList = parseList();
          if(restOfList == _symbolTable.cdr_char) {
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
        if (cursym.equals(_symbolTable.EOF)) {
            throw new ParseException("unexpected End of File");
        }
        if (cursym.equals(_symbolTable.rightParen)) {
            throw new ParseException("unexpected right paren");
        }
        if (cursym.equals(_symbolTable.leftParen)) {
            tree = parseList();
            if (!cursym.equals(_symbolTable.rightParen)) {
                throw new ParseException("missing right paren");
            }
        } else if (cursym == _symbolTable.raw_backquote) {
            nextsym();
            tree = new Lcons(_symbolTable.backquote, new Lcons(parseExpression(), NIL));
        } else if (cursym == _symbolTable.raw_quote) {
            nextsym();
            tree = new Lcons(_symbolTable.quote, new Lcons(parseExpression(), NIL));
        } else if (cursym == _symbolTable.raw_comma) {
            nextsym();
            tree = new Lcons(_symbolTable.comma, new Lcons(parseExpression(), NIL));
        } else if (cursym == _symbolTable.raw_comma_at) {
            nextsym();
            tree = new Lcons(_symbolTable.comma_at, new Lcons(parseExpression(), NIL));
        } else {
            tree = cursym;
        }
        return (tree);
    }
}
