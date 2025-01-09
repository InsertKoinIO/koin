package org.koin.koincomponent

import org.koin.KoinCoreTest
import org.koin.Simple
import org.koin.core.Koin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.assertHasNoStandaloneInstance
import kotlin.native.concurrent.ThreadLocal
import kotlin.test.Test
import kotlin.test.assertEquals

abstract class CustomKoinComponent : KoinComponent {

    override fun getKoin(): Koin = customKoin

    @ThreadLocal
    companion object {
        val customKoin = koinApplication {
            modules(
                module {
                    single { Simple.ComponentA() }
                },
            )
        }.koin
    }
}

class MyCustomApp : CustomKoinComponent() {
    val a: Simple.ComponentA by inject()
}

class CustomKoinComponentTest : KoinCoreTest(){

    @Test
    fun can_inject_KoinComponent_from_custom_instance() {
        val app = MyCustomApp()
        val a = CustomKoinComponent.customKoin.get<Simple.ComponentA>()

        assertEquals(app.a, a)

        assertHasNoStandaloneInstance()
    }
}
