package org.koin.dsl

import org.koin.Simple
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(KoinApiExtension::class)
class ModuleRestartTest : KoinComponent {

    @BeforeTest
    fun before() {
        startKoin {
            modules(module {
                single { Simple.ComponentA() }
            })
        }
    }

    @AfterTest
    fun after() {
        stopKoin()
    }

    @Test
    fun `1st test`() {
        get<Simple.ComponentA>()
    }

    @Test
    fun `2nd test`() {
        get<Simple.ComponentA>()
    }
}