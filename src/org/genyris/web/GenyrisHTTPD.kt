package org.genyris.web

import org.genyris.core.*
import org.genyris.core.Dictionary
import org.genyris.exception.GenyrisException
import org.genyris.format.Formatter
import org.genyris.format.HTMLFormatter
import org.genyris.format.IndentedFormatter
import org.genyris.format.JSONFormatter
import org.genyris.interp.ClassicReadEvalPrintLoop
import org.genyris.interp.Interpreter
import org.genyris.load.SourceLoader
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.util.*

class GenyrisHTTPD(port: Int, filename: String?, argv: Array<Exp?>) : NanoHTTPD() {
    var interpreter: Interpreter? = null
    var filename: String?
    var _argv: Array<Exp?>

    var NIL: Symbol? = null

    var HttpRequestClazz: Dictionary? = null
    var AlistClazz: Dictionary? = null

    init {
        myTcpPort = port
        this.filename = filename
        this._argv = argv

        try {
            ss = getSharedServerSocket(myTcpPort)
        } catch (e1: IOException) {
            throw GenyrisException(
                ("GenyrisHTTPD: Port " + myTcpPort + " "
                        + e1.getMessage())
            )
        }
    }

    @kotlin.Throws(IOException::class)
    override fun run(): Thread {
        val t = Thread(object : Runnable {
            override fun run() {
                var terminating = false
                try {
                    interpreterSetup()
                } catch (e: GenyrisException) {
                    System.out.println("GenyrisHTTPD: " + e.getMessage())
                    return
                }
                while (!terminating) {
                    try {
                        Thread.yield()
                        HTTPSession(ss.accept())
                    } catch (e: InterruptedIOException) {
                        if (Thread.currentThread().isInterrupted()) {
                            terminating = true
                        }
                        continue
                    } catch (ioe: IOException) {
                        System.out.println(
                            "GenyrisHTTPD: IOException "
                                    + ioe.getMessage()
                        )
                    }
                }

                // try {
                // if (ss != null)
                // ; // ss.close();
                // } catch (IOException e) {
                // }
            }
        })
        t.setName(
            (this.getClass().getName() + " " + myTcpPort + " "
                    + this.filename)
        )
        t.setDaemon(true)
        t.start()
        return t
    }

    @kotlin.jvm.Synchronized
    override fun serve(
        sessionNumber: Long, sock: Socket,
        uri: String?, method: String?, header: Properties, parms: Properties,
        rootdir: String?, clientIP: String?
    ): NanoResponse? {
        var request: Exp? = NIL

        // System.out.println(method + " '" + uri + "' ");
        var headers: Exp = NIL!!
        var e = header.propertyNames()
        while (e.hasMoreElements()) {
            val value = e.nextElement() as String?
            headers = Pair(
                Pair(
                    StrinG(value), StrinG(
                        header
                            .getProperty(value)
                    )
                ), headers
            )
            // System.out.println(" HDR: '" + value + "' = '" +
            // header.getProperty(value) + "'");
        }
        headers.addClass(AlistClazz)

        var parameters: Exp = NIL!!
        e = parms.propertyNames()
        while (e.hasMoreElements()) {
            val value = e.nextElement() as String?
            parameters = Pair(
                Pair(
                    StrinG(value), StrinG(
                        parms
                            .getProperty(value)
                    )
                ), parameters
            )
            // System.out.println(" PRM: '" + value + "' = '" +
            // parms.getProperty(value) + "'");
        }
        parameters.addClass(this.AlistClazz)

        request = Pair(Bignum(sessionNumber.toDouble()), request)
        request = Pair(Bignum(sock.getPort()), request)
        request = Pair(Pair(StrinG(clientIP), NIL), request)
        request = Pair(parameters, request)
        request = Pair(headers, request)
        request = Pair(StrinG(uri), request)
        request = Pair(StrinG(method), request)
        request.addClass(HttpRequestClazz)

        val buffer = ByteArrayOutputStream()
        val output: Writer = PrintWriter(buffer)
        // (httpd-serve request)
        val expression: Exp = Pair(
            interpreter!!.intern("httpd-serve"), Pair(
                request, NIL
            )
        )

        try {
            val formatter: Formatter?
            // formatter = new IndentedFormatter(output, 1, interpreter);
            // expression.acceptVisitor(formatter);
            var result = interpreter!!.evalInGlobalEnvironment(expression)
            val status = result.nth(0, NIL).toString()
            if (status == "SERVE-FILE") {
                // This response from Genyris means web server serves a static
                // file
                val rootDirectory = result.nth(1, NIL).toString()
                val filePath = result.nth(2, NIL).toString()
                val directoryListing = (result.nth(3, NIL).toString()
                        == "ls")

                return serveFile(
                    filePath, header, File(rootDirectory),
                    directoryListing
                )
            }
            result = result.cdr()
            var mime = "text/html"
            var responseHeaders: Exp = NIL!!
            if (result.car() is StrinG) {
                mime = result.car().toString()
            } else {
                responseHeaders = result.car()
            }
            var tmp = responseHeaders
            while (tmp !== NIL) {
                if (tmp.car().car().toString() == "Content-Type") {
                    mime = tmp.car().cdr().toString()
                }
                tmp = tmp.cdr()
            }
            if (mime == "text/html") {
                formatter = HTMLFormatter(output)
            } else if (mime == "application/json") {
                formatter = JSONFormatter(output)
            } else {
                formatter = IndentedFormatter(output, 2)
            }
            result = result.cdr().car()
            result.acceptVisitor(formatter)
            output.flush()
            val response = NanoResponse(
                status, mime,
                ByteArrayInputStream(buffer.toByteArray())
            )
            var tmph = responseHeaders
            while (tmph !== NIL) {
                response.addHeader(
                    tmph.car().car().toString(), tmph.car()
                        .cdr().toString()
                )
                tmph = tmph.cdr()
            }
            return response
        } catch (ey: GenyrisException) {
            System.out.println("*** Error: " + ey.getMessage())
            return NanoResponse(
                NanoHTTPD.Companion.HTTP_OK, "text/plain", "*** Error: "
                        + ey.getMessage()
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return NanoResponse()
    }

    @kotlin.Throws(GenyrisException::class)
    private fun interpreterSetup() {
        interpreter = Interpreter()
        interpreter!!.init(false, ClassicReadEvalPrintLoop.Companion.getContainingDirectoryPath(filename))
        val argv = interpreter!!.intern(PrefixSymbol(Constants.GENYRIS + "system#", Constants.ARGV, "sys"))
        NIL = interpreter!!.NIL
        interpreter!!.getGlobalEnv().defineVariable(argv, makeListOfArray(NIL, _argv))
        val output: Writer = PrintWriter(System.out)
        HttpRequestClazz = interpreter!!.lookupGlobalFromString("HttpRequest") as Dictionary?
        AlistClazz = interpreter!!.lookupGlobalFromString("Alist") as Dictionary?
        SourceLoader.loadScriptFromFile(
            interpreter!!.getGlobalEnv(), interpreter!!
                .getSymbolTable(), filename, output
        )
    }

    companion object {
        var serverSockets: HashMap<*, *> = HashMap<Any?, Any?>()

        @kotlin.jvm.Synchronized
        @kotlin.Throws(IOException::class)
        private fun getSharedServerSocket(port: Int): ServerSocket? {
            if (serverSockets.containsKey(Integer.valueOf(port))) {
                return serverSockets.get(Integer.valueOf(port)) as ServerSocket?
            } else {
                val ss = ServerSocket(port)
                ss.setSoTimeout(NanoHTTPD.Companion.SERVER_SOCKET_TIMEOUT)
                serverSockets.put(Integer.valueOf(port), ss)
                return ss
            }
        }

        private fun makeListOfArray(NIL: Symbol?, args: Array<Exp?>): Exp? {
            // TODO DRY - repeated in evaluater somewhere...
            var arglist: Exp? = NIL
            for (i in args.size - 1 downTo 1) {
                arglist = Pair(args[i], arglist)
            }
            return arglist
        }
    }
}
