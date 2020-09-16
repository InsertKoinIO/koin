package org.koin.koincomponent

import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.Simple
import org.koin.core.Koin
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.assertHasNoStandaloneInstance

@OptIn(KoinApiExtension::class)
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