package org.koin.test

import org.junit.Assert.assertNotNull
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

class KoinTestTest : KoinTest {

    val myString42: Simple.MyString by inject(named("42"))

    @Test
    fun `use qualifier`() {
        startKoin {
            modules(
                module {
                    single(named("42")) { Simple.MyString("42") }
                }
            )
        }

        assertNotNull(myString42)
    }

}