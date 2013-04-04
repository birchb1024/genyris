package org.genyris.io;

import java.io.IOException;
import java.io.OutputStream;

import jline.console.ConsoleReader;
import jline.console.completer.StringsCompleter;

import org.apache.commons.io.output.WriterOutputStream;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Interpreter;

public class JlineStdioInStream implements InStream {

    //
    // WARNING - this class has only one instance shared between all threads.
    //

    private int _nextIndex;
    private ConsoleReader _jline;
    private String _nextLine;
    private StringsCompleter _completer;
    private static JlineStdioInStream singleton = null;
    private static Interpreter _interp;

    public static synchronized JlineStdioInStream knew() { // the 'k' is silent.
        if (singleton == null) {
            singleton = new JlineStdioInStream();
        }
        return singleton;
    }

    private JlineStdioInStream() {
        _nextLine = null;
        try {
            _jline = new ConsoleReader();
            parsingDone();
            _jline.setExpandEvents(false);
            _completer = null;

            _jline.addCompleter(_completer);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public OutputStream getOutput() {
        return new WriterOutputStream(_jline.getOutput()) ;        
    }
    public synchronized void unGet(char x) throws LexException {
        throw new LexException("StdioStream: unGet not implemented.");
    }

    public synchronized char readNext() throws LexException {
        if (_nextLine == null) {
            throw new LexException(
                    "StdioInStream: readNext() called before hasData()");
        }
        int retval =_nextLine.charAt(_nextIndex);
        _nextIndex++;
        if( _nextLine.length() == _nextIndex) {
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
            _nextIndex = 0;
        } catch (IOException e) {
            _nextLine = null;
            return false;
        }
        if (_nextLine == null) {
            return false;
        } else {
            if( _nextLine.length() == 0 ) {
                // blank line
                resetTabCompletion();
            }
            _nextLine = _nextLine + '\n';
            return true;
        }
    }

    private void resetTabCompletion() {
        _jline.removeCompleter(_completer);
        _completer = new StringsCompleter(_interp.getSymbolsAsListOfStrings());
        _jline.addCompleter(_completer);
    }

    public synchronized void close() throws GenyrisException {
    }

    public synchronized void resetAfterError() {
        _nextLine = null;
        parsingDone();
    }

    public void setInterpreter(Interpreter _interpreter) {
        _interp = _interpreter;      
        resetTabCompletion();
    }

    public void parsingStarted() {
        _jline.setPrompt(": ");
    }

    public void parsingDone() {
        _jline.setPrompt("> ");
    }

}
