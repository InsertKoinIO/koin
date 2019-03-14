package org.koin.koincomponent

import org.junit.Assert
import org.junit.Test
import org.koin.Simple
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.get
import org.koin.core.inject
import org.koin.dsl.module

class MyComponent : KoinComponent {
    val anInject: Simple.ComponentA by inject()
    val aGet: Simple.ComponentA = get()
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

        Assert.assertEquals(component.anInject, a)
        Assert.assertEquals(component.aGet, a)

        stopKoin()
    }
}