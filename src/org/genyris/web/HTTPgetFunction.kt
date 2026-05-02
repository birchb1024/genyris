// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.web

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.genyris.core.Exp
import org.genyris.core.StrinG
import org.genyris.core.Symbol
import org.genyris.exception.GenyrisException
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import java.io.IOException

class HTTPgetFunction(interp: Interpreter?) : HTTPclientFunction(interp, "get", true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>,
        envForBindOperations: Environment?
    ): Exp? {
        val URI: String? = getArg(arguments, 0, StrinG::class.java, true).toString()
        val headers = getArg(arguments, 1, Exp::class.java)
        val protocol = getArg(arguments, 2, Exp::class.java)
        val options = getArg(arguments, 3, Symbol::class.java)

        val httpclient: CloseableHttpClient = HTTPclientFunction.Companion.getCloseableHttpClient(options)
        val httpVersion = parseProtocol(protocol)

        try {
            val httpGet = HttpGet(URI)
            httpGet.setProtocolVersion(httpVersion)

            addHeadersToRequest(headers, charset, httpGet)

            val response = httpclient.execute(httpGet)

            return processResponse(URI, response)
        } catch (e: IOException) {
            throw GenyrisException(e.getMessage())
        } catch (e: RuntimeException) {
            throw GenyrisException(e.toString())
        } finally {
            try {
                httpclient.close()
            } catch (ignored: IOException) {
                ignored.printStackTrace()
            }
        }
    }
}
