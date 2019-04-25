package org.genyris.io;

import java.io.IOException;
import java.io.OutputStream;

import jline.console.ConsoleReader;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.ArgumentCompleter.AbstractArgumentDelimiter;
import jline.console.completer.StringsCompleter;

import org.apache.commons.io.output.WriterOutputStream;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;

public class JlineStdioInStream implements InStream {

    //
    // WARNING - this class has only one instance shared between all threads.
    //

    private int _nextIndex;
    private ConsoleReader _jline;
    private String _nextLine;
    private ArgumentCompleter _completer;
    private Environment _environment;
    private static JlineStdioInStream singleton = null;
    private static Interpreter _interp;
    private int _lineCount;

    public static synchronized JlineStdioInStream knew() { // the 'k' is silent.
        if (singleton == null) {
            singleton = new JlineStdioInStream();
        }
        return singleton;
    }

    private JlineStdioInStream() {
        _nextLine = null;
        _lineCount = 0;
        try {
            _jline = new ConsoleReader();
            _jline.setPrompt("> ");
            _jline.setExpandEvents(false);
            _completer = null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public OutputStream getOutput() {
        return new WriterOutputStream(_jline.getOutput());
    }

    public synchronized void unGet(char x) throws LexException {
        throw new LexException("StdioStream: unGet not implemented.");
    }

    public synchronized char readNext() throws LexException {
        if (_nextLine == null) {
            throw new LexException(
                    "StdioInStream: readNext() called before hasData()");
        }
        int retval = _nextLine.charAt(_nextIndex);
        _nextIndex++;
        if (_nextLine.length() == _nextIndex) {
            // read the last one
            _nextLine = null;
            _nextIndex = 0;
        }
        return (char) retval;
    }

    public synchronized boolean hasData() {
        try {
            if (_nextLine != null) {
                return true;
            }
            _nextLine = _jline.readLine();
            _lineCount++;
            _nextIndex = 0;
        } catch (IOException e) {
            _nextLine = null;
            return false;
        }
        if (_nextLine == null) {
            return false;
        } else {
            _nextLine = _nextLine + '\n';
            return true;
        }
    }

    private void refreshTabCompletion() {
        if (_environment == null) {
            _environment = _interp.getGlobalEnv();
        }
        _jline.removeCompleter(_completer);
        _completer = new ArgumentCompleter(new GenyrisArgumentDelimiter(),
                new StringsCompleter(
                        _interp.getBoundSymbolsAsListOfStrings(_environment)));
        _completer.setStrict(false);
        _jline.addCompleter(_completer);
    }

    public synchronized void close() throws GenyrisException {
    }

    public synchronized void resetAfterError() {
        _nextLine = null;
        beginningExpression();
    }

    public void setInterpreter(Interpreter _interpreter) {
        _interp = _interpreter;
        _environment = _interp.getGlobalEnv();
        refreshTabCompletion();
    }

    public void setEnvironment(Environment env) {
        _environment = env;
        refreshTabCompletion();
    }

    public void withinExpression(Environment env) {
        _environment = env;
        refreshTabCompletion();
        _jline.setPrompt(": ");
    }

    public void beginningExpression() {
        refreshTabCompletion();
        _jline.setPrompt("> ");
    }

    public static class GenyrisArgumentDelimiter extends
            AbstractArgumentDelimiter {

        public boolean isDelimiterChar(final CharSequence buffer, final int pos) {
            char ch = buffer.charAt(pos);
            return Character.isWhitespace(ch) || ch == '(' || ch == ')'
                    || ch == '[' || ch == ']' || ch == ','
                    || ch == '\'' || ch == '"' || ch == '#'
                    || ch == '^' || ch == '{' || ch == '}';
        }
    }

    public int getLineNumber() {
        return _lineCount;
    }
    public String getFilename() {
        return "console";
    }

}
