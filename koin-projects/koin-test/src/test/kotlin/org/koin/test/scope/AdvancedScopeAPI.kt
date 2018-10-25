package org.koin.test.scope

import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.KoinTest

class AdvancedScopeAPI : KoinTest {

    data class Request(var name: String = "")

    val mod = module {
        scope("request"){ Request() }
    }

    @Test
    fun `create detached scope requests`() {
        startKoin(listOf(mod))

        val request1Scope = getKoin().createScope("request")
        val request2Scope = getKoin().createScope("request")

        val request1 = get<Request>(scope = request1Scope)
        request1.name = "request1"
        val request2 = get<Request>(scope = request2Scope)
        request2.name = "request2"

        assertNotEquals(request1, request2)
    }
}