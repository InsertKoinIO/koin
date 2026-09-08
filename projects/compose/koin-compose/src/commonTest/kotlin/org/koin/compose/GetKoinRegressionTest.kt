package org.koin.compose

import androidx.compose.runtime.AbstractApplier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.Recomposer
import kotlinx.coroutines.Dispatchers
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Regression tests for getKoin() Compose context resolution across Koin restart (#2416)
 * and preservation of isolated Compose Koin contexts.
 *
 * @author Avik Makwana
 */
class GetKoinRegressionTest {

    private class TestService(val name: String)

    private class EmptyApplier : AbstractApplier<Unit>(Unit) {
        override fun insertBottomUp(index: Int, instance: Unit) {}
        override fun insertTopDown(index: Int, instance: Unit) {}
        override fun move(from: Int, to: Int, count: Int) {}
        override fun remove(index: Int, count: Int) {}
        override fun onClear() {}
    }

    private fun compose(content: @Composable () -> Unit) {
        val recomposer = Recomposer(Dispatchers.Unconfined)
        val composition = Composition(EmptyApplier(), recomposer)
        try {
            composition.setContent(content)
        } finally {
            composition.dispose()
            recomposer.close()
        }
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun test_getKoin_resolves_new_koin_instance_after_restart() {
        // First execution: start Koin instance A
        startKoin {
            modules(module {
                single { TestService("ServiceFromInstanceA") }
            })
        }
        var serviceA: TestService? = null
        compose {
            serviceA = getKoin().get<TestService>()
        }
        assertEquals("ServiceFromInstanceA", serviceA?.name)

        // Teardown: stop Koin instance A
        stopKoin()

        // Second execution: start fresh Koin instance B
        startKoin {
            modules(module {
                single { TestService("ServiceFromInstanceB") }
            })
        }
        var serviceB: TestService? = null
        compose {
            serviceB = getKoin().get<TestService>()
        }
        assertEquals("ServiceFromInstanceB", serviceB?.name)
    }

    @Test
    fun test_getKoin_preserves_isolated_context() {
        val isolatedApp = koinApplication {
            modules(module {
                single { TestService("IsolatedService") }
            })
        }

        var service: TestService? = null
        compose {
            KoinIsolatedContext(isolatedApp) {
                service = getKoin().get<TestService>()
            }
        }
        assertEquals("IsolatedService", service?.name)
    }

    @Test
    fun test_getKoin_preserves_preview_context() {
        var service: TestService? = null
        compose {
            KoinApplicationPreview(application = {
                modules(module {
                    single { TestService("PreviewService") }
                })
            }) {
                service = getKoin().get<TestService>()
            }
        }
        assertEquals("PreviewService", service?.name)
    }
}
