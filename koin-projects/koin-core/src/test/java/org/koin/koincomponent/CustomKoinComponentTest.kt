package org.koin.koincomponent

import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.Simple
import org.koin.core.Koin
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.assertHasNoStandaloneInstance

abstract class CustomKoinComponent : KoinComponent {

    override fun getKoin(): Koin = customKoin

    companion object {
        val customKoin = koinApplication {
            modules(module {
                single { Simple.ComponentA() }
            })
        }.koin
    }
}

class MyCustomApp : CustomKoinComponent() {
    val a: Simple.ComponentA by inject()
}

class CustomKoinComponentTest {

    @Test
    fun `can inject KoinComponent from custom instance`() {
        val app = MyCustomApp()
        val a = CustomKoinComponent.customKoin.get<Simple.ComponentA>()

        assertEquals(app.a, a)

        assertHasNoStandaloneInstance()
    }
}