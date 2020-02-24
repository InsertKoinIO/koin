package org.koin.test

import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertNotNull

class KoinQualifierTest : AutoCloseKoinTest() {

    val qualifier = named("42")
    val myString42: Simple.MyString by inject(qualifier)

    @Test
    fun `use qualifier`() {
        startKoin {
            modules(
                module {
                    single(qualifier) { Simple.MyString("42") }
                }
            )
        }

        assertNotNull(myString42)
    }

}