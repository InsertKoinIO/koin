package org.koin.core

import org.koin.Simple
import org.koin.core.error.ClosedScopeException
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.*

class KoinAutoCloseableTest {

    @Test
    fun `Koin implements AutoCloseable and works with use`() {
        val app = koinApplication {
            modules(
                module {
                    single { Simple.ComponentA() }
                },
            )
        }

        val result = app.koin.use {
            it.get<Simple.ComponentA>()
        }

        assertNotNull(result)
        assertFailsWith<ClosedScopeException> { app.koin.get<Simple.ComponentA>() }
    }

    @Test
    fun `Koin use closes on exception`() {
        val app = koinApplication {
            modules(
                module {
                    single { Simple.ComponentA() }
                },
            )
        }

        assertFailsWith<IllegalStateException>("test exception") {
            app.koin.use {
                error("test exception")
            }
        }
        assertFailsWith<ClosedScopeException> { app.koin.get<Simple.ComponentA>() }
    }

    @Test
    fun `KoinApplication implements AutoCloseable and works with use`() {
        val app = koinApplication {
            modules(
                module {
                    single { Simple.ComponentA() }
                },
            )
        }

        val result = app.use { it.koin.get<Simple.ComponentA>() }

        assertNotNull(result)
        assertFailsWith<ClosedScopeException> { app.koin.get<Simple.ComponentA>() }
    }

    @Test
    fun `KoinApplication use closes on exception`() {
        val app = koinApplication {
            modules(
                module {
                    single { Simple.ComponentA() }
                },
            )
        }

        assertFailsWith<IllegalStateException>("test exception") {
            app.use { error("test exception") }
        }
        assertFailsWith<ClosedScopeException> { app.koin.get<Simple.ComponentA>() }
    }
}
