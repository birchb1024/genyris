// Copyright 2008 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.genyris.core.Exp;
import org.genyris.exception.GenyrisException;
import org.genyris.format.Formatter;
import org.genyris.format.IndentedFormatter;
import org.genyris.interp.Interpreter;
import org.genyris.io.ConvertEofInStream;
import org.genyris.io.InStream;
import org.genyris.io.IndentStream;
import org.genyris.io.Parser;
import org.genyris.io.StringInStream;
import org.genyris.io.UngettableInStream;

public final class GenyrisServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
    * Respond to a GET request for the content produced by this servlet.
    *
    * @param request
    *            The servlet request we are processing
    * @param response
    *            The servlet response we are producing
    *
    * @exception IOException
    *                if an input/output error occurs
    * @exception ServletException
    *                if a servlet error occurs
    */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();

        writer.println("<html>");
        writer.println("<head>");
        writer.println("<title>Genyris</title>");
        writer.println("</head>");
        writer.println("<body bgcolor=white><PRE>");

        Interpreter _interpreter = null;
        Object interpattr = request.getSession().getAttribute("interpreter");
        Enumeration headerIter = request.getHeaderNames();
        while (headerIter.hasMoreElements() ) {
            headerIter.nextElement();
        }
        Enumeration paramIter = request.getAttributeNames();
        while (paramIter.hasMoreElements() ) {
            paramIter.nextElement();
        }
        if (interpattr == null) {
            try {
                _interpreter = new Interpreter();
                _interpreter.init(false);
                request.getSession().setAttribute("interpreter", _interpreter);
                writer.println("new interpreter:" + _interpreter.toString());
            } catch (GenyrisException e) {
                writer.println(e.getMessage());
                writer.println("Could not construct interpreter!</PRE></body>");
                writer.println("</html>");
                return;
            }
        } else {
        	if(interpattr instanceof Interpreter) {
        		_interpreter = (Interpreter) interpattr;      		
        	} else {
        		throw new ServletException("bad session object was not an interpreter.");
        	}
        }

        try {
            InStream input = new UngettableInStream(new ConvertEofInStream(
                    new IndentStream(new UngettableInStream(new StringInStream(
                            request.getParameter("expression"))), false)));
            Parser parser = _interpreter.newParser(input);
            Exp expression = parser.read();
            Exp result = _interpreter.evalInGlobalEnvironment(expression);

            Formatter formatter = new IndentedFormatter(writer, 3);
            result.acceptVisitor(formatter);
        } catch (GenyrisException e) {
            writer.println(e.getMessage());
            writer.println(e.getStackTrace().toString());
        }

        writer.println("</PRE></body>");
        writer.println("</html>");

    }

}
