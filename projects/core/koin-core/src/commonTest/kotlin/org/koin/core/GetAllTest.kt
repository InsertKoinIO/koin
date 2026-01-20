package org.koin.core

import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals

class GetAllTest {

    data class Entry(val value : String)

    @Test
    fun testMe(){
        val koin = koinApplication {
            modules(
                module {
                    single(named("entry_1")) { Entry("entry_1") }
                    single(named("entry_2")) { Entry("entry_2") }
                    single(named("entry_3")) { Entry("entry_3") }
                }
            )
        }.koin

        val entries = koin.getAll<Entry>()
        assertEquals(3,entries.size)
    }

    data class MyScope(val name : String)

    //TODO GetAll sur scope archetype - fail?

    @Test
    fun testMeSCope(){
        val koin = koinApplication {
            modules(
                module {
                    scope<MyScope> {
                        scoped(named("entry_1")) { Entry("entry_1") }
                        scoped(named("entry_2")) { Entry("entry_2") }
                        scoped(named("entry_3")) { Entry("entry_3") }
                    }
                }
            )
        }.koin
        val scope = koin.createScope<MyScope>()
        val entries = scope.getAll<Entry>()
        assertEquals(3,entries.size)
    }
}