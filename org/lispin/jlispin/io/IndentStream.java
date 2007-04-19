package org.lispin.jlispin.io;


public class IndentStream implements InStreamEOF {

	private static final int LEADING_WHITE_SPACE = 0;
	private static final int STRIP_COMMENT = 1;
	private static final int IN_STATEMENT = 2;
	private static final int CATCHUP = 3;
	private static final int IN_STRING = 4;
	private static final int IN_STRING_ESC = 5;

	InStream _instream;

	int _parseState;

	int _tabs[];

	int _numberOfLeadingSpaces;

	int _tabsToGo;

	int _lineLevel;

	int _currentLevel; // current indent level (of previous line) -1 means

	// prior to first line
	int ch;

	private int[] _bufferit; // TODO maybe use a stringbuffer or some other

	// type
	int _bufferitReadPtr; // where are we up to.

	int _bufferitWritePtr; // where are we up to.

	private int _maxTab;

	public IndentStream(InStream in) {
		_instream = in;
		_tabs = new int[256]; // TODO;
		_bufferit = new int[2048]; // TODO
		_lineLevel = 0;
		_parseState = LEADING_WHITE_SPACE;
		_maxTab = 0;
	}

	public void unGet(char x) throws LexException {
		;
	}

	void startLine() {
		_lineLevel = 0;
		_parseState = LEADING_WHITE_SPACE;
		_numberOfLeadingSpaces = 0;
	}

	void input() throws LexException {
		if (!_instream.hasData()) {
			throw new LexException("oops");
		}
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
				if( !_instream.hasData()) {
					return (char)EOF;
				}
				ch = _instream.readNext();
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
					// blank line
					startLine();
					return ch;
				}
				else if (ch == '\t') {
					throw new LexException(
							"illegal tab character before statement");
				}
				else if (ch == '~') {
					// continuation line so pretend indentation is aligned
					// with the previous level
					// TODO
					break; // jump back to the top of the loop
				}
				if (_numberOfLeadingSpaces == 0) {
					// text started at beginning of the line
					_lineLevel = 0;
					_parseState = IN_STATEMENT;
					bufferit('(');
					bufferit(ch);
					_parseState = CATCHUP;
					break;
				}
				else {
					_lineLevel = computeDepthFromSpaces(_numberOfLeadingSpaces);

					if (_currentLevel < 0) {
						// This is the first line
						bufferit('(', _lineLevel + 1);
						_currentLevel = 0;
					}
					else if (_currentLevel == _lineLevel) {
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
						removeTabsAfter(_currentLevel);
					}
					bufferit(ch);

					_currentLevel = _lineLevel;
					_parseState = CATCHUP;
					break; 
				}

			case CATCHUP:
				if (bufferitEmpty()) {
					_parseState = IN_STATEMENT;
				}
				else {
					int result = bufferitReadNext();
					return result;
				}
				break;

			case IN_STRING_ESC:
				input();
				_parseState = IN_STRING;
				return (ch);

			case IN_STRING:
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
				if( !_instream.hasData() ) {
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
					return (ch);

				default:
					return (ch);
				}
				break;

			case STRIP_COMMENT:
				if (_instream.hasData()) {
					ch = _instream.readNext();
					if (ch == '\n') {
						_lineLevel = 0;
						_parseState = LEADING_WHITE_SPACE;
						return (ch);
					}
				}
				break;

			case FINISHING:
				if (bufferitEmpty()) {
					_parseState = IN_STATEMENT;
				}
				else {
					int result = bufferitReadNext();
					return result;
				}
				break;

			}
		}
	}

	private void finish() throws LexException {
		// close all parenthesis

		bufferit(')', _currentLevel + 1);
		_parseState = FINISHING;

	}

	public boolean hasData() throws LexException {
		throw new LexException("hasData() not implemented");
	}

	void removeTabsAfter(int newMax) {
		_maxTab = newMax;
	}
	int computeDepthFromSpaces(int numsp) throws LexException {

		if (numsp == 0) {
			return (0);
		}
		if (numsp > _tabs[_maxTab]) {
			_maxTab++;
			if (_maxTab > _tabs.length) {
				throw new LexException("input stream indented too deeply");
			}
			_tabs[_maxTab] = numsp; // remember the tabstop
			return (_maxTab);
		}
		else {
			// look for the first tabstop on the left, starting at the left
			// margin
			for (int i = 0; i <= _maxTab; i++) { // sequential
				// search
				// is esential here.
				// (left to right)

				if (_tabs[i] == numsp) {
					_maxTab = i;
					return (i);
				}
			}
			// nothing matching, so it's an error
			throw new LexException(
					"invalid indentation not matching previous indentation");
		}
	}
}