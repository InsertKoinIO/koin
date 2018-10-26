package org.koin.test.scope

import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.error.ClosedScopeException
import org.koin.log.PrintLogger
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstanceHolders
import java.util.*

class AdvancedScopeAPI : AutoCloseKoinTest() {

    data class Request(var name: String = "")
    data class Extra(val id : String = UUID.randomUUID().toString())

    val mod = module {
        scope("request") { Request() }
    }

    @Test
    fun `create detached scope requests`() {
        startKoin(listOf(mod), logger = PrintLogger(showDebug = true))

        val request1Scope = getKoin().detachScope("request")
        val request2Scope = getKoin().detachScope("request")

        val request1 = get<Request>(scope = request1Scope)
        request1.name = "request1"
        val request2 = get<Request>(scope = request2Scope)
        request2.name = "request2"

        assertNotEquals(request1, request2)

        assertRemainingInstanceHolders(2)

        request1Scope.close()

        try {
            get<Request>(scope = request1Scope)
            fail()
        } catch (e: ClosedScopeException) {
        }

        assertRemainingInstanceHolders(1)

        request2Scope.close()

        try {
            get<Request>(scope = request2Scope)
            fail()
        } catch (e: ClosedScopeException) {
        }

        assertRemainingInstanceHolders(0)
    }

    @Test
    fun `reuse detached scope`() {
        startKoin(listOf(mod), logger = PrintLogger(showDebug = true))

        val scope = getKoin().detachScope("request")

        val value = "world"
        scope.parameters["hello"] = value

        val foundScope = getKoin().getDetachedScope(scope.uuid)

        assertEquals(scope,foundScope)
        assertEquals(foundScope.parameters["hello"], value)
    }

    @Test
    fun `add new component in scope`() {
        startKoin(listOf(mod))

        val scope = getKoin().createScope("request")
        val extra = Extra()
        scope.addInstance(extra)

        val getExtra = get<Extra>()
        assertEquals(extra,getExtra)
        assertDefinitions(2)
        assertRemainingInstanceHolders(1)

        scope.close()

        assertDefinitions(1)
        assertRemainingInstanceHolders(0)
    }
}