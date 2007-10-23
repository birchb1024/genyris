package org.genyris.web;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.genyris.core.Exp;
import org.genyris.format.IndentedFormatter;
import org.genyris.interp.Interpreter;
import org.genyris.interp.GenyrisException;
import org.genyris.io.ConvertEofInStream;
import org.genyris.io.InStream;
import org.genyris.io.IndentStream;
import org.genyris.io.Parser;
import org.genyris.io.StringInStream;
import org.genyris.io.UngettableInStream;


public final class GenyrisServlet extends HttpServlet {


    private static final long serialVersionUID = 1L;

    /**
     * Respond to a GET request for the content produced by
     * this servlet.
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are producing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
      throws IOException, ServletException {

    response.setContentType("text/html");
    PrintWriter writer = response.getWriter();

    writer.println("<html>");
    writer.println("<head>");
    writer.println("<title>Genyris</title>");
    writer.println("</head>");
    writer.println("<body bgcolor=white><PRE>");

        Interpreter _interpreter;

        try {
            _interpreter = new Interpreter();
            _interpreter.init(true);

            InStream input = new UngettableInStream(new ConvertEofInStream(
                    new IndentStream(
                            new UngettableInStream(new StringInStream("list \"Hello\" \"world\"")), false)));
            Parser parser = _interpreter.newParser(input);
            Exp expression = parser.read();
            Exp result = _interpreter.evalInGlobalEnvironment(expression);

            IndentedFormatter formatter = new IndentedFormatter(writer, 3, _interpreter);
            result.acceptVisitor(formatter);
        } catch (GenyrisException e) {
            writer.print(e.getStackTrace());
        }


    writer.println("</PRE></body>");
    writer.println("</html>");

    }


}
