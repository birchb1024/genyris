package org.genyris.web;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TimeZone;

/**
 * A simple, tiny, nicely embeddable HTTP 1.0 server in Java
 * 
 * <p>
 * NanoHTTPD version 1.1, Copyright &copy; 2001,2005-2007 Jarno Elonen
 * (elonen@iki.fi, http://iki.fi/elonen/)
 * 
 * <p>
 * <b>Features + limitations: </b>
 * <ul>
 * 
 * <li>Only one Java file</li>
 * <li>Java 1.1 compatible</li>
 * <li>Released as open source, Modified BSD licence</li>
 * <li>No fixed config files, logging, authorization etc. (Implement yourself if
 * you need them.)</li>
 * <li>Supports parameter parsing of GET and POST methods</li>
 * <li>Supports both dynamic content and file serving</li>
 * <li>Never caches anything</li>
 * <li>Doesn't limit bandwidth, request time or simultaneous connections</li>
 * <li>Default code serves files and shows all HTTP parameters and headers</li>
 * <li>File server supports directory listing, index.html and index.htm</li>
 * <li>File server does the 301 redirection trick for directories without '/'</li>
 * <li>File server supports simple skipping for files (continue download)</li>
 * <li>File server uses current directory as a web root</li>
 * <li>File server serves also very long files without memory overhead</li>
 * <li>Contains a built-in list of most common mime types</li>
 * <li>All header names are converted lowercase so they don't vary between
 * browsers/clients</li>
 * 
 * </ul>
 * 
 * <p>
 * <b>Ways to use: </b>
 * <ul>
 * 
 * <li>Run as a standalone app, serves files from current directory and shows
 * requests</li>
 * <li>Subclass serve() and embed to your own program</li>
 * <li>Call serveFile() from serve() with your own base directory</li>
 * 
 * </ul>
 * 
 * See the end of the source file for distribution license (Modified BSD
 * licence)
 */
public class NanoHTTPD {

	protected static final int SERVER_SOCKET_TIMEOUT = 10;

	public static class NanoException extends Exception {
		private static final long serialVersionUID = 8521291173564816199L;

		public NanoException(String string) {
			super(string);
		}
	}

	protected int myTcpPort;

	private final String rootdir;

	protected ServerSocket ss;

	public NanoHTTPD() {
		rootdir = "no root";
	}

	// ==================================================
	// API parts
	// ==================================================

	/**
	 * Override this to customize the server.
	 * <p>
	 * 
	 * (By default, this delegates to serveFile() and allows directory listing.)
	 * 
	 * @parm uri Percent-decoded URI without parameters, for example
	 *       "/index.cgi"
	 * @parm method "GET", "POST" etc.
	 * @parm parms Parsed, percent decoded parameters from URI and, in case of
	 *       POST, data.
	 * @parm header Header entries, percent decoded
	 * @return HTTP response, see class NanoResponse for details
	 */
	public NanoResponse serve(String uri, String method, Properties header,
			Properties parms, String rootdir, String IP, String name) {
		return serveFile(uri, header, new File(rootdir), true);
	}

	/**
	 * HTTP response. Return one of these from serve().
	 */
	public static class NanoResponse {
		/**
		 * Default constructor: response = HTTP_OK, data = mime = 'null'
		 */
		public NanoResponse() {
			this.status = HTTP_OK;
		}

		/**
		 * Basic constructor.
		 */
		public NanoResponse(String status, String mimeType, InputStream data) {
			this.status = status;
			this.mimeType = mimeType;
			this.data = data;
		}

		/**
		 * Convenience method that makes an InputStream out of given text.
		 */
		public NanoResponse(String status, String mimeType, String txt) {
			this.status = status;
			this.mimeType = mimeType;
			this.data = new ByteArrayInputStream(txt.getBytes());
		}

		/**
		 * Adds given line to the header.
		 */
		public void addHeader(String name, String value) {
			header.put(name, value);
		}

		/**
		 * HTTP status code after processing, e.g. "200 OK", HTTP_OK
		 */
		public String status;

		/**
		 * MIME type of content, e.g. "text/html"
		 */
		public String mimeType;

		/**
		 * Data of the response, may be null.
		 */
		public InputStream data;

		/**
		 * Headers for the HTTP response. Use addHeader() to add lines.
		 */
		public Properties header = new Properties();
	}

	/**
	 * Some HTTP response status codes
	 */
	public static final String HTTP_OK = "200 OK",
			HTTP_REDIRECT = "301 Moved Permanently",
			HTTP_FORBIDDEN = "403 Forbidden", HTTP_NOTFOUND = "404 Not Found",
			HTTP_BADREQUEST = "400 Bad Request",
			HTTP_INTERNALERROR = "500 Internal Server Error",
			HTTP_NOTIMPLEMENTED = "501 Not Implemented";

	/**
	 * Common mime types for dynamic content
	 */
	public static final String MIME_PLAINTEXT = "text/plain",
			MIME_HTML = "text/html",
			MIME_DEFAULT_BINARY = "application/octet-stream";

	// ==================================================
	// Socket & server code
	// ==================================================

	/**
	 * Starts a HTTP server to given port.
	 * <p>
	 * Throws an IOException if the socket is already in use
	 * 
	 * @throws NanoException
	 */
	public NanoHTTPD(int port, final String root) throws IOException,
			NanoException {
		myTcpPort = port;
		this.rootdir = root;
		File homeDir = new File(root);
		if (!homeDir.exists())
			throw new NanoException("INTERNAL ERRROR: serveFile(): '"
					+ homeDir.getAbsolutePath() + "' does not exist.");

		ss = new ServerSocket(myTcpPort);
		ss.setSoTimeout(SERVER_SOCKET_TIMEOUT);
	}

	public Thread run() throws IOException {
		Thread t = new Thread(new Runnable() {
			public void run() {
				boolean terminating = false;
				while (!terminating) {
					try {
						new HTTPSession(ss.accept(), rootdir);
					} catch (InterruptedIOException e) {
						if (Thread.currentThread().isInterrupted()) {
							terminating = true;
						}
						continue;
					} catch (IOException ioe) {
						System.out.println("NanoHTTPD: Port " + myTcpPort + " "
								+ ioe.getMessage());
					}
				}
				try {
					if (ss != null)
						ss.close();
				} catch (IOException e) {
				}

			}
		});
		t.setName(this.getClass().getName() + " " + myTcpPort + " "
				+ this.rootdir);
		t.setDaemon(true);
		t.start();
		return t;
	}


	/**
	 * Handles one session, i.e. parses the HTTP request and returns the
	 * response.
	 */
	class HTTPSession implements Runnable {
		private String rootdir;
		private InputStream is;
		private String clientIP;
		private String clientName;
		private BufferedReader in;



		public HTTPSession(Socket s, String rootdir) {
			mySocket = s;
			this.rootdir = rootdir;
			Thread t = new Thread(this);
			t.setDaemon(true);
			t.start();
		}

		public HTTPSession(Socket socket) {
			mySocket = socket;
			try {
				 mySocket.setSoTimeout(1000*10);
				 is = mySocket.getInputStream();
				 clientIP = mySocket.getInetAddress().getHostAddress();
				 clientName = mySocket.getInetAddress().getCanonicalHostName();
				 in = new BufferedReader( new InputStreamReader(is));

			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			Thread t = new Thread(this);
			t.setDaemon(true);
			t.start();
		}

		public void run() {
			try {
				while(true) {
					if(is.available() > 0) {
						handleRequest();
					} else {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
						}
					}
				}
			} catch (NanoException e) {
				try {
					mySocket.close();
				} catch (IOException ignore) { }
				System.out.println(e.getMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		public void handleRequest() throws NanoException {
			try {
				if (is == null)
					throw new NanoException("null is");
				// Read the request line
				String reqline;
				try {
					 reqline = in.readLine();
				} catch (Exception ioe) {
					System.err.print(ioe.getMessage());
					throw new NanoException(ioe.getMessage());
				}
				if( reqline == null ) {
					return;					
				}
				StringTokenizer st = new StringTokenizer(reqline);
				if (!st.hasMoreTokens())
					sendError(HTTP_BADREQUEST,
							"BAD REQUEST: Syntax error. Usage: GET /example/file.html");

				String method = st.nextToken();

				if (!st.hasMoreTokens())
					sendError(HTTP_BADREQUEST,
							"BAD REQUEST: Missing URI. Usage: GET /example/file.html");

				String uri = decodePercent(st.nextToken());

				// Decode parameters from the URI
				Properties parms = new Properties();
				int qmi = uri.indexOf('?');
				if (qmi >= 0) {
					decodeParms(uri.substring(qmi + 1), parms);
					uri = decodePercent(uri.substring(0, qmi));
				}

				// If there's another token, it's protocol version,
				// followed by HTTP headers. Ignore version but parse headers.
				// NOTE: this now forces header names uppercase since they are
				// case insensitive and vary by client.
				Properties header = new Properties();
				if (st.hasMoreTokens()) {
					String line = in.readLine();
					if( line != null)
						while (line.trim().length() > 0) {
							int p = line.indexOf(':');
							header.put(line.substring(0, p).trim().toLowerCase(),
									line.substring(p + 1).trim());
							line = in.readLine();
							if(line == null) break;
						}
				}

				// If the method is POST, there may be parameters
				// in data section, too, read it:
				if (method.equalsIgnoreCase("POST")) {
					long size = 0x7FFFFFFFFFFFFFFFl;
					String contentLength = header.getProperty("content-length");
					if (contentLength != null) {
						try {
							size = Integer.parseInt(contentLength);
						} catch (NumberFormatException ex) {
						}
					}
					String postLine = "";
					char buf[] = new char[512];
					int read = in.read(buf);
					while (read >= 0 && size > 0 && !postLine.endsWith("\r\n")) {
						size -= read;
						postLine += String.valueOf(buf, 0, read);
						if (size > 0)
							read = in.read(buf);
					}
					postLine = postLine.trim();
					decodeParms(postLine, parms);
				}

				// Ok, now do the serve()
				NanoResponse r = serve(uri, method, header, parms, rootdir,
						clientIP, clientName);
				if (r == null)
					sendError(HTTP_INTERNALERROR,
							"SERVER INTERNAL ERROR: Serve() returned a null response.");
				else
					sendResponse(r.status, r.mimeType, r.header, r.data);

			} catch (IOException ioe) {
				try {
					sendError(HTTP_INTERNALERROR,
							"SERVER INTERNAL ERROR: IOException: "
									+ ioe.getMessage());
				} catch (Throwable t) {
				}
			}
		}

		/**
		 * Decodes the percent encoding scheme. <br/>
		 * For example: "an+example%20string" -> "an example string"
		 */
		private String decodePercent(String str) throws NanoException {
			try {
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < str.length(); i++) {
					char c = str.charAt(i);
					switch (c) {
					case '+':
						sb.append(' ');
						break;
					case '%':
						sb.append((char) Integer.parseInt(str.substring(i + 1,
								i + 3), 16));
						i += 2;
						break;
					default:
						sb.append(c);
						break;
					}
				}
				return new String(sb.toString().getBytes());
			} catch (Exception e) {
				sendError(HTTP_BADREQUEST, "BAD REQUEST: Bad percent-encoding.");
				return null;
			}
		}

		/**
		 * Decodes parameters in percent-encoded URI-format ( e.g.
		 * "name=Jack%20Daniels&pass=Single%20Malt" ) and adds them to given
		 * Properties.
		 */
		private void decodeParms(String parms, Properties p)
				throws NanoException {
			if (parms == null)
				return;

			StringTokenizer st = new StringTokenizer(parms, "&");
			while (st.hasMoreTokens()) {
				String e = st.nextToken();
				int sep = e.indexOf('=');
				if (sep >= 0)
					p.put(decodePercent(e.substring(0, sep)).trim(),
							decodePercent(e.substring(sep + 1)));
			}
		}

		/**
		 * Returns an error message as a HTTP response and throws
		 * GenyrisInterruptedException to stop furhter request processing.
		 */
		private void sendError(String status, String msg) throws NanoException {
			sendResponse(status, MIME_PLAINTEXT, null,
					new ByteArrayInputStream(msg.getBytes()));
			throw new NanoException(msg);
		}

		/**
		 * Sends given response to the socket.
		 */
		private void sendResponse(String status, String mime,
				Properties header, InputStream data) {
			try {
				if (status == null)
					throw new Error("sendResponse(): Status can't be null.");

				OutputStream out = mySocket.getOutputStream();
				PrintWriter pw = new PrintWriter(out);
				pw.print("HTTP/1.1 " + status + " \r\n");

				if (mime != null)
					pw.print("Content-Type: " + mime + "\r\n");

				if (data != null)
					pw.print("Content-Length: " + data.available() + "\r\n");

				if (header == null || header.getProperty("Date") == null)
					pw.print("Date: " + gmtFrmt.format(new Date()) + "\r\n");

				if (header != null) {
					Enumeration e = header.keys();
					while (e.hasMoreElements()) {
						String key = (String) e.nextElement();
						String value = header.getProperty(key);
						pw.print(key + ": " + value + "\r\n");
					}
				}

				pw.print("\r\n");
				pw.flush();

				if (data != null) {
					byte[] buff = new byte[2048];
					while (true) {
						int read = data.read(buff, 0, 2048);
						if (read <= 0)
							break;
						out.write(buff, 0, read);
					}
				}
				out.flush();
				// out.close();
				if (data != null)
					data.close();
			} catch (IOException ioe) {
				// Couldn't write? No can do.
				try {
					mySocket.close();
				} catch (Throwable t) {
				}
			}
		}

		private Socket mySocket;

	};

	/**
	 * URL-encodes everything between "/"-characters. Encodes spaces as '%20'
	 * instead of '+'.
	 */
	private StringBuffer encodeUri(String uri) {
		StringBuffer newUri = new StringBuffer();
		StringTokenizer st = new StringTokenizer(uri, "/ ", true);
		while (st.hasMoreTokens()) {
			String tok = st.nextToken();
			if (tok.equals("/"))
				newUri.append('/');
			else if (tok.equals(" "))
				newUri.append("%20");
			else {
				// newUri += URLEncoder.encode( tok );
				// For Java 1.4 you'll want to use this instead:
				try {
					newUri.append(URLEncoder.encode(tok, "UTF-8"));
				} catch (UnsupportedEncodingException uee) {
				}
			}
		}
		return newUri;
	}

	// ==================================================
	// File server code
	// ==================================================

	/**
	 * Serves file from homeDir and its subdirectories (only). Uses only URI,
	 * ignores all headers and HTTP parameters.
	 */
	public NanoResponse serveFile(String uri, Properties header, File homeDir,
			boolean allowDirectoryListing) {
		// Make sure we won't die of an exception later
		if (!homeDir.exists())
			return new NanoResponse(HTTP_INTERNALERROR, MIME_PLAINTEXT,
					"INTERNAL ERRROR: serveFile(): '"
							+ homeDir.getAbsolutePath() + "' does not exist.");
		if (!homeDir.isDirectory())
			return new NanoResponse(HTTP_INTERNALERROR, MIME_PLAINTEXT,
					"INTERNAL ERRROR: serveFile(): given homeDir is not a directory.");

		// Remove URL arguments
		uri = uri.trim().replace(File.separatorChar, '/');
		if (uri.indexOf('?') >= 0)
			uri = uri.substring(0, uri.indexOf('?'));

		// Prohibit getting out of current directory
		if (uri.startsWith("..") || uri.endsWith("..")
				|| uri.indexOf("../") >= 0)
			return new NanoResponse(HTTP_FORBIDDEN, MIME_PLAINTEXT,
					"FORBIDDEN: Won't serve ../ for security reasons.");

		File f = new File(homeDir, uri);
		if (!f.exists())
			return new NanoResponse(HTTP_NOTFOUND, MIME_PLAINTEXT,
					"Error 404, file not found.");

		// List the directory, if necessary
		if (f.isDirectory()) {
			// Browsers get confused without '/' after the
			// directory, send a redirect.
			if (!uri.endsWith("/")) {
				uri += "/";
				NanoResponse r = new NanoResponse(HTTP_REDIRECT, MIME_HTML,
						"<html><body>Redirected: <a href=\"" + uri + "\">"
								+ uri + "</a></body></html>");
				r.addHeader("Location", uri);
				return r;
			}

			// First try index.html and index.htm
			if (new File(f, "index.html").exists())
				f = new File(homeDir, uri + "/index.html");
			else if (new File(f, "index.htm").exists())
				f = new File(homeDir, uri + "/index.htm");

			// No index file, list the directory
			else if (allowDirectoryListing) {
				String[] files = f.list();
				if (files == null) {
					return new NanoResponse(HTTP_FORBIDDEN, MIME_PLAINTEXT,
							"FORBIDDEN: No directory listing.");
				}
				Arrays.sort(files);
				StringBuffer msg = new StringBuffer();
				msg.append("<html><head><title>");
				msg.append(uri);
				msg.append("</title></head><body><h1>Directory ");
				msg.append(uri);
				msg.append("</h1><br/>");

				if (uri.length() > 1) {
					String u = uri.substring(0, uri.length() - 1);
					int slash = u.lastIndexOf('/');
					if (slash >= 0 && slash < u.length()) {
						msg.append("<b><a href=\"");
						msg.append(uri.substring(0, slash + 1));
						msg.append("\">..</a></b><br/>");
					}
				}
				msg.append("<ul>");
				for (int i = 0; i < files.length; ++i) {
					File curFile = new File(f, files[i]);
					boolean dir = curFile.isDirectory();
					if (dir) {
						msg.append("<b>");
						files[i] += "/";
					}

					msg.append("<li><a href=\"");
					msg.append(encodeUri(uri + files[i]));
					msg.append("\">");
					msg.append(files[i]);
					msg.append("</a></li>");

					// Show file size
					// if (curFile.isFile()) {
					// long len = curFile.length();
					// msg += " &nbsp;<font size=2>(";
					// if (len < 1024)
					// msg += curFile.length() + " bytes";
					// else if (len < 1024 * 1024)
					// msg += curFile.length() / 1024 + "."
					// + (curFile.length() % 1024 / 10 % 100)
					// + " KB";
					// else
					// msg += curFile.length() / (1024 * 1024) + "."
					// + curFile.length() % (1024 * 1024) / 10
					// % 100 + " MB";
					//
					// msg += ")</font>";
					// }
					if (dir) msg.append("</b>");
				}
				msg.append("</ul></body></html>");
				return new NanoResponse(HTTP_OK, MIME_HTML, msg.toString());
			} else {
				return new NanoResponse(HTTP_FORBIDDEN, MIME_PLAINTEXT,
						"FORBIDDEN: No directory listing.");
			}
		}
		FileInputStream fis = null;
		try {
			// Get MIME type from file name extension, if possible
			String mime = null;
			int dot = f.getCanonicalPath().lastIndexOf('.');
			if (dot >= 0)
				mime = (String) theMimeTypes.get(f.getCanonicalPath()
						.substring(dot + 1).toLowerCase());
			if (mime == null)
				mime = MIME_DEFAULT_BINARY;

			// Support (simple) skipping:
			long startFrom = 0;
			String range = header.getProperty("Range");
			if (range != null) {
				if (range.startsWith("bytes=")) {
					range = range.substring("bytes=".length());
					int minus = range.indexOf('-');
					if (minus > 0)
						range = range.substring(0, minus);
					try {
						startFrom = Long.parseLong(range);
					} catch (NumberFormatException nfe) {
					}
				}
			}

			fis = new FileInputStream(f);
			long actuallySkipped = fis.skip(startFrom);
			if( actuallySkipped != startFrom) {
				return new NanoResponse(HTTP_INTERNALERROR, MIME_PLAINTEXT,
						"INTERNAL ERRROR: serveFile(): '"
								+ homeDir.getAbsolutePath() + "' Sorry, skip to " + startFrom + " failed.");
			}
			NanoResponse r = new NanoResponse(HTTP_OK, mime, fis);
			r.addHeader("Content-length", "" + (f.length() - startFrom));
			r.addHeader("Content-range", "" + startFrom + "-"
					+ (f.length() - 1) + "/" + f.length());
			return r;
		} catch (IOException ioe) {
			return new NanoResponse(HTTP_FORBIDDEN, MIME_PLAINTEXT,
					"FORBIDDEN: Reading file failed.");
		}
	}

	/**
	 * Hashtable mapping (String)FILENAME_EXTENSION -> (String)MIME_TYPE
	 */
	private static Hashtable theMimeTypes = new Hashtable();
	static {
		StringTokenizer st = new StringTokenizer("htm        text/html "
				+ "ico        image/vnd.microsoft.icon "
				+ "xml        text/xml " + "html        text/html "
				+ "txt        text/plain " + "asc        text/plain "
				+ "gif        image/gif " + "jpg        image/jpeg "
				+ "jpeg        image/jpeg " + "png        image/png "
				+ "mp3        audio/mpeg " + "m3u        audio/mpeg-url "
				+ "pdf        application/pdf "
				+ "doc        application/msword "
				+ "ogg        application/x-ogg "
				+ "zip        application/octet-stream "
				+ "exe        application/octet-stream "
				+ "class        application/octet-stream ");
		while (st.hasMoreTokens())
			theMimeTypes.put(st.nextToken(), st.nextToken());
	}

	/**
	 * GMT date formatter
	 */
	private static java.text.SimpleDateFormat gmtFrmt;
	static {
		gmtFrmt = new java.text.SimpleDateFormat(
				"E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
		gmtFrmt.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	/**
	 * The distribution licence
	 */
//	private static final String LICENCE = "Copyright (C) 2001,2005 by Jarno Elonen <elonen@iki.fi>\n"
//			+ "\n"
//			+ "Redistribution and use in source and binary forms, with or without\n"
//			+ "modification, are permitted provided that the following conditions\n"
//			+ "are met:\n"
//			+ "\n"
//			+ "Redistributions of source code must retain the above copyright notice,\n"
//			+ "this list of conditions and the following disclaimer. Redistributions in\n"
//			+ "binary form must reproduce the above copyright notice, this list of\n"
//			+ "conditions and the following disclaimer in the documentation and/or other\n"
//			+ "materials provided with the distribution. The name of the author may not\n"
//			+ "be used to endorse or promote products derived from this software without\n"
//			+ "specific prior written permission. \n"
//			+ " \n"
//			+ "THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR\n"
//			+ "IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES\n"
//			+ "OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.\n"
//			+ "IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,\n"
//			+ "INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT\n"
//			+ "NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,\n"
//			+ "DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY\n"
//			+ "THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT\n"
//			+ "(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE\n"
//			+ "OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.";
}
