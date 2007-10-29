// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.io;

public class IndentStream implements InStreamEOF {

    private static final int LEADING_WHITE_SPACE = 0;

    private static final int IN_STATEMENT = 1;

    private static final int CATCHUP = 2;

    private static final int FINISHING = 3;

    private static final int IN_STRING = 4;

    private static final int IN_STRING_ESC = 5;

    private static final int STRIP_COMMENT = 6;

    private static final int NEXTLINE = 7;

    InStream _instream;

    int _parseState;

    int _tabs[];

    int _numberOfLeadingSpaces;

    int _tabsToGo;

    int _lineLevel;

    int _currentLevel; // current indent level (of previous line) -1 means

    // prior to first line
    char ch;

    private int[] _bufferit; // TODO maybe use a stringbuffer or some other

    // type
    int _bufferitReadPtr; // where are we up to.

    int _bufferitWritePtr; // where are we up to.

    boolean _interactive;

    private int _maxTab;

    public IndentStream(InStream in, boolean interactiveMode) {
        _instream = in;
        _tabs = new int[256]; // TODO;
        _bufferit = new int[2048]; // TODO
        _lineLevel = 0;
        _parseState = LEADING_WHITE_SPACE;
        _maxTab = 1;
        _interactive = interactiveMode;
    }

    public void unGet(char x) throws LexException {
        throw new LexException("unGet() not implemented in IndentStream!");
    }

    void startLine() {
        _lineLevel = 0;
        _parseState = LEADING_WHITE_SPACE;
        _numberOfLeadingSpaces = 0;
    }

    void input() throws LexException {
        ch = _instream.readNext();
    }

    void bufferit(int c) throws LexException {
        bufferit(c, 1);
    }

    void bufferit(int ch, int num) throws LexException {
        while (num > 0) {
            if (_bufferitWritePtr > _bufferit.length) {
                throw new LexException("lexer buffer overrun");
            }
            _bufferit[_bufferitWritePtr++] = ch;
            num--;
        }
    }

    boolean bufferitEmpty() {
        return _bufferitReadPtr >= _bufferitWritePtr;
    }

    int bufferitReadNext() throws LexException {
        if (bufferitEmpty())
            throw new LexException("Tried to read past end of buffer.");
        int result = _bufferit[_bufferitReadPtr++];
        if (bufferitEmpty()) {
            _bufferitReadPtr = _bufferitWritePtr = 0;
        }
        return result;
    }

    public int getChar() throws LexException {

        while (true) {
            switch (_parseState) {

            case LEADING_WHITE_SPACE:
                if (!_instream.hasData()) {
                    finish();
                    break;
                }
                input();
                if (ch == ' ') {
                    _numberOfLeadingSpaces++;
                    break;
                }
                else if (ch == ';') {
                    _parseState = STRIP_COMMENT;
                    break;
                }
                else if (ch == '\r') {
                    break; // probably MS-DOS line end - ignore
                }
                else if (ch == '\n') {
                    if (_interactive) {
                        // finish the indentations all up, don't wait for more lines
                        _lineLevel = 0;
                        _numberOfLeadingSpaces = 0;
                        bufferit(')', _currentLevel);
                        _currentLevel = 0;
                        _parseState = NEXTLINE;
                        removeTabsAfter(1);
                        break;
                    }
                    else {
                        startLine();
                    }
                    break;
                }
                else if (ch == '\t') {
                    throw new LexException("illegal tab character before statement");
                }
                else if (ch == '~') {
                    // Continuation line so pretend indentation is aligned
                    // with the previous level
                    // Example:
                    // foo
                    // bar 1 2
                    // ~ '(1 2 3 4 5)
                    // Gives: (foo (bar 1 2) '(1 2 3 4 5))
                    // xyz
                    // foo
                    // bar 1 2
                    // ~ quux
                    // Gives: (xyz (foo (bar 1 2)) quux)

                    stripLeadingSpaces();
                    // readAndBufferRestOfLine();

                    _lineLevel = computeDepthFromSpaces(_numberOfLeadingSpaces);

                    bufferit(')', _currentLevel - _lineLevel + 1);
                    bufferit(' ');
                    _currentLevel = _lineLevel - 1;
                    removeTabsAfter(_currentLevel);

                    _parseState = CATCHUP;
                    break;
                }
                else {
                    if(_numberOfLeadingSpaces == 0) {
                        removeTabsAfter(1);
                    }
                    _lineLevel = computeDepthFromSpaces(_numberOfLeadingSpaces);

                    if (_currentLevel == _lineLevel) {
                        // Same indentation as previous line.
                        bufferit(')');
                        bufferit('(');
                    }
                    else if (_currentLevel < _lineLevel) {
                        bufferit('(', _lineLevel - _currentLevel);
                    }
                    else if (_currentLevel > _lineLevel) {
                        bufferit(')', _currentLevel - _lineLevel + 1);
                        bufferit('(');
                        removeTabsAfter(_lineLevel);
                    }
                    if (ch == '"') {
                        _instream.unGet('"');
                    }
                    else {
                        bufferit(ch);
                    }
                    _currentLevel = _lineLevel;
                    _parseState = CATCHUP;
                    break;
                }

            case IN_STRING_ESC:
                if (!_instream.hasData()) {
                    finish();
                    break;
                }
                input();
                _parseState = IN_STRING;
                return (ch);

            case IN_STRING:
                if (!_instream.hasData()) {
                    finish();
                    break;
                }
                input();

                switch (ch) {

                case '\\':
                    _parseState = IN_STRING_ESC;
                    return (ch);

                case '"':
                    _parseState = IN_STATEMENT;
                    return (ch);

                default:
                    return (ch);
                }

            case IN_STATEMENT:
                if (!_instream.hasData()) {
                    finish();
                    break;
                }
                input();
                switch (ch) {

                case '"':
                    _parseState = IN_STRING;
                    return (ch);

                case ';':
                    _parseState = STRIP_COMMENT;
                    break;

                case '\n':
                    startLine();
                    break;

                default:
                    return (ch);
                }
                break;

            case STRIP_COMMENT:
                if (_instream.hasData()) {
                    input();
                    if (ch == '\n') {
                        startLine();
                        break;
                    }
                }
                else
                    finish();
                break;

            case CATCHUP:
                if (bufferitEmpty()) {
                    _parseState = IN_STATEMENT;
                }
                else {
                    int result = bufferitReadNext();
                    return result;
                }
                break;

            case NEXTLINE:
                if (bufferitEmpty()) {
                    startLine();
                }
                else {
                    int result = bufferitReadNext();
                    return result;
                }
                break;

            case FINISHING:
                if (bufferitEmpty()) {
                    return EOF;
                }
                else {
                    int result = bufferitReadNext();
                    return result;
                }
            }
        }
    }

    private void stripLeadingSpaces() throws LexException {
        while (_instream.hasData()) {
            input();
            if (ch != ' ') {
                _instream.unGet(ch);
                break;
            }
        }
    }

    private void finish() throws LexException {
        // close all parenthesis

        bufferit(')', _currentLevel);
        _parseState = FINISHING;

    }

    public boolean hasData() throws LexException {
        throw new LexException("hasData() not implemented");
    }

    void removeTabsAfter(int newMax) {
        _maxTab = newMax; // TODO unnecessary - done below alos ?
    }

    public int computeDepthFromSpaces(int numsp) throws LexException {

        if (numsp == 0) {
            return (1);
        }
        if (numsp > _tabs[_maxTab - 1]) {
            if (_maxTab > _tabs.length) {
                throw new LexException("input stream indented too deeply");
            }
            _tabs[_maxTab] = numsp; // remember the tabstop
            _maxTab++;
            return (_maxTab);
        }
        else {
            // look for the first tabstop on the left, starting at the left
            // margin
            for (int i = 0; i < _maxTab; i++) { // sequential search
                // is esential here. (left to right)

                if (_tabs[i] == numsp) {
                    _maxTab = i + 1;
                    return (_maxTab);
                }
            }
            // nothing matching, so it's an error
            throw new LexException("invalid indentation not matching previous indentation");
        }
    }
}
