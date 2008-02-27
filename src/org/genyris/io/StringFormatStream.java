// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io;

import org.genyris.exception.GenyrisException;

public class StringFormatStream implements InStreamEOF {

    private static final int IN_A_STRING = 0;

    private static final int STARTING = 1;

    private static final int ENDING = 2;

    private static final int ESCAPE = 3;

    private static final int IN_LISP = 4;

    InStream _instream;

    int _parseState;

    char _escaped;

    public StringFormatStream(InStream in) {
        _instream = in;
        _parseState = STARTING;
    }

    public int getChar() throws LexException {

        char ch = '@';

        switch (_parseState) {

        case IN_A_STRING:
            if (!_instream.hasData()) {
                _parseState = ENDING;
                return '"';
            }
            ch = _instream.readNext();
            if (ch == '\\') {
                _parseState = ESCAPE;
                _escaped = '\\';
                return '\\';
            }
            else if (ch == '"') {
                _parseState = ESCAPE;
                _escaped = '"';
                return '\\';
            }
            else if (ch == '#') {
                if (!_instream.hasData()) {
                    _parseState = ENDING;
                    return ch;
                }
                char ch2 = _instream.readNext();
                if (ch2 == '{') {
                    _parseState = IN_LISP;
                    return '"'; // end current string
                }
                else {
                    _instream.unGet(ch2); // parse this char again
                    return '#';
                }
            }
            else {
                return ch;
            }

        case IN_LISP:
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

        case STARTING:
            _parseState = IN_A_STRING;
            ch = '"';
            return ch;

        case ENDING: // End
            return EOF;

        case ESCAPE: // escape
            _parseState = IN_A_STRING;
            return _escaped;

        }
        return EOF;
    }

    public void close() throws GenyrisException {}
}