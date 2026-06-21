package org.genyris.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.SimpleFileServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Executors;

/**
 * Minimal embedded HTTP server base class, replacing NanoHTTPD.
 * Uses com.sun.net.httpserver (standard JDK) and SimpleFileServer (Java 18+)
 * for static file serving. Zero external dependencies.
 *
 * Subclass and override serve(). Return null to delegate to serveStatic(),
 * or return an HttpResponse for dynamic content.
 */
public class GenyrisHttpServer {

    // --------------------------------------------------------
    // Status + MIME constants
    // --------------------------------------------------------

    public static final String HTTP_OK             = "200 OK";
    public static final String HTTP_REDIRECT       = "301 Moved Permanently";
    public static final String HTTP_FORBIDDEN      = "403 Forbidden";
    public static final String HTTP_NOTFOUND       = "404 Not Found";
    public static final String HTTP_BADREQUEST     = "400 Bad Request";
    public static final String HTTP_INTERNALERROR  = "500 Internal Server Error";
    public static final String HTTP_NOTIMPLEMENTED = "501 Not Implemented";

    public static final String MIME_PLAINTEXT      = "text/plain";
    public static final String MIME_HTML           = "text/html";
    public static final String MIME_DEFAULT_BINARY = "application/octet-stream";

    public static int sessionCount = 0;

    // --------------------------------------------------------
    // HttpResponse
    // --------------------------------------------------------

    public static class HttpResponse {
        public String      status;
        public String      mimeType;
        public InputStream data;
        public Properties  header = new Properties();

        public HttpResponse() {
            this.status = HTTP_OK;
        }

        public HttpResponse(String status, String mimeType, InputStream data) {
            this.status   = status;
            this.mimeType = mimeType;
            this.data     = data;
        }

        public HttpResponse(String status, String mimeType, String txt) {
            this.status   = status;
            this.mimeType = mimeType;
            this.data     = new ByteArrayInputStream(txt.getBytes());
        }

        public void addHeader(String name, String value) {
            header.setProperty(name, value);
        }
    }

    // --------------------------------------------------------
    // Fields
    // --------------------------------------------------------

    protected int myTcpPort;
    private HttpServer httpServer;

    // --------------------------------------------------------
    // Constructor
    // --------------------------------------------------------

    protected GenyrisHttpServer() {}

    // --------------------------------------------------------
    // Override in subclass.
    // Return null to fall through to serveStatic().
    // --------------------------------------------------------

    public HttpResponse serve(long sessionNumber,
                              String clientIP,
                              int clientPort,
                              String uri,
                              String method,
                              Properties header,
                              Properties parms) {
        return null;
    }

    // --------------------------------------------------------
    // Lifecycle
    // --------------------------------------------------------

    public Thread run() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(myTcpPort), 0);
        httpServer.createContext("/", new RootHandler());
        httpServer.setExecutor(Executors.newCachedThreadPool());
        httpServer.start();

        Thread t = new Thread(() -> {
            try { Thread.currentThread().join(); } catch (InterruptedException ignored) {}
        });
        t.setName(getClass().getName() + " " + myTcpPort);
        t.setDaemon(true);
        t.start();
        return t;
    }

    public void stop() {
        if (httpServer != null) httpServer.stop(0);
    }

    // --------------------------------------------------------
    // Static file serving with a dynamic root — called by subclasses
    // --------------------------------------------------------

    protected void serveStatic(HttpExchange exchange, String rootdir) throws IOException {
        SimpleFileServer.createFileHandler(Path.of(rootdir)).handle(exchange);
    }

    // --------------------------------------------------------
    // Request handler
    // --------------------------------------------------------

    private class RootHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            long sessionNumber;
            synchronized (GenyrisHttpServer.class) {
                sessionNumber = ++sessionCount;
            }

            String method   = exchange.getRequestMethod().toUpperCase();
            String uri      = decodePercent(exchange.getRequestURI().getRawPath());
            String query    = exchange.getRequestURI().getRawQuery();
            String clientIP = exchange.getRemoteAddress().getAddress().getHostAddress();
            int clientPort  = exchange.getRemoteAddress().getPort();

            Properties header = new Properties();
            for (Map.Entry<String, List<String>> entry : exchange.getRequestHeaders().entrySet())
                header.setProperty(entry.getKey().toLowerCase(), String.join(", ", entry.getValue()));

            Properties parms = new Properties();
            if (query != null) decodeParms(query, parms);

            if (method.equals("POST")) {
                String cl = header.getProperty("content-length");
                long size = cl != null ? Long.parseLong(cl.trim()) : Long.MAX_VALUE;
                try (InputStream body = exchange.getRequestBody()) {
                    StringBuilder sb = new StringBuilder();
                    byte[] buf = new byte[512];
                    int read;
                    while (size > 0 && (read = body.read(buf)) != -1) {
                        sb.append(new String(buf, 0, read));
                        size -= read;
                    }
                    decodeParms(sb.toString().trim(), parms);
                }
            }

            HttpResponse r = serve(sessionNumber, clientIP, clientPort,
                                   uri, method, header, parms);

            if (r == null) {
                serveStatic(exchange, null);
            } else {
                sendResponse(exchange, r);
            }
        }

        private void sendResponse(HttpExchange exchange, HttpResponse r) throws IOException {
            int statusCode = 200;
            try { statusCode = Integer.parseInt(r.status.trim().split(" ")[0]); }
            catch (NumberFormatException ignored) {}

            if (r.mimeType != null)
                exchange.getResponseHeaders().set("Content-Type", r.mimeType);

            if (r.header != null) {
                Enumeration<?> keys = r.header.propertyNames();
                while (keys.hasMoreElements()) {
                    String key = (String) keys.nextElement();
                    exchange.getResponseHeaders().set(key, r.header.getProperty(key));
                }
            }

            if (r.data != null) {
                byte[] body = r.data.readAllBytes();
                r.data.close();
                exchange.sendResponseHeaders(statusCode, body.length);
                try (OutputStream os = exchange.getResponseBody()) { os.write(body); }
            } else {
                exchange.sendResponseHeaders(statusCode, -1);
                exchange.getResponseBody().close();
            }
        }

        private String decodePercent(String str) {
            if (str == null) return "";
            try {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < str.length(); i++) {
                    char c = str.charAt(i);
                    if (c == '+') sb.append(' ');
                    else if (c == '%' && i + 2 < str.length()) {
                        sb.append((char) Integer.parseInt(str.substring(i + 1, i + 3), 16));
                        i += 2;
                    } else sb.append(c);
                }
                return sb.toString();
            } catch (NumberFormatException e) { return str; }
        }

        private void decodeParms(String parms, Properties p) {
            if (parms == null) return;
            StringTokenizer st = new StringTokenizer(parms, "&");
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                int sep = token.indexOf('=');
                if (sep >= 0)
                    p.setProperty(decodePercent(token.substring(0, sep)).trim(),
                                  decodePercent(token.substring(sep + 1)));
            }
        }
    }
}
