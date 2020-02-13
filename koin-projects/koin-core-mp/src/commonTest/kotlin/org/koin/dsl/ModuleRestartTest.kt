package org.koin.dsl

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.Simple
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.get

class ModuleRestartTest : KoinComponent {

    @Before
    fun before() {
        startKoin {
            modules(module {
                single { Simple.ComponentA() }
            })
        }
    }

    @After
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