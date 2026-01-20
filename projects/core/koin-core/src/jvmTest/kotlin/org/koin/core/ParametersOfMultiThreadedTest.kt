package org.koin.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.logger.Level
import org.koin.core.module.dsl.factoryOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertNotNull

private class ComponentA(val id: String)
private class ComponentB(val id: Int)

class ParametersOfMultiThreadedTest {

    @Test
    fun verify_creates_components_using_parametersOf_multi_threaded() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(
                module {
                    factoryOf(::ComponentA)
                    factoryOf(::ComponentB)
                },
            )
        }.koin

        var componentA: ComponentA? = null
        val job = CoroutineScope(Dispatchers.Default).launch {
            componentA = koin.get<ComponentA> { parametersOf("a") }
        }
        val componentB = koin.get<ComponentB> { parametersOf(1) }
        runBlocking {
            job.join()
        }

        // Both components should be successfully resolved
        assertNotNull(componentA)
        assertNotNull(componentB)
    }
}