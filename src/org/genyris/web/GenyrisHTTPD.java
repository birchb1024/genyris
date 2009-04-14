package org.genyris.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.util.Enumeration;
import java.util.Properties;

import org.genyris.core.Dictionary;
import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.core.StrinG;
import org.genyris.exception.GenyrisException;
import org.genyris.format.BasicFormatter;
import org.genyris.format.Formatter;
import org.genyris.format.HTMLFormatter;
import org.genyris.interp.Interpreter;
import org.genyris.load.SourceLoader;

public class GenyrisHTTPD extends NanoHTTPD {

	Interpreter interpreter;

	Exp NIL;

	Dictionary HttpRequestClazz, AlistClazz;

	public GenyrisHTTPD(int port, String filename) throws GenyrisException {
		myTcpPort = port;

		interpreter = new Interpreter();
		interpreter.init(false);
		NIL = interpreter.NIL;
		Writer output = new PrintWriter(System.out);
		SourceLoader.loadScriptFromClasspath(interpreter,
				"org/genyris/load/boot/httpd-serve.lin", output);
		SourceLoader.loadScriptFromFile(interpreter, filename, output);

		HttpRequestClazz = (Dictionary) interpreter
				.lookupGlobalFromString("HttpRequest");
		AlistClazz = (Dictionary) interpreter.lookupGlobalFromString("Alist");

		try {
			ss = new ServerSocket(myTcpPort);
			ss.setSoTimeout(SERVER_SOCKET_TIMEOUT);
		} catch (IOException e1) {
			throw new GenyrisException("GenyrisHTTPD: Port " + myTcpPort + " "
					+ e1.getMessage());
		}
	}

	public Thread run() throws IOException {
		Thread t = new Thread(new Runnable() {
			public void run() {
				boolean terminating = false;
				while (!terminating) {
					try {
						 new HTTPSession(ss.accept());
					} catch (InterruptedIOException e) {
						if (Thread.currentThread().isInterrupted()) {
							terminating = true;
						}
						continue;
					} catch (IOException ioe) {
						System.out.println("GenyrisHTTPD: IOException " + ioe.getMessage());
					}
				}
				try {
					if (ss != null)
						ss.close();
				} catch (IOException e) {
				}

			}
		});
		t.setName("GenyrisHTTPD-" + myTcpPort);
		t.setDaemon(true);
		t.start();
		return t;
	}

	public synchronized NanoResponse  serve(String uri, String method, Properties header,
			Properties parms, String rootdir) {
		Exp request = NIL;
		// System.out.println(method + " '" + uri + "' ");

		Exp headers = NIL;
		Enumeration e = header.propertyNames();
		while (e.hasMoreElements()) {
			String value = (String) e.nextElement();
			headers = new Pair(new Pair(new StrinG(value), new StrinG(header
					.getProperty(value))), headers);
			// System.out.println(" HDR: '" + value + "' = '" +
			// header.getProperty(value) + "'");
		}
		headers.addClass(AlistClazz);

		Exp parameters = NIL;
		e = parms.propertyNames();
		while (e.hasMoreElements()) {
			String value = (String) e.nextElement();
			parameters = new Pair(new Pair(new StrinG(value), new StrinG(parms
					.getProperty(value))), parameters);
			// System.out.println(" PRM: '" + value + "' = '" +
			// parms.getProperty(value) + "'");
		}
		parameters.addClass(this.AlistClazz);

		request = new Pair(parameters, request);
		request = new Pair(headers, request);
		request = new Pair(new StrinG(uri), request);
		request = new Pair(new StrinG(method), request);
		request.addClass(HttpRequestClazz);

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		Writer output = new PrintWriter(buffer);
		// (httpd-serve request)
		Exp expression = new Pair(interpreter.intern("httpd-serve"), new Pair(
				request, NIL));

		try {
			Formatter formatter;
			// formatter = new IndentedFormatter(output, 1, interpreter);
			// expression.acceptVisitor(formatter);
			Exp result = interpreter.evalInGlobalEnvironment(expression);
			String status = result.car().toString();
			result = result.cdr();
			String mime = result.car().toString();
			if (mime.equals("text/html")) {
				formatter = new HTMLFormatter(output);
			} else {
				formatter = new BasicFormatter(output);
			}
			result = result.cdr();
			result.acceptVisitor(formatter);
			output.flush();

			return new NanoResponse(status, mime, new ByteArrayInputStream(buffer
					.toByteArray()));

		} catch (GenyrisException ey) {
			System.out.println("*** Error: " + ey.getMessage());
			return new NanoResponse(HTTP_OK, "text/plain", "*** Error: "
					+ ey.getMessage());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return new NanoResponse();

	}
}
