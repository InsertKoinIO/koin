package org.koin.component

import org.junit.Assert
import org.junit.Test
import org.koin.Simple
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import org.koin.dsl.koin
import org.koin.dsl.module

class MyComponent : KoinComponent {
    val a_inject: Simple.ComponentA by inject()
    val a_get: Simple.ComponentA = get()
}

class KoinComponentTest {

    @Test
    fun `can lazy inject from KoinComponent`() {
        val app = koin {
            loadModules(
                module {
                    single { Simple.ComponentA() }
                })
        }.start()

        val koin = app.koin
        val a: Simple.ComponentA = koin.get()
        val component = MyComponent()

        Assert.assertEquals(component.a_inject, a)
        Assert.assertEquals(component.a_get, a)

        app.stop()
    }
}