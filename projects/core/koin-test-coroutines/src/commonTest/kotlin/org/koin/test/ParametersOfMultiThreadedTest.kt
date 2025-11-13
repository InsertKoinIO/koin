package org.koin.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertNotNull

private class ComponentA(val id: String)
private class ComponentB(val id: Int)

class ParametersOfMultiThreadedTest : KoinTest {

    @Test
    fun verify_creates_components_using_parametersOf_multi_threaded() {
        startKoin {
            modules(
                module {
                    factoryOf(::ComponentA)
                    factoryOf(::ComponentB)
                },
            )
        }

        var componentA: ComponentA? = null
        val job = CoroutineScope(Dispatchers.Default).launch {
            componentA = get<ComponentA> { parametersOf("a") }
        }
        val componentB = get<ComponentB> { parametersOf(1) }
        runBlocking {
            job.join()
        }

        // Both components should be successfully resolved
        assertNotNull(componentA)
        assertNotNull(componentB)

        stopKoin()
    }
}
