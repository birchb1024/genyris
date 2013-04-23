// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io;

import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;
import org.genyris.io.parser.StreamParser.AbstractParserMethod;
import org.genyris.io.readerstream.ReaderStream;

public class StringFormatStream implements InStreamEOF {
    private static final int IN_A_STRING = 0;
    private static final int STARTING    = 1;
    private static final int ENDING      = 2;
    private static final int ESCAPE      = 3;
    private static final int IN_LISP     = 4;
    InStream                 _instream;
    int                      _parseState;
    char                     _escaped;

    public StringFormatStream(InStream in) {
        _instream = in;
        _parseState = STARTING;
    }

    public int getChar() throws LexException {
        char ch = '@';
        switch (_parseState) {
            case IN_A_STRING :
                if (!_instream.hasData()) {
                    _parseState = ENDING;
                    return '"';
                }
                ch = _instream.readNext();
                if (ch == '\\') {
                    _parseState = ESCAPE;
                    _escaped = '\\';
                    return '\\';
                } else if (ch == '"') {
                    _parseState = ESCAPE;
                    _escaped = '"';
                    return '\\';
                } else if (ch == '#') {
                    if (!_instream.hasData()) {
                        _parseState = ENDING;
                        return ch;
                    }
                    char ch2 = _instream.readNext();
                    if (ch2 == '{') {
                        _parseState = IN_LISP;
                        return '"'; // end current string
                    } else {
                        _instream.unGet(ch2); // parse this char again
                        return '#';
                    }
                } else {
                    return ch;
                }
            case IN_LISP :
                if (!_instream.hasData()) {
                    _parseState = ENDING;
                    return ch;
                }
                ch = _instream.readNext();
                if (ch == '}') {
                    _parseState = IN_A_STRING;
                    return '"'; // start new string
                } else {
                    return ch;
                }
            case STARTING :
                _parseState = IN_A_STRING;
                ch = '"';
                return ch;
            case ENDING : // End
                return EOF;
            case ESCAPE : // escape
                _parseState = IN_A_STRING;
                return _escaped;
        }
        return EOF;
    }

    public void close() throws GenyrisException {
    }

    public static class NewMethod extends AbstractParserMethod {
        public NewMethod(Interpreter interp) {
            super(interp, "new");
        }

        public Exp bindAndExecute(Closure proc, Exp[] arguments, Environment env)
                throws GenyrisException {
            if (!(arguments[0] instanceof ReaderStream)) {
                throw new GenyrisException("Bad arg to new method of StringFormatStream");
            } else {
                ReaderStream input = (ReaderStream)arguments[0];
                return new ReaderStream(new StringFormatStream(input.getInStream()));
            }
        }
    }

    public static void bindFunctionsAndMethods(Interpreter interpreter) throws UnboundException, GenyrisException {
        interpreter.bindMethodInstance("StringFormatStream", new StringFormatStream.NewMethod(interpreter));
    }

	public void resetAfterError() {
	}

    public void withinExpression(Environment env) {
        this._instream.withinExpression(null);
    }

    public void beginningExpression() {
        this._instream.beginningExpression();
    }
    public int getLineNUmber() {
        return _instream.getLineNumber();
    }

}