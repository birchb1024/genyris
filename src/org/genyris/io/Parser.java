// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io;

import org.genyris.core.*;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

import java.net.URI;
import java.net.URISyntaxException;

public class Parser {
    protected Lex _lexer;

    private Exp cursym;

    protected Exp NIL;

    private Exp _prefix;

    Internable _table;

    private Exp pushback;

    public Parser(Internable table, InStream stream) {
        this(table, stream, Constants.DYNAMICSCOPECHAR2, Constants.CDRCHAR, Constants.COMMENTCHAR);
    }

    public Parser(Internable table, InStream stream, char dynaChar, char cdrCharacter,
            char commentChar) {
        _table = table;
        _lexer = new Lex(stream, table, dynaChar, cdrCharacter, commentChar);
        NIL = table.NIL();
        _prefix = table.PREFIX();
    }

    protected Exp cons(Exp l, Exp r, int line) {
        return new Pair(l,r);
    }

    public ParseException parseError(String msg) {
        return new ParseException(msg, _lexer.getFilename(), _lexer.getLineNumber());
    }

    public void nextsym() throws GenyrisException {
        if (pushback == null) {
            cursym = _lexer.nextToken();
        } else {
            cursym = pushback;
            pushback = null;
        }

    }

    public void pushbacksym(Exp token) throws ParseException {
        if (pushback == null) {
            pushback = token;
        } else {
            throw parseError("Attempt to pushback to many: " + token.toString());
        }

    }

    private Exp readAux(Environment env) throws GenyrisException {
        Exp retval = NIL;
        _lexer.beginningExpression();
        nextsym();
        _lexer.withinExpression(env);
        if (cursym.equals(_lexer.EOF_TOKEN)) {
            retval = _table.EOF();
        } else {
            retval = parseExpression();
        }
        _lexer.beginningExpression();
        return (retval);
    }

    public Exp read(Environment env) throws GenyrisException {
        Exp input = NIL;
        try {            
            input = readAux(env);
            while (namespaceDeclaration(input)) {
                input = readAux(env);
            }
        } catch (ParseException e) {
            _lexer.resetAfterError();
            throw e;
        }
        return input;
    }

    public Exp read() throws GenyrisException {
        return read(null);
    }

    public boolean isURI(StrinG X) {
        try {
            URI uri = new URI(X.toString());
            return uri.isAbsolute();
        }
        catch (URISyntaxException e) { }
        return false;
    }
    private boolean namespaceDeclaration(Exp input) throws GenyrisException {
        // process parser orders like @ns foo 'http://genyris.org/foo'
        if (!input.isPair()) {
            return false;
        }
        if (input.car() != _prefix) {
            return false;
        }
        Exp namespace = input.cdr().car();
        if(!(namespace instanceof Symbol)){
            throw parseError("namespace not a symbol: " + namespace);
        }
        if (input.cdr().cdr() == NIL) {
            throw parseError("namespace declaration missing prefix " + input);
        }
        Exp prefix = input.cdr().cdr().car();
        if(!(prefix instanceof StrinG)) {
            throw parseError("namespace prefix isn't a string: " + prefix);
        }
        if(!isURI((StrinG)prefix)){
                throw parseError("namespace prefix is not a URL: " + prefix);
        }
        _lexer.addprefix(((Symbol) namespace).getPrintName(), // TODO notify the interpreter a new prefix is in use with _interp.collectPrefix(prefix, expansion);
                        prefix.toString());                           //   - means changing Parser ctor signatures..
        return true;
    }

    private Exp collectPlings(Exp tree) throws GenyrisException {
        int startline = _lexer.getLineNumber();
        Exp old = cursym;
        nextsym();
        if (!(cursym instanceof Symbol)) {
            throw parseError("Bad indirection: " + cursym.toString());
        } else if (cursym == _lexer.DYNAMIC_TOKEN) {
            throw parseError("Bad indirection: " + cursym.toString());
        }
        tree = cons(tree, cons(
                new DynamicSymbol((SimpleSymbol) cursym), NIL, startline), startline); // TODO bad cast
        old = cursym;
        nextsym();
        if (cursym == _lexer.PLING_TOKEN) {
            return collectPlings(tree);
        } else {
            pushbacksym(cursym);
            cursym = old;
        }
        return tree;
    }

    private Exp collectSlashes(Exp tree) throws GenyrisException {
        int startline = _lexer.getLineNumber();
        Exp old = cursym;
        nextsym();
        if (!(cursym instanceof Symbol)) {
            throw parseError("Bad indirection: " + cursym.toString());
        } else if (cursym == _lexer.DYNAMIC_TOKEN) {
            throw parseError("Bad indirection: " + cursym.toString());
        }
        tree = cons(tree, cons(
                new DynamicSymbol((SimpleSymbol) cursym), NIL, startline), startline); // TODO bad cast
        old = cursym;
        nextsym();
        if (cursym == _lexer.SLASH_TOKEN) {
            return collectSlashes(tree);
        } else {
            pushbacksym(cursym);
            cursym = old;
        }
        return tree;
    }

    public Exp parseList(Exp lhs) throws GenyrisException {
        int startLine = _lexer.getLineNumber();
        Exp tree;
        nextsym();
        if (cursym.equals(_lexer.RIGHT_PAREN_TOKEN)) {
            tree = NIL;
        } else if (cursym.equals(_lexer.RIGHT_SQUARE_TOKEN)) {
            tree = NIL;
        } else if (cursym.equals(_lexer.RIGHT_CURLY_TOKEN)) {
            tree = NIL;
        } else if (cursym.equals(_lexer.CDR_TOKEN)) {
            return _lexer.CDR_TOKEN;
        } else {
            startLine = _lexer.getLineNumber();
            tree = parseExpression();
            Exp old = cursym;
           nextsym();
            if (cursym.equals(_lexer.PLING_TOKEN)) {
                Exp plings = collectPlings(tree);
                Exp restOfList = parseList(plings);
                if (restOfList == _lexer.CDR_TOKEN) {
                    nextsym();
                    restOfList = parseExpression();
                    nextsym();
                    tree = new PairEquals(plings, restOfList);
                    return tree;
                }
                tree = cons(plings, restOfList, startLine);
                return tree;
            }
            else if (cursym.equals(_lexer.SLASH_TOKEN)) {
                    Exp slashes = collectSlashes(tree);
                    Exp restOfList = parseList(slashes);
                    if (restOfList == _lexer.CDR_TOKEN) {
                        nextsym();
                        restOfList = parseExpression();
                        nextsym();
                        tree = new PairEquals(slashes, restOfList);
                        return tree;
                    }
                    tree = cons(slashes, restOfList, startLine);
                    return tree;
            } else {
                pushbacksym(cursym);
                cursym = old;
            }
            Exp restOfList = parseList(tree);
            if (restOfList == _lexer.CDR_TOKEN) {
                nextsym();
                restOfList = parseExpression();
                nextsym();
                tree = new PairEquals(tree, restOfList);
                return tree;
            }
            tree = cons(tree, restOfList, startLine);

        }
        return tree;
    }

    public Exp parseExpression() throws GenyrisException {
        int startline = _lexer.getLineNumber();
        Exp tree = NIL;
        if (cursym.equals(_lexer.PLING_TOKEN)) {
            throw parseError("unexpected !");
        }
        if (cursym.equals(_lexer.SLASH_TOKEN)) {
            throw parseError("unexpected /");
        }
        if (cursym.equals(_lexer.CDR_TOKEN)) {
            throw parseError("unexpected =");
        }
        if (cursym.equals(_lexer.EOF_TOKEN)) {
            throw parseError("unexpected End of File");
        }
        if (cursym.equals(_lexer.RIGHT_PAREN_TOKEN)) {
            throw parseError("unexpected right paren");
        }
        if (cursym.equals(_lexer.LEFT_PAREN_TOKEN)) {
            tree = parseList(NIL);
            if (!cursym.equals(_lexer.RIGHT_PAREN_TOKEN)) {
                throw parseError("missing right paren - found: "
                        + cursym);
            }
        } else if (cursym.equals(_lexer.LEFT_SQUARE_TOKEN)) {
            tree = parseList(NIL);
            tree = cons(_table.SQUARE(), tree, startline);
            if (!cursym.equals(_lexer.RIGHT_SQUARE_TOKEN)) {
                throw parseError("missing right square brace - found: "
                        + cursym);
            }
        } else if (cursym.equals(_lexer.LEFT_CURLY_TOKEN)) { // TOD DRY - SQUARE
            tree = parseList(NIL);
            tree = cons(_table.CURLY(), tree, startline);
            if (!cursym.equals(_lexer.RIGHT_CURLY_TOKEN)) {
                throw parseError("missing right curly brace - found: "
                        + cursym);
            }
        } else if (cursym == _lexer.BACKQUOTE_TOKEN) {
            nextsym();
            tree = cons(_table.TEMPLATE(), cons(parseExpression(), NIL, startline), startline);
        } else if (cursym == _lexer.QUOTE_TOKEN) {
            nextsym();
            tree = cons(_table.QUOTE(), cons(parseExpression(), NIL, startline), startline);
        } else if (cursym == _lexer.DOLLAR_TOKEN) {
            nextsym();
            tree = cons(_table.DOLLAR(), cons(parseExpression(), NIL, startline), startline);
        } else if (cursym == _lexer.DOLLAR_AT_TOKEN) {
            nextsym();
            tree = cons(_table.DOLLAR_AT(), cons(parseExpression(), NIL, startline), startline);
        } else if (cursym == _lexer.DYNAMIC_TOKEN) {
            nextsym();
            if (cursym instanceof SimpleSymbol) {
                tree = new DynamicSymbol((SimpleSymbol) cursym);
            } else {
                throw parseError("period found before non-symbol: "
                        + cursym);
            }
        } else {
            tree = cursym;
        }
        return tree;
    }

    public void addPrefix(Interpreter interp, String prefix, String expansion)
            throws GenyrisException {
        interp.collectPrefix(prefix, expansion);
        _lexer.addprefix(prefix, expansion);
    }

    public static Exp parseSingleExpressionFromString(Internable table,
            String script) throws GenyrisException {
        InStream input = new UngettableInStream(new StringInStream(script));
        Parser parser = new Parser(table, input);
        Exp expression = parser.read();
        return expression;
    }

    public void resetAfterError() throws LexException {
        _lexer.resetAfterError();
    }

    public void setUsualPrefixes(Interpreter interp) throws GenyrisException {
        addPrefix(interp, "u", Constants.PREFIX_UTIL);
        addPrefix(interp, "web", Constants.PREFIX_WEB);
        addPrefix(interp, "syn", Constants.PREFIX_SYNTAX);
        addPrefix(interp, "sys", Constants.PREFIX_SYSTEM);
        addPrefix(interp, "task", Constants.PREFIX_TASK);
        addPrefix(interp, "types", Constants.PREFIX_TYPES);
        addPrefix(interp, "date", Constants.PREFIX_DATE);
        addPrefix(interp, "java", Constants.PREFIX_JAVA);
    }

}
