// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.bsf;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Vector;
import org.apache.bsf.BSFDeclaredBean;
import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;
import org.apache.bsf.util.BSFEngineImpl;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.Interpreter;
import org.genyris.interp.Runner;
import org.genyris.io.StringInStream;

public class GenyrisEngine extends BSFEngineImpl {
    Interpreter _interp;

    /**
     * Initialize the engine.
     */
    public void initialize(BSFManager mgr, String lang, Vector declaredBeans) throws BSFException {
        super.initialize(mgr, lang, declaredBeans);
        try {
            _interp = new Interpreter();
            _interp.init(true);
        } catch (GenyrisException e) {
            throw new BSFException(e.getMessage());
        };
        // // create an interpreter
        // interp = new BSFPythonInterpreter ();
        //
        // // ensure that output and error streams are re-directed correctly
        // interp.setOut(System.out);
        // interp.setErr(System.err);
        //
        // // register the mgr with object name "bsf"
        // interp.set ("bsf", new BSFFunctions (mgr, this));
        //
        // // Declare all declared beans to the interpreter
        // int size = declaredBeans.size ();
        // for (int i = 0; i < size; i++) {
        // declareBean ((BSFDeclaredBean) declaredBeans.elementAt (i));
        // }
    }

    /**
     * Evaluate an expression.
     */
    public Object eval(String source, int lineNo, int columnNo, Object script) throws BSFException {
        Writer output = new StringWriter();
        try {
            Runner.executeScript(_interp, new StringInStream(script.toString()), output);
        } catch (GenyrisException e) {
            throw new BSFException(e.getMessage());
        }
        return output.toString();
    }

    /**
     * call the named method of the given object.
     */
    public Object call(Object object, String method, Object[] args) {
        // throws BSFException {
        // try {
        // PyObject[] pyargs = Py.EmptyObjects;
        //
        // if (args != null) {
        // pyargs = new PyObject[args.length];
        // for (int i = 0; i < pyargs.length; i++)
        // pyargs[i] = Py.java2py(args[i]);
        // }
        //
        // if (object != null) {
        // PyObject o = Py.java2py(object);
        // return unwrap(o.invoke(method, pyargs));
        // }
        //
        // PyObject m = interp.get(method);
        //
        // if (m == null)
        // m = interp.eval(method);
        // if (m != null) {
        // return unwrap(m.__call__(pyargs));
        // }
        //
        // return null;
        // } catch (PyException e) {
        // throw new BSFException (BSFException.REASON_EXECUTION_ERROR,
        // "exception from Genyris:\n" + e, e);
        // }
        return null;
    }

    /**
     * Declare a bean
     */
    public void declareBean(BSFDeclaredBean bean) throws BSFException {
        // interp.set (bean.name, bean.bean);
    }

    /**
     * Evaluate an anonymous function (differs from eval() in that apply() handles multiple lines).
     */
    public Object apply(String source, int lineNo, int columnNo, Object funcBody,
            Vector paramNames, Vector arguments) throws BSFException {
        // try {
        // /* We wrapper the original script in a function definition, and
        // * evaluate the function. A hack, no question, but it allows
        // * apply() to pretend to work on Genyris.
        // */
        // StringBuffer script = new StringBuffer(byteify(funcBody.toString()));
        // int index = 0;
        // script.insert(0, "def bsf_temp_fn():\n");
        //
        // while (index < script.length()) {
        // if (script.charAt(index) == '\n') {
        // script.insert(index+1, '\t');
        // }
        // index++;
        // }
        //
        // interp.exec (script.toString ());
        //
        // Object result = interp.eval ("bsf_temp_fn()");
        //
        // if (result != null && result instanceof PyJavaInstance)
        // result = ((PyJavaInstance)result).__tojava__(Object.class);
        // return result;
        // } catch (PyException e) {
        // throw new BSFException (BSFException.REASON_EXECUTION_ERROR,
        // "exception from Genyris:\n" + e, e);
        // }
        return null;
    }

    /**
     * Execute a script.
     */
    public void exec(String source, int lineNo, int columnNo, Object script) throws BSFException {
        // try {
        // interp.exec (byteify(script.toString ()));
        // } catch (PyException e) {
        // throw new BSFException (BSFException.REASON_EXECUTION_ERROR,
        // "exception from Genyris:\n" + e, e);
        // }
    }

    /**
     * Execute script code, emulating console interaction.
     */
    public void iexec(String source, int lineNo, int columnNo, Object script) throws BSFException {
        // String scriptStr = byteify(script.toString());
        // int newline = scriptStr.indexOf("\n");
        //
        // if (newline > -1)
        // scriptStr = scriptStr.substring(0, newline);
        //
        // try {
        // if (interp.buffer.length() > 0)
        // interp.buffer.append("\n");
        // interp.buffer.append(scriptStr);
        // if (!(interp.runsource(interp.buffer.toString())))
        // interp.resetbuffer();
        // } catch (PyException e) {
        // interp.resetbuffer();
        // throw new BSFException(BSFException.REASON_EXECUTION_ERROR,
        // "exception from Genyris:\n" + e, e);
        // }
    }

    /**
     * Undeclare a previously declared bean.
     */
    public void undeclareBean(BSFDeclaredBean bean) throws BSFException {
        // interp.set (bean.name, null);
    }
    // public Object unwrap(PyObject result) {
    // if (result != null) {
    // Object ret = result.__tojava__(Object.class);
    // if (ret != Py.NoConversion)
    // return ret;
    // }
    // return result;
    // }
    // private class BSFPythonInterpreter extends InteractiveInterpreter {
    //
    // public BSFPythonInterpreter() {
    // super();
    // }
    //
    // // Override runcode so as not to print the stack dump
    // public void runcode(PyObject code) {
    // try {
    // this.exec(code);
    // } catch (PyException exc) {
    // throw exc;
    // }
    // }
    // }
}
