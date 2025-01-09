package org.koin.koincomponent

import org.koin.KoinCoreTest
import org.koin.Simple
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MyComponent : KoinComponent {
    val anInject: Simple.ComponentA by inject()
    val aGet: Simple.ComponentA = get()
}

class MyLazyComponent : KoinComponent {
    val anInject: Simple.ComponentA by inject()
}

class KoinComponentTest : KoinCoreTest(){

    @Test
    fun can_lazy_inject_from_KoinComponent() {
        val app = startKoin {
            printLogger()
            modules(
                module {
                    single { Simple.ComponentA() }
                },
            )
        }

        val koin = app.koin
        val a: Simple.ComponentA = koin.get()
        val component = MyComponent()

        assertEquals(component.anInject, a)
        assertEquals(component.aGet, a)

        stopKoin()
    }

    @Test
    fun can_lazy_inject_before_starting_Koin() {
        var caughtException: Exception? = null
        var component: MyLazyComponent? = null
        try {
            component = MyLazyComponent()
        } catch (e: Exception) {
            caughtException = e
        }
        assertNull(caughtException)

        val app = startKoin {
            printLogger()
            modules(
                module {
                    single { Simple.ComponentA() }
                },
            )
        }

        val koin = app.koin
        val a: Simple.ComponentA = koin.get()
        assertEquals(component?.anInject, a)

        stopKoin()
    }
}
