package org.koin.koincomponent

import org.koin.Simple
import org.koin.core.Koin
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.assertHasNoStandaloneInstance
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * `customKoin` can not be a member of the companion object,
 * cause companion objects are global (shared by all threads)
 * and such objects are frozen by default in kotlin/native.
 *
 * Therefore, all members of the koinApplication are also frozen,
 * including the `SingleDefinitionInstance` holding `Simple.ComponentA`.
 *
 * Because `SingleDefinitionInstance` contains a `var value`,
 * that will be mutated when the instance is lazily created,
 * a `kotlin.native.concurrent.InvalidMutabilityException` will be thrown
 * when the application is stored in a frozen object.
 */
private val customKoin = koinApplication {
    modules(module {
        single { Simple.ComponentA() }
    })
}.koin

abstract class CustomKoinComponent : KoinComponent {

    override fun getKoin(): Koin = customKoin
}

class MyCustomApp : CustomKoinComponent() {
    val a: Simple.ComponentA by inject()
}

class CustomKoinComponentTest {

    @Test
    fun `can inject KoinComponent from custom instance`() {
        val app = MyCustomApp()
        val a = customKoin.get<Simple.ComponentA>()

        assertEquals(app.a, a)

        assertHasNoStandaloneInstance()
    }
}
