package org.genyris.web

import java.io.*
import java.io.File
import java.lang.String
import java.net.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Any
import kotlin.Boolean
import kotlin.ByteArray
import kotlin.Char
import kotlin.CharArray
import kotlin.Error
import kotlin.Exception
import kotlin.Int
import kotlin.Long
import kotlin.NumberFormatException
import kotlin.Throwable
import kotlin.plus

/**
 * A simple, tiny, nicely embeddable HTTP 1.0 server in Java
 *
 *
 *
 * NanoHTTPD version 1.1, Copyright  2001,2005-2007 Jarno Elonen
 * (elonen@iki.fi, http://iki.fi/elonen/)
 *
 *
 *
 * **Features + limitations: **
 *
 *
 *  * Only one Java file
 *  * Java 1.1 compatible
 *  * Released as open source, Modified BSD licence
 *  * No fixed config files, logging, authorization etc. (Implement yourself if
 * you need them.)
 *  * Supports parameter parsing of GET and POST methods
 *  * Supports both dynamic content and file serving
 *  * Never caches anything
 *  * Doesn't limit bandwidth, request time or simultaneous connections
 *  * Default code serves files and shows all HTTP parameters and headers
 *  * File server supports directory listing, index.html and index.htm
 *  * File server does the 301 redirection trick for directories without '/'
 *  * File server supports simple skipping for files (continue download)
 *  * File server uses current directory as a web root
 *  * File server serves also very long files without memory overhead
 *  * Contains a built-in list of most common mime types
 *  * All header names are converted lowercase so they don't vary between
 * browsers/clients
 *
 *
 *
 *
 *
 * **Ways to use: **
 *
 *
 *  * Run as a standalone app, serves files from current directory and shows
 * requests
 *  * Subclass serve() and embed to your own program
 *  * Call serveFile() from serve() with your own base directory
 *
 *
 *
 * See the end of the source file for distribution license (Modified BSD
 * licence)
 */
open class NanoHTTPD {
    class NanoException(string: String?) : Exception(string) {
        companion object {
            private const val serialVersionUID = 8521291173564816199L
        }
    }

    protected var myTcpPort: Int = 0

    private val rootdir: String

    protected var ss: ServerSocket? = null

    constructor() {
        rootdir = "no root"
    }

    // ==================================================
    // API parts
    // ==================================================
    /**
     * Override this to customize the server.
     *
     *
     *
     * (By default, this delegates to serveFile() and allows directory listing.)
     * @param mySocket
     *
     * @parm uri Percent-decoded URI without parameters, for example
     * "/index.cgi"
     * @parm method "GET", "POST" etc.
     * @parm parms Parsed, percent decoded parameters from URI and, in case of
     * POST, data.
     * @parm header Header entries, percent decoded
     * @return HTTP response, see class NanoResponse for details
     */
    open fun serve(
        sessionNumber: Long, mySocket: Socket?, uri: String, method: String?, header: Properties,
        parms: Properties?, rootdir: String, IP: String?
    ): NanoResponse {
        return serveFile(uri, header, File(rootdir), true)
    }

    /**
     * HTTP response. Return one of these from serve().
     */
    class NanoResponse {
        /**
         * Default constructor: response = HTTP_OK, data = mime = 'null'
         */
        constructor() {
            this.status = HTTP_OK
        }

        /**
         * Basic constructor.
         */
        constructor(status: String, mimeType: String?, data: InputStream?) {
            this.status = status
            this.mimeType = mimeType
            this.data = data
        }

        /**
         * Convenience method that makes an InputStream out of given text.
         */
        constructor(status: String, mimeType: String?, txt: String) {
            this.status = status
            this.mimeType = mimeType
            this.data = ByteArrayInputStream(txt.getBytes())
        }

        /**
         * Adds given line to the header.
         */
        fun addHeader(name: String?, value: String?) {
            header.setProperty(name, value)
        }

        /**
         * HTTP status code after processing, e.g. "200 OK", HTTP_OK
         */
        var status: String

        /**
         * MIME type of content, e.g. "text/html"
         */
        var mimeType: String? = null

        /**
         * Data of the response, may be null.
         */
        var data: InputStream? = null

        /**
         * Headers for the HTTP response. Use addHeader() to add lines.
         */
        var header: Properties = Properties()
    }

    // ==================================================
    // Socket & server code
    // ==================================================
    /**
     * Starts a HTTP server to given port.
     *
     *
     * Throws an IOException if the socket is already in use
     *
     * @throws NanoException
     */
    constructor(port: Int, root: String) {
        myTcpPort = port
        this.rootdir = root
        val homeDir = File(root)
        if (!homeDir.exists()) throw NanoException(
            ("INTERNAL ERRROR: serveFile(): '"
                    + homeDir.getAbsolutePath() + "' does not exist.")
        )

        ss = ServerSocket(myTcpPort)
        ss!!.setSoTimeout(SERVER_SOCKET_TIMEOUT)
    }

    @kotlin.Throws(IOException::class)
    open fun run(): Thread {
        val t = Thread(object : Runnable {
            override fun run() {
                var terminating = false
                while (!terminating) {
                    try {
                        HTTPSession(ss!!.accept(), rootdir)
                    } catch (e: InterruptedIOException) {
                        if (Thread.currentThread().isInterrupted()) {
                            terminating = true
                        }
                        continue
                    } catch (ioe: IOException) {
                        System.out.println(
                            ("NanoHTTPD: Port " + myTcpPort + " "
                                    + ioe.getMessage())
                        )
                    }
                }
                try {
                    if (ss != null) ss!!.close()
                } catch (e: IOException) {
                }
            }
        })
        t.setName(
            (this.getClass().getName() + " " + myTcpPort + " "
                    + this.rootdir)
        )
        t.setDaemon(true)
        t.start()
        return t
    }


    /**
     * Handles one session, i.e. parses the HTTP request and returns the
     * response.
     */
    internal inner class HTTPSession : Runnable {
        private var rootdir: String? = null
        private val `is`: InputStream? = null
        private val clientIP: String? = null
        private val `in`: BufferedReader? = null
        private val sessionNumber: Long
        private var keepAlive: Boolean


        constructor(s: Socket, rootdir: String) {
            mySocket = s
            keepAlive = true
            sessionCount += 1
            this.sessionNumber = sessionCount.toLong()
            this.rootdir = rootdir
            val t = Thread(this)
            t.setDaemon(true)
            t.start()
        }

        constructor(socket: Socket) {
            keepAlive = true
            mySocket = socket
            sessionCount += 1
            this.sessionNumber = sessionCount.toLong()
            try {
                mySocket.setSoTimeout(1000 * CONNECTION_TIMEOUT_SECONDS)
                `is` = mySocket.getInputStream()
                clientIP = mySocket.getInetAddress().getHostAddress()
                `in` = BufferedReader(InputStreamReader(`is`))
            } catch (e: SocketException) {
                e.printStackTrace()
                return
            } catch (e: IOException) {
                e.printStackTrace()
                return
            }
            val t = Thread(this)
            t.setDaemon(true)
            t.start()
        }

        override fun run() {
            try {
                handleRequest()
            } catch (e: NanoException) {
                try {
                    mySocket.close()
                } catch (ignore: IOException) {
                }
            }
            try {
                mySocket.close()
            } catch (ignore: IOException) {
            }
        }

        @kotlin.Throws(NanoException::class)
        fun handleRequest() {
            try {
                if (`is` == null) throw NanoException("null is")
                // Read the request line
                val reqline: String?
                try {
                    reqline = `in`!!.readLine()
                } catch (toe: SocketTimeoutException) {
                    throw NanoException(toe.getMessage())
                } catch (ioe: Exception) {
                    val msg = ioe.getClass().getCanonicalName() + " " + ioe.getMessage()
                    throw NanoException(msg)
                }
                if (reqline == null) {
                    sendError(HTTP_BADREQUEST, "BAD REQUEST reqline == null")
                }
                val st = StringTokenizer(reqline)
                if (!st.hasMoreTokens()) sendError(HTTP_BADREQUEST, "BAD REQUEST " + Companion.toHex(reqline!!))

                val method = st.nextToken()

                if (!st.hasMoreTokens()) sendError(HTTP_BADREQUEST, "BAD REQUEST " + Companion.toHex(reqline!!))

                var uri = decodePercent(st.nextToken())

                // Decode parameters from the URI
                val parms = Properties()
                val qmi: Int = uri.indexOf('?'.code)
                if (qmi >= 0) {
                    decodeParms(uri.substring(qmi + 1), parms)
                    uri = decodePercent(uri.substring(0, qmi))
                }

                // If there's another token, it's protocol version,
                if (st.hasMoreTokens()) {
                    val version = st.nextToken()
                    if (version == "HTTP/1.0") {
                        keepAlive = false // close connection after sending.
                    }
                } else {
                    keepAlive = false // Assume HTTP/1.0 hence close connection after sending.
                }
                // followed by HTTP headers. 
                // NOTE: this now forces header names uppercase since they are
                // case insensitive and vary by client.
                val header = Properties()
                if (`in`.ready()) {
                    var line = `in`.readLine()
                    if (line != null) while (line.trim().length() > 0) {
                        val p: Int = line.indexOf(':'.code)
                        if (p <= 0) {
                            sendError(HTTP_BADREQUEST, "BAD REQUEST Malformed Header " + Companion.toHex(line!!))
                        }
                        header.setProperty(line.substring(0, p).trim().toLowerCase(), line.substring(p + 1).trim())
                        line = `in`.readLine()
                        if (line == null) break
                    }
                } else {
                    // Assume no headers
                }

                // If the method is POST, there may be parameters
                // in data section, too, read it:
                if (method.equalsIgnoreCase("POST")) {
                    var size = 0x7FFFFFFFFFFFFFFFL
                    val contentLength = header.getProperty("content-length")
                    if (contentLength != null) {
                        try {
                            size = Integer.parseInt(contentLength).toLong()
                        } catch (ex: NumberFormatException) {
                        }
                    }
                    var postLine = ""
                    val buf: CharArray? = CharArray(512)
                    var read = `in`.read(buf)
                    while (read >= 0 && size > 0 && !postLine.endsWith("\r\n")) {
                        size -= read.toLong()
                        postLine += String.valueOf(buf, 0, read)
                        if (size > 0) read = `in`.read(buf)
                    }
                    postLine = postLine.trim()
                    decodeParms(postLine, parms)
                }

                // Ok, now do the serve()
                val r = serve(
                    this.sessionNumber, this.mySocket, uri!!, method, header, parms, rootdir!!,
                    clientIP
                )
                if (r == null) sendError(
                    HTTP_INTERNALERROR,
                    "SERVER INTERNAL ERROR: Serve() returned a null response."
                )
                else {
                    sendResponse(r.status, r.mimeType, r.header, r.data)
                }
            } catch (e: Exception) {
                throw NanoException(e.getMessage())
            }
        }

        /**
         * Decodes the percent encoding scheme. <br></br>
         * For example: "an+example%20string" -> "an example string"
         */
        @kotlin.Throws(NanoException::class)
        private fun decodePercent(str: kotlin.String): kotlin.String? {
            try {
                val sb = StringBuffer()
                var i = 0
                while (i < str.length()) {
                    val c: Char = str.charAt(i)
                    when (c) {
                        '+' -> sb.append(' ')
                        '%' -> {
                            sb.append(
                                Integer.parseInt(
                                    str.substring(
                                        i + 1,
                                        i + 3
                                    ), 16
                                ).toChar()
                            )
                            i += 2
                        }

                        else -> sb.append(c)
                    }
                    i++
                }
                return kotlin.String(sb.toString().getBytes())
            } catch (e: Exception) {
                sendError(HTTP_BADREQUEST, "BAD REQUEST: " + toHex(str))
                return null
            }
        }

        /**
         * Decodes parameters in percent-encoded URI-format ( e.g.
         * "name=Jack%20Daniels&pass=Single%20Malt" ) and adds them to given
         * Properties.
         */
        @kotlin.Throws(NanoException::class)
        private fun decodeParms(parms: kotlin.String?, p: Properties) {
            if (parms == null) return

            val st = StringTokenizer(parms, "&")
            while (st.hasMoreTokens()) {
                val e = st.nextToken()
                val sep: Int = e.indexOf('='.code)
                if (sep >= 0) p.setProperty(
                    decodePercent(e.substring(0, sep)).trim(),
                    decodePercent(e.substring(sep + 1))
                )
            }
        }

        /**
         * Returns an error message as a HTTP response and throws
         * GenyrisInterruptedException to stop further request processing.
         */
        @kotlin.Throws(NanoException::class)
        private fun sendError(status: kotlin.String, msg: kotlin.String) {
            sendResponse(
                status, MIME_PLAINTEXT, null,
                ByteArrayInputStream(msg.getBytes())
            )
            throw NanoException(msg)
        }

        /**
         * Sends given response to the socket.
         */
        private fun sendResponse(
            status: kotlin.String, mime: kotlin.String?,
            header: Properties?, data: InputStream?
        ) {
            try {
                if (status == null) throw Error("sendResponse(): Status can't be null.")

                val out = mySocket.getOutputStream()
                val pw = PrintWriter(out)
                pw.print("HTTP/1.1 " + status + " \r\n")

                if (mime != null) pw.print("Content-Type: " + mime + "\r\n")

                if (data != null) pw.print("Content-Length: " + data.available() + "\r\n")

                if (header == null || header.getProperty("Date") == null) pw.print("Date: " + gmtFrmt!!.format(Date()) + "\r\n")
                pw.print("Connection-Timeout: " + CONNECTION_TIMEOUT_SECONDS + "\r\n")

                if (header != null) {
                    val e: Enumeration<*> = header.keys()
                    while (e.hasMoreElements()) {
                        val key = e.nextElement() as kotlin.String?
                        val value = header.getProperty(key)
                        pw.print(key + ": " + value + "\r\n")
                    }
                }

                pw.print("\r\n")
                pw.flush()

                if (data != null) {
                    val buff = ByteArray(2048)
                    while (true) {
                        val read = data.read(buff, 0, 2048)
                        if (read <= 0) break
                        out.write(buff, 0, read)
                    }
                }
                out.flush()
                // out.close();
                if (data != null) data.close()
            } catch (ioe: IOException) {
                // Couldn't write? No can do.
                try {
                    mySocket.close()
                } catch (t: Throwable) {
                }
            }
        }

        private val mySocket: Socket
    }

    /**
     * URL-encodes everything between "/"-characters. Encodes spaces as '%20'
     * instead of '+'.
     */
    private fun encodeUri(uri: kotlin.String): StringBuffer {
        val newUri = StringBuffer()
        val st = StringTokenizer(uri, "/ ", true)
        while (st.hasMoreTokens()) {
            val tok = st.nextToken()
            if (tok == "/") newUri.append('/')
            else if (tok == " ") newUri.append("%20")
            else {
                // newUri += URLEncoder.encode( tok );
                // For Java 1.4 you'll want to use this instead:
                try {
                    newUri.append(URLEncoder.encode(tok, "UTF-8"))
                } catch (uee: UnsupportedEncodingException) {
                }
            }
        }
        return newUri
    }

    // ==================================================
    // File server code
    // ==================================================
    /**
     * Serves file from homeDir and its subdirectories (only). Uses only URI,
     * ignores all headers and HTTP parameters.
     */
    fun serveFile(
        uri: kotlin.String, header: Properties, homeDir: File,
        allowDirectoryListing: Boolean
    ): NanoResponse {
        // Make sure we won't die of an exception later
        var uri = uri
        if (!homeDir.exists()) return NanoResponse(
            HTTP_INTERNALERROR, MIME_PLAINTEXT,
            ("INTERNAL ERRROR: serveFile(): '"
                    + homeDir.getAbsolutePath() + "' does not exist.")
        )
        if (!homeDir.isDirectory()) return NanoResponse(
            HTTP_INTERNALERROR, MIME_PLAINTEXT,
            "INTERNAL ERRROR: serveFile(): given homeDir is not a directory."
        )

        // Remove URL arguments
        uri = uri.trim().replace(File.separatorChar, '/')
        if (uri.indexOf('?'.code) >= 0) uri = uri.substring(0, uri.indexOf('?'.code))

        // Prohibit getting out of current directory
        if (uri.startsWith("..") || uri.endsWith("..")
            || uri.indexOf("../") >= 0
        ) return NanoResponse(
            HTTP_FORBIDDEN, MIME_PLAINTEXT,
            "FORBIDDEN: Won't serve ../ for security reasons."
        )

        var f = File(homeDir, uri)
        if (!f.exists()) return NanoResponse(
            HTTP_NOTFOUND, MIME_PLAINTEXT,
            "Error 404, file not found."
        )

        // List the directory, if necessary
        if (f.isDirectory()) {
            // Browsers get confused without '/' after the
            // directory, send a redirect.
            if (!uri.endsWith("/")) {
                uri += "/"
                val r = NanoResponse(
                    HTTP_REDIRECT, MIME_HTML,
                    ("<html><body>Redirected: <a href=\"" + uri + "\">"
                            + uri + "</a></body></html>")
                )
                r.addHeader("Location", uri)
                return r
            }

            // First try index.html and index.htm
            if (File(f, "index.html").exists()) f = File(homeDir, uri + "/index.html")
            else if (File(f, "index.htm").exists()) f = File(homeDir, uri + "/index.htm")
            else if (allowDirectoryListing) {
                val files = f.list()
                if (files == null) {
                    return NanoResponse(
                        HTTP_FORBIDDEN, MIME_PLAINTEXT,
                        "FORBIDDEN: No directory listing."
                    )
                }
                Arrays.sort(files)
                val msg = StringBuffer()
                msg.append("<html><head><title>")
                msg.append(uri)
                msg.append("</title></head><body><h1>Directory ")
                msg.append(uri)
                msg.append("</h1><br/>")

                if (uri.length() > 1) {
                    val u: kotlin.String = uri.substring(0, uri.length() - 1)
                    val slash: Int = u.lastIndexOf('/'.code)
                    if (slash >= 0 && slash < u.length()) {
                        msg.append("<b><a href=\"")
                        msg.append(uri.substring(0, slash + 1))
                        msg.append("\">..</a></b><br/>")
                    }
                }
                msg.append("<ul>")
                for (i in files.indices) {
                    val curFile = File(f, files[i])
                    val dir = curFile.isDirectory()
                    if (dir) {
                        msg.append("<b>")
                        files[i] += "/"
                    }

                    msg.append("<li><a href=\"")
                    msg.append(encodeUri(uri + files[i]))
                    msg.append("\">")
                    msg.append(files[i])
                    msg.append("</a></li>")

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
                    if (dir) msg.append("</b>")
                }
                msg.append("</ul></body></html>")
                return NanoResponse(HTTP_OK, MIME_HTML, msg.toString())
            } else {
                return NanoResponse(
                    HTTP_FORBIDDEN, MIME_PLAINTEXT,
                    "FORBIDDEN: No directory listing."
                )
            }
        }
        var fis: FileInputStream? = null
        try {
            // Get MIME type from file name extension, if possible
            var mime: kotlin.String? = null
            val dot: Int = f.getCanonicalPath().lastIndexOf('.'.code)
            if (dot >= 0) mime = theMimeTypes.get(
                f.getCanonicalPath()
                    .substring(dot + 1).toLowerCase()
            ) as kotlin.String?
            if (mime == null) mime = MIME_DEFAULT_BINARY

            // Support (simple) skipping:
            var startFrom: Long = 0
            var range = header.getProperty("Range")
            if (range != null) {
                if (range.startsWith("bytes=")) {
                    range = range.substring("bytes=".length())
                    val minus: Int = range.indexOf('-'.code)
                    if (minus > 0) range = range.substring(0, minus)
                    try {
                        startFrom = java.lang.Long.parseLong(range)
                    } catch (nfe: NumberFormatException) {
                    }
                }
            }

            fis = FileInputStream(f)
            val actuallySkipped = fis.skip(startFrom)
            if (actuallySkipped != startFrom) {
                fis.close()
                return NanoResponse(
                    HTTP_INTERNALERROR, MIME_PLAINTEXT,
                    ("INTERNAL ERRROR: serveFile(): '"
                            + homeDir.getAbsolutePath() + "' Sorry, skip to " + startFrom + " failed.")
                )
            }
            val r = NanoResponse(HTTP_OK, mime, fis)
            r.addHeader(
                "Content-range", ("" + startFrom + "-"
                        + (f.length() - 1) + "/" + f.length())
            )
            return r
        } catch (ioe: IOException) {
            return NanoResponse(
                HTTP_FORBIDDEN, MIME_PLAINTEXT,
                "FORBIDDEN: Reading file failed."
            )
        }
    }

    companion object {
        protected const val SERVER_SOCKET_TIMEOUT: Int = 0
        protected const val CONNECTION_TIMEOUT_SECONDS: Int = 10

        var sessionCount: Int = 0

        /**
         * Some HTTP response status codes
         */
        const val HTTP_OK: kotlin.String = "200 OK"
        const val HTTP_REDIRECT: kotlin.String = "301 Moved Permanently"
        const val HTTP_FORBIDDEN: kotlin.String = "403 Forbidden"
        const val HTTP_NOTFOUND: kotlin.String = "404 Not Found"
        const val HTTP_BADREQUEST: kotlin.String = "400 Bad Request"
        const val HTTP_INTERNALERROR: kotlin.String = "500 Internal Server Error"
        const val HTTP_NOTIMPLEMENTED: kotlin.String = "501 Not Implemented"

        /**
         * Common mime types for dynamic content
         */
        const val MIME_PLAINTEXT: kotlin.String = "text/plain"
        const val MIME_HTML: kotlin.String = "text/html"
        const val MIME_DEFAULT_BINARY: kotlin.String = "application/octet-stream"

        /**
         * Hashtable mapping (String)FILENAME_EXTENSION -> (String)MIME_TYPE
         */
        private val theMimeTypes: Hashtable<*, *> = Hashtable<Any?, Any?>()

        init {
            val st = StringTokenizer(
                ("g          text/plain "
                        + "java       text/plain "
                        + "htm        text/html "
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
                        + "class        application/octet-stream ")
            )
            while (st.hasMoreTokens()) theMimeTypes.put(st.nextToken(), st.nextToken())
        }

        /**
         * GMT date formatter
         */
        private val gmtFrmt: SimpleDateFormat? = null

        init {
            gmtFrmt = SimpleDateFormat(
                "E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US
            )
            gmtFrmt.setTimeZone(TimeZone.getTimeZone("GMT"))
        }

        fun toHex(arg: kotlin.String): kotlin.String {
            val ba: ByteArray = arg.getBytes()
            val str = StringBuilder()
            for (i in ba.indices) str.append(String.format("%x", ba[i]))
            return str.toString()
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
        //			+ "thlist of conditions and the following disclaimer. Redistributions in\n"
        //			+ "binary form must reproduce the above copyright notice, thlist of\n"
        //			+ "conditions and the following disclaimer in the documentation and/or other\n"
        //			+ "materials provided with the distribution. The name of the author may not\n"
        //			+ "be used to endorse or promote products derived from thsoftware without\n"
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
}
