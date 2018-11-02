package org.koin.dsl

import org.junit.Assert.assertEquals
import org.junit.Test

class InstanceResolutionTest {

    @Test
    fun `can resolve a single`() {

        val app = koin {
            loadModules(
                module {
                    single { Simple.ComponentA() }
                })
        }

        val koin = app.koin
        val a : Simple.ComponentA = koin.get()
        val a2 : Simple.ComponentA = koin.get()

        assertEquals(a,a2)
    }
}