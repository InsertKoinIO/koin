package org.koin.core

import org.koin.Simple
import org.koin.core.error.ClosedScopeException
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.*

class KoinAutoCloseableTest {

    private fun newApp() = koinApplication { modules(module { single { Simple.ComponentA() } }) }

    @Test
    fun `Koin implements AutoCloseable and works with use`() {
        val app = newApp()

        app.koin.use { it.get<Simple.ComponentA>() }

        assertFailsWith<ClosedScopeException> { app.koin.get<Simple.ComponentA>() }
    }

    @Test
    fun `Koin use closes on exception`() {
        val app = newApp()

        assertFailsWith<IllegalStateException>("test exception") {
            app.koin.use {
                error("test exception")
            }
        }
        assertFailsWith<ClosedScopeException> { app.koin.get<Simple.ComponentA>() }
    }

    @Test
    fun `KoinApplication implements AutoCloseable and works with use`() {
        val app = newApp()

        app.use { it.koin.get<Simple.ComponentA>() }

        assertFailsWith<ClosedScopeException> { app.koin.get<Simple.ComponentA>() }
    }

    @Test
    fun `KoinApplication use closes on exception`() {
        val app = newApp()

        assertFailsWith<IllegalStateException>("test exception") {
            app.use { error("test exception") }
        }
        assertFailsWith<ClosedScopeException> { app.koin.get<Simple.ComponentA>() }
    }
}
