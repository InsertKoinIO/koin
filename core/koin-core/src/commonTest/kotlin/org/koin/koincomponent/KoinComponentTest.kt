package org.koin.koincomponent

import kotlin.test.*
import org.koin.Simple
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

@OptIn(KoinApiExtension::class)
class MyComponent : KoinComponent {
    val anInject: Simple.ComponentA by inject()
    val aGet: Simple.ComponentA = get()
}

@OptIn(KoinApiExtension::class)
class MyLazyComponent : KoinComponent {
    val anInject: Simple.ComponentA by inject()
}

class KoinComponentTest {

    @Test
    fun `can lazy inject from KoinComponent`() {
        val app = startKoin {
            printLogger()
            modules(
                    module {
                        single { Simple.ComponentA() }
                    })
        }

        val koin = app.koin
        val a: Simple.ComponentA = koin.get()
        val component = MyComponent()

        assertEquals(component.anInject, a)
        assertEquals(component.aGet, a)

        stopKoin()
    }

    @Test
    fun `can lazy inject before starting Koin`() {
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
                    })
        }

        val koin = app.koin
        val a: Simple.ComponentA = koin.get()
        assertEquals(component?.anInject, a)

        stopKoin()
    }
}