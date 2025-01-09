package org.koin.dsl

import org.koin.KoinCoreTest
import org.koin.Simple
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ModuleRestartTest : KoinComponent, KoinCoreTest() {

    @BeforeTest
    fun before() {
        startKoin {
            modules(
                module {
                    single { Simple.ComponentA() }
                },
            )
        }
    }

    @Test
    fun first_test() {
        get<Simple.ComponentA>()
    }

    @Test
    fun second_test() {
        get<Simple.ComponentA>()
    }
}
