package org.genyris.web;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.genyris.core.Bignum;
import org.genyris.core.Exp;
import org.genyris.core.Pair;
import org.genyris.core.StrinG;
import org.genyris.exception.AccessException;
import org.genyris.exception.GenyrisException;
import org.genyris.interp.ApplicableFunction;
import org.genyris.interp.Closure;
import org.genyris.interp.Environment;
import org.genyris.interp.Interpreter;
import org.genyris.interp.UnboundException;
import org.genyris.io.readerstream.ReaderStream;

public abstract class HTTPclientFunction extends ApplicableFunction {
    
    String charset = "UTF-8";

    public HTTPclientFunction(Interpreter interp, String name, boolean eager) {
        super(interp, name, eager);
    } 
    
    protected static CloseableHttpClient getCloseableHttpClient(Exp options) throws GenyrisException {
        CloseableHttpClient httpclient;

        try {
            httpclient = HttpClients.createDefault();
        } catch (Exception e) {
            throw new GenyrisException(e.toString());
        }

        if (options.toString().equals("insecure")) {
            // https://stackoverflow.com/questions/19517538/ignoring-ssl-certificate-in-apache-httpclient-4-3
            try {
                SSLContextBuilder builder = new SSLContextBuilder();
                builder.loadTrustMaterial(null, new TrustStrategy() {
                    public boolean isTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                        return true;
                    }
                });
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(builder.build(), NoopHostnameVerifier.INSTANCE);
                httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (Exception e) {
                throw new GenyrisException(e.toString());
            }
        }
        return httpclient;
    }

    public abstract Exp bindAndExecute(Closure proc, Exp[] arguments, Environment envForBindOperations)
            throws GenyrisException;

    protected Exp processResponse(String URI, CloseableHttpResponse response) throws IOException {
        try {
            HttpEntity entity = response.getEntity();
            entity = new BufferedHttpEntity(entity);
            Header[] responseHeaders = response.getAllHeaders();
            Exp headerList = NIL;
            for (int i = responseHeaders.length - 1; i >= 0; i--) {
                headerList = Pair.cons(Pair.cons(
                        new StrinG(responseHeaders[i].getName()), new StrinG(
                                responseHeaders[i].getValue())), headerList);
            }

            return Pair.cons3(new ReaderStream( new InputStreamReader(entity.getContent()), URI),
                    headerList, Pair.cons2(new Bignum(response.getStatusLine().getStatusCode()),
                            new StrinG(response.getStatusLine().getReasonPhrase()),NIL), NIL);
        } finally {
            response.close();
        }
    }

    protected void addHeadersToRequest(Exp headers, String charset, HttpMessage httpGetOrPost)
            throws AccessException {

        httpGetOrPost.setHeader("Content-Type",
                        "application/x-www-form-urlencoded;charset=" + charset);
        while (headers != NIL) {
            httpGetOrPost.setHeader(headers.car().car().toString(), headers.car().cdr()
                    .toString());
            headers = headers.cdr();
        }
    }

    protected HttpVersion parseProtocol(Exp protocol) {
        HttpVersion httpVersion = HttpVersion.HTTP_1_1;
        if( protocol == NIL )
            return httpVersion;

        if ( protocol.toString().equals("1.0") ) {
            httpVersion = HttpVersion.HTTP_1_0;
        }
        return httpVersion;
    }

    public static void bindFunctionsAndMethods(Interpreter interpreter)
            throws UnboundException, GenyrisException {
        interpreter.bindGlobalProcedureInstance(new HTTPgetFunction(interpreter));
        interpreter.bindGlobalProcedureInstance(new HTTPpostFunction(interpreter));
    }

}
