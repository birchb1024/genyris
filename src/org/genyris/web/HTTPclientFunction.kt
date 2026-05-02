package org.genyris.web

import org.apache.http.HttpMessage
import org.apache.http.HttpVersion
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.entity.BufferedHttpEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.ssl.SSLContextBuilder
import org.apache.http.ssl.TrustStrategy
import org.genyris.core.*
import org.genyris.exception.AccessException
import org.genyris.exception.GenyrisException
import org.genyris.interp.*
import org.genyris.io.readerstream.ReaderStream
import java.io.IOException
import java.io.InputStreamReader
import java.security.KeyManagementException
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate

abstract class HTTPclientFunction(interp: Interpreter, name: String?, eager: Boolean) : ApplicableFunction(
    interp, PrefixSymbol(
        Constants.WEB, name, "web"
    ), eager
) {
    var charset: String = "UTF-8"

    @kotlin.Throws(GenyrisException::class)
    abstract override fun bindAndExecute(
        proc: Closure?,
        arguments: Array<Exp?>?,
        envForBindOperations: Environment?
    ): Exp?

    @kotlin.Throws(IOException::class)
    protected fun processResponse(URI: String?, response: CloseableHttpResponse): Exp {
        try {
            var entity = response.getEntity()
            entity = BufferedHttpEntity(entity)
            val responseHeaders = response.getAllHeaders()
            var headerList: Exp? = NIL
            for (i in responseHeaders.indices.reversed()) {
                headerList = Pair.Companion.cons(
                    Pair.Companion.cons(
                        StrinG(responseHeaders[i]!!.getName()), StrinG(
                            responseHeaders[i]!!.getValue()
                        )
                    ), headerList
                )
            }

            return Pair.Companion.cons3(
                ReaderStream(InputStreamReader(entity.getContent()), URI),
                headerList, Pair.Companion.cons2(
                    Bignum(response.getStatusLine().getStatusCode()),
                    StrinG(response.getStatusLine().getReasonPhrase()), NIL
                ), NIL
            )
        } finally {
            response.close()
        }
    }

    @kotlin.Throws(AccessException::class)
    protected fun addHeadersToRequest(headers: Exp, charset: String?, httpGetOrPost: HttpMessage) {
        var headers = headers
        httpGetOrPost.setHeader(
            "Content-Type",
            "application/x-www-form-urlencoded;charset=" + charset
        )
        while (headers !== NIL) {
            httpGetOrPost.setHeader(
                headers.car().car().toString(), headers.car().cdr()
                    .toString()
            )
            headers = headers.cdr()
        }
    }

    protected fun parseProtocol(protocol: Exp): HttpVersion {
        var httpVersion = HttpVersion.HTTP_1_1
        if (protocol === NIL) return httpVersion

        if (protocol.toString() == "1.0") {
            httpVersion = HttpVersion.HTTP_1_0
        }
        return httpVersion
    }

    companion object {
        @kotlin.Throws(GenyrisException::class)
        protected fun getCloseableHttpClient(options: Exp): CloseableHttpClient? {
            var httpclient: CloseableHttpClient?

            try {
                httpclient = HttpClients.createDefault()
            } catch (e: Exception) {
                throw GenyrisException(e.toString())
            }

            if (options.toString() == "insecure") {
                // https://stackoverflow.com/questions/19517538/ignoring-ssl-certificate-in-apache-httpclient-4-3
                try {
                    val builder = SSLContextBuilder()
                    builder.loadTrustMaterial(null, object : TrustStrategy {
                        @kotlin.Throws(CertificateException::class)
                        override fun isTrusted(chain: Array<X509Certificate?>?, authType: String?): Boolean {
                            return true
                        }
                    })
                    val sslsf = SSLConnectionSocketFactory(builder.build(), NoopHostnameVerifier.INSTANCE)
                    httpclient = HttpClients.custom().setSSLSocketFactory(sslsf)
                        .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build()
                } catch (e: NoSuchAlgorithmException) {
                    e.printStackTrace()
                } catch (e: KeyManagementException) {
                    e.printStackTrace()
                } catch (e: KeyStoreException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    throw GenyrisException(e.toString())
                }
            }
            return httpclient
        }

        @kotlin.Throws(UnboundException::class, GenyrisException::class)
        fun bindFunctionsAndMethods(interpreter: Interpreter) {
            interpreter.bindGlobalProcedureInstance(HTTPgetFunction(interpreter))
            interpreter.bindGlobalProcedureInstance(HTTPpostFunction(interpreter))
        }
    }
}
