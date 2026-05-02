// Copyright 2009 Peter William Birch <birchb@genyis.org>
//
// This software may be used and distributed according to the terms
// of the Genyris License, in the file "LICENSE", incorporated herein by reference.
//
package org.genyris.web

import org.apache.http.HttpVersion
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.message.BasicNameValuePair
import org.genyris.core.Exp
import org.genyris.core.Pair
import org.genyris.core.StrinG
import org.genyris.core.Symbol
import org.genyris.exception.GenyrisException
import org.genyris.interp.Closure
import org.genyris.interp.Environment
import org.genyris.interp.Interpreter
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import kotlin.collections.ArrayList
import kotlin.collections.MutableList

class HTTPpostFunction(interp: Interpreter?) : HTTPclientFunction(interp, "post", true) {
    @kotlin.Throws(GenyrisException::class)
    override fun bindAndExecute(
        proc: Closure?, arguments: Array<Exp?>,
        envForBindOperations: Environment?
    ): Exp? {
        val URI = getArg(arguments, 0, StrinG::class.java, true) as StrinG
        var params = getArg(arguments, 1, Exp::class.java)
        val headers = getArg(arguments, 2, Exp::class.java)
        val protocol = getArg(arguments, 3, Exp::class.java)
        val options = getArg(arguments, 4, Symbol::class.java)

        val httpclient: CloseableHttpClient
        val httpVersion: HttpVersion?

        try {
            httpclient = HTTPclientFunction.Companion.getCloseableHttpClient(options)
            httpVersion = parseProtocol(protocol)
        } catch (e: Exception) {
            throw GenyrisException(e.toString())
        }

        try {
            if (!isURI(URI)) {
                throw GenyrisException("post URI is not valid: " + URI.toString())
            }
            val httpPost = HttpPost(URI.toString())
            httpPost.setProtocolVersion(httpVersion)

            addHeadersToRequest(headers, charset, httpPost)

            // Now add post parameters...
            if (params is Pair) {
                val nvps: MutableList<NameValuePair?> = ArrayList<NameValuePair?>()
                while (params !== NIL) {
                    val item = params.car()
                    if (item !is Pair) {
                        throw GenyrisException("post parameters are not an Assoc: " + params)
                    }
                    nvps.add(
                        BasicNameValuePair(
                            item.car().toString(),
                            item.cdr().toString()
                        )
                    )
                    params = params.cdr()
                }
                httpPost.setEntity(UrlEncodedFormEntity(nvps))
            } else {
                httpPost.setEntity(StringEntity(params.toString()))
            }
            val response = httpclient.execute(httpPost)
            return processResponse(URI.toString(), response)
        } catch (e: Exception) {
            throw GenyrisException(e.toString())
        } finally {
            try {
                httpclient.close()
            } catch (ignored: IOException) {
                ignored.printStackTrace()
            }
        }
    }

    companion object {
        fun isURI(X: StrinG): Boolean {
            try {
                val uri = URI(X.toString())
                return uri.isAbsolute()
            } catch (e: URISyntaxException) {
            }
            return false
        }
    }
}
