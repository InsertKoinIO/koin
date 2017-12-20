package org.koin.sample.util

import org.apache.http.HttpRequest
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.*
import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.socket.ConnectionSocketFactory
import org.apache.http.conn.socket.PlainConnectionSocketFactory
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.conn.BasicHttpClientConnectionManager
import org.apache.http.util.EntityUtils
import java.io.UnsupportedEncodingException
import java.net.URI
import java.util.*

class SparkTestUtil(val port: Int) {

    private val httpClient: HttpClient

    init {
        this.httpClient = httpClientBuilder().build()
    }

    private fun httpClientBuilder(): HttpClientBuilder {
        val socketRegistry = RegistryBuilder
                .create<ConnectionSocketFactory>()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .build()
        val connManager = BasicHttpClientConnectionManager(socketRegistry)
        return HttpClientBuilder.create().setConnectionManager(connManager)
    }

    fun get(path: String): UrlResponse {
        return doMethod("GET", path)
    }

    fun doMethod(requestMethod: String, path: String, body: String? = null, secureConnection: Boolean =  false, acceptType: String = "text/html", reqHeaders: Map<String, String>? = null): UrlResponse {
        val httpRequest = getHttpRequest(requestMethod, path, body, secureConnection, acceptType, reqHeaders)
        val httpResponse = httpClient.execute(httpRequest)

        val urlResponse = UrlResponse()
        urlResponse.status = httpResponse.statusLine.statusCode
        val entity = httpResponse.entity
        if (entity != null) {
            urlResponse.body = EntityUtils.toString(entity)
        } else {
            urlResponse.body = ""
        }
        val headers = HashMap<String, String>()
        val allHeaders = httpResponse.allHeaders
        for (header in allHeaders) {
            headers.put(header.name, header.value)
        }
        urlResponse.headers = headers
        return urlResponse
    }

    private fun getHttpRequest(requestMethod: String, path: String, body: String?, secureConnection: Boolean,
                               acceptType: String, reqHeaders: Map<String, String>?): HttpUriRequest {
        try {
            val protocol = if (secureConnection) "https" else "http"
            val uri = protocol + "://localhost:" + port + path

            if (requestMethod == "GET") {
                val httpGet = HttpGet(uri)
                httpGet.setHeader("Accept", acceptType)
                addHeaders(reqHeaders, httpGet)
                return httpGet
            }

            if (requestMethod == "POST") {
                val httpPost = HttpPost(uri)
                httpPost.setHeader("Accept", acceptType)
                addHeaders(reqHeaders, httpPost)
                httpPost.entity = StringEntity(body!!)
                return httpPost
            }

            if (requestMethod == "PATCH") {
                val httpPatch = HttpPatch(uri)
                httpPatch.setHeader("Accept", acceptType)
                addHeaders(reqHeaders, httpPatch)
                httpPatch.entity = StringEntity(body!!)
                return httpPatch
            }

            if (requestMethod == "DELETE") {
                val httpDelete = HttpDelete(uri)
                addHeaders(reqHeaders, httpDelete)
                httpDelete.setHeader("Accept", acceptType)
                return httpDelete
            }

            if (requestMethod == "PUT") {
                val httpPut = HttpPut(uri)
                httpPut.setHeader("Accept", acceptType)
                addHeaders(reqHeaders, httpPut)
                httpPut.entity = StringEntity(body!!)
                return httpPut
            }

            if (requestMethod == "HEAD") {
                val httpHead = HttpHead(uri)
                addHeaders(reqHeaders, httpHead)
                return httpHead
            }

            if (requestMethod == "TRACE") {
                val httpTrace = HttpTrace(uri)
                addHeaders(reqHeaders, httpTrace)
                return httpTrace
            }

            if (requestMethod == "OPTIONS") {
                val httpOptions = HttpOptions(uri)
                addHeaders(reqHeaders, httpOptions)
                return httpOptions
            }

            if (requestMethod == "LOCK") {
                val httpLock = HttpLock(uri)
                addHeaders(reqHeaders, httpLock)
                return httpLock
            }

            throw IllegalArgumentException("Unknown method " + requestMethod)

        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException(e)
        }

    }

    private fun addHeaders(reqHeaders: Map<String, String>?, req: HttpRequest) {
        if (reqHeaders != null) {
            for ((key, value) in reqHeaders) {
                req.addHeader(key, value)
            }
        }
    }

    class UrlResponse {

        var headers: Map<String, String>? = null
        var body: String? = null
        var status: Int = 0
    }

    internal class HttpLock(uri: String) : HttpRequestBase() {

        init {
            setURI(URI.create(uri))
        }

        override fun getMethod(): String {
            return METHOD_NAME
        }

        companion object {
            val METHOD_NAME = "LOCK"
        }
    }

}