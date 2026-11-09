package org.koin.ktor.ext

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.hooks.CallFailed
import io.ktor.server.application.hooks.ResponseSent
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondBytesWriter
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.util.cio.ChannelReadException
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.koin.core.scope.Scope
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.ktor.plugin.scope
import org.koin.module.requestScope
import java.io.IOException
import java.net.Socket
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// Real engine, real sockets: request scopes must not outlive their call (#2463 failed calls, #2472 cancelled calls).
// A handler throwing before responding is covered in KoinPluginRunTest: on Ktor 3.4.0 Netty that call gets no
// response until the client gives up, with or without Koin.
class RequestScopeLeakNettyTest {

    private val requestScopes = CopyOnWriteArrayList<Scope>()
    private val endedStreams = CopyOnWriteArrayList<Scope>()
    private val errorPageResolutions = CopyOnWriteArrayList<String>()
    private val catchBlockResolutions = CopyOnWriteArrayList<String>()
    private val http = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).connectTimeout(Duration.ofSeconds(2)).build()

    @Test
    fun responded_calls_close_their_request_scope() = withServer { port ->
        repeat(CALLS) { get(port, "/ok") }
        assertNoLeakedScopes()
    }

    @Test
    fun response_hook_failing_after_koin_closes_the_request_scope() = withServer { port ->
        repeat(CALLS) { get(port, "/later-hook-fails") }
        assertNoLeakedScopes()
    }

    @Test
    fun client_disconnecting_from_a_stream_closes_its_request_scope() = withServer { port ->
        repeat(CALLS) {
            Socket(LOOPBACK, port).use { socket ->
                socket.soTimeout = (REQUEST_TIMEOUT_SECONDS * 1_000).toInt()
                socket.getOutputStream().write("GET /stream HTTP/1.1\r\nHost: localhost\r\n\r\n".toByteArray())
                socket.getOutputStream().flush()
                socket.getInputStream().read(ByteArray(64))
            }
        }
        // A stream handler only learns its client left on a failed write, which Netty can report seconds later;
        // until then the handler legitimately still uses its scope.
        waitUntil(STREAM_END_TIMEOUT_MILLIS) { endedStreams.size == CALLS }
        assertTrue(endedStreams.isNotEmpty(), "no stream handler noticed its client leaving")
        waitUntil(CLOSE_TIMEOUT_MILLIS) { endedStreams.none { it.isLeaked() } }
        assertEquals(
            0,
            endedStreams.count { it.isLeaked() },
            "ended stream handlers kept their request scope open (${endedStreams.size} of $CALLS streams ended)",
        )
    }

    @Test
    fun stream_failing_after_headers_closes_its_request_scope() = withServer { port ->
        repeat(CALLS) {
            Socket(LOOPBACK, port).use { socket ->
                socket.soTimeout = (REQUEST_TIMEOUT_SECONDS * 1_000).toInt()
                socket.getOutputStream().write("GET /stream-fails HTTP/1.1\r\nHost: localhost\r\n\r\n".toByteArray())
                socket.getOutputStream().flush()
                socket.getInputStream().read(ByteArray(64))
                // keep the client connected: the server-side failure, not a disconnect, must close the scope
                Thread.sleep(STREAM_FAILURE_GRACE_MILLIS)
            }
        }
        assertNoLeakedScopes()
    }

    @Test
    fun error_page_after_a_stream_failing_mid_body_resolves_request_scoped_dependencies() = withServer { port ->
        repeat(FEW_CALLS) { readPartialStream(port, "/stream-fails") }
        // the failure unwinds on the server after the client has read the partial body
        waitUntil(CLOSE_TIMEOUT_MILLIS) { errorPageResolutions.size == FEW_CALLS }
        assertEquals(List(FEW_CALLS) { "request-scoped" }, errorPageResolutions.toList())
        assertNoLeakedScopes(FEW_CALLS)
    }

    @Test
    fun catch_block_after_a_stream_failing_mid_body_resolves_request_scoped_dependencies() = withServer { port ->
        repeat(FEW_CALLS) { readPartialStream(port, "/stream-fails-caught") }
        waitUntil(CLOSE_TIMEOUT_MILLIS) { catchBlockResolutions.size == FEW_CALLS }
        assertEquals(List(FEW_CALLS) { "request-scoped" }, catchBlockResolutions.toList())
        assertNoLeakedScopes(FEW_CALLS)
    }

    // The engine only logs a channel I/O failure and never answers, so ResponseSent never fires.
    @Test
    fun channel_io_failure_before_responding_closes_its_request_scope() = withServer { port ->
        repeat(FEW_CALLS) {
            Socket(LOOPBACK, port).use { socket ->
                socket.soTimeout = UNANSWERED_READ_TIMEOUT_MILLIS
                socket.getOutputStream().write("GET /channel-io-fails HTTP/1.1\r\nHost: localhost\r\n\r\n".toByteArray())
                socket.getOutputStream().flush()
                runCatching { socket.getInputStream().read(ByteArray(64)) }
            }
        }
        assertNoLeakedScopes(FEW_CALLS)
    }

    private fun readPartialStream(port: Int, path: String) {
        Socket(LOOPBACK, port).use { socket ->
            socket.soTimeout = (REQUEST_TIMEOUT_SECONDS * 1_000).toInt()
            socket.getOutputStream().write("GET $path HTTP/1.1\r\nHost: localhost\r\n\r\n".toByteArray())
            socket.getOutputStream().flush()
            socket.getInputStream().read(ByteArray(64))
            Thread.sleep(STREAM_FAILURE_GRACE_MILLIS)
        }
    }

    private fun withServer(block: suspend (port: Int) -> Unit) = runBlocking {
        val server = embeddedServer(Netty, port = 0, host = LOOPBACK) { leakingApplication() }.start(wait = false)
        try {
            block(server.engine.resolvedConnectors().first().port)
        } finally {
            server.stop(gracePeriodMillis = 100, timeoutMillis = 2_000)
        }
    }

    private fun Application.leakingApplication() {
        install(Koin) { modules(module { requestScope { scoped { RequestScopedDependency() } } }) }
        install(createApplicationPlugin("StreamErrorPage") {
            on(CallFailed) { call, _ ->
                if (call.request.local.uri == "/stream-fails") errorPageResolutions += call.resolveRequestScopedLabel()
            }
        })
        install(createApplicationPlugin("FailingResponseHook") {
            on(ResponseSent) { call -> if (call.request.local.uri == "/later-hook-fails") error("response hook failed") }
        })
        routing {
            get("/ok") { requestScopes += call.scope; call.respondText("ok") }
            get("/later-hook-fails") { requestScopes += call.scope; call.respondText("ok") }
            get("/stream-fails") {
                requestScopes += call.scope
                call.respondBytesWriter {
                    writeStringUtf8("partial\n")
                    flush()
                    error("stream producer failed after the headers were sent")
                }
            }
            get("/stream-fails-caught") {
                requestScopes += call.scope
                try {
                    call.respondBytesWriter {
                        writeStringUtf8("partial\n")
                        flush()
                        error("stream producer failed after the headers were sent")
                    }
                } catch (failure: IllegalStateException) {
                    catchBlockResolutions += call.resolveRequestScopedLabel()
                }
            }
            get("/channel-io-fails") {
                requestScopes += call.scope
                throw ChannelReadException("upstream read failed", IOException("connection reset"))
            }
            get("/stream") {
                val scope = call.scope
                requestScopes += scope
                try {
                    call.respondBytesWriter {
                        while (true) {
                            writeStringUtf8("tick\n")
                            flush()
                            delay(10)
                        }
                    }
                } finally {
                    endedStreams += scope
                }
            }
        }
    }

    private fun get(port: Int, path: String): String = try {
        val request = HttpRequest.newBuilder(URI("http://$LOOPBACK:$port$path")).timeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS)).build()
        http.send(request, HttpResponse.BodyHandlers.ofString()).body()
    } catch (e: IOException) {
        // a server-side failure may drop the connection: the scope bookkeeping below is what is under test
        "no response: $e"
    }

    private suspend fun assertNoLeakedScopes(calls: Int = CALLS) {
        assertEquals(calls, requestScopes.size, "every call should have opened a request scope (opened ${requestScopes.size})")
        waitUntil(CLOSE_TIMEOUT_MILLIS) { requestScopes.none { it.isLeaked() } }
        assertEquals(0, requestScopes.count { it.isLeaked() }, "request scopes still open and registered, out of $calls calls")
    }

    private fun ApplicationCall.resolveRequestScopedLabel(): String =
        runCatching { scope.get<RequestScopedDependency>().label }.getOrElse { it.toString() }

    private suspend fun waitUntil(timeoutMillis: Long, condition: () -> Boolean) {
        val deadline = System.currentTimeMillis() + timeoutMillis
        while (!condition() && System.currentTimeMillis() < deadline) delay(20)
    }

    private fun Scope.isLeaked(): Boolean = !closed || getKoin().getScopeOrNull(id) != null

    private companion object {
        const val CALLS = 50
        const val FEW_CALLS = 5
        const val UNANSWERED_READ_TIMEOUT_MILLIS = 300
        const val LOOPBACK = "127.0.0.1"
        const val CLOSE_TIMEOUT_MILLIS = 3_000L
        const val STREAM_END_TIMEOUT_MILLIS = 10_000L
        const val STREAM_FAILURE_GRACE_MILLIS = 50L
        const val REQUEST_TIMEOUT_SECONDS = 5L
    }
}
