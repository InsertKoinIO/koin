package org.koin.dsl

import org.koin.KoinCoreTest
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import kotlin.test.Test
import kotlin.test.assertNotNull

class ModuleIncludeTest : KoinCoreTest() {

    @Test
    fun test_include() {
        val m2 = module {
            singleOf(::ClassB)
        }
        val m1 = module {
            includes(m2)
            singleOf(::ClassA)
        }

        val koin = koinApplication { modules(m1) }.koin
        assertNotNull(koin.getOrNull<ClassA>())
    }

    @Test
    fun test_include_higher() {
        val m3 = module {
            singleOf(::ClassB)
        }
        val m2 = module {
            includes(m3)
            singleOf(::ClassA)
        }
        val m1 = module {
            includes(m2)
        }

        val koin = koinApplication { modules(m1) }.koin
        assertNotNull(koin.getOrNull<ClassA>())
    }

    @Test
    fun test_include_higher2() {
        val m3 = module {
            singleOf(::ClassB)
        }
        val m2 = module {
            singleOf(::ClassA)
        }
        val m1 = module {
            includes(m2, m3)
        }

        val koin = koinApplication { modules(m1) }.koin
        assertNotNull(koin.getOrNull<ClassA>())
    }

    val q1 = named("1")
    val q2 = named("2")
    data class Person(val parent: Person? = null)
    val m2 = module {
        factory(q2) { Person(get(q1)) }
    }
    val m1 = module {
        factory(q1) { Person() }
        includes(m2)
    }

    @Test
    fun should_include_all() {
        val koin = koinApplication { modules(m1) }.koin
        assertNotNull(koin.getOrNull<Person>(q1))
        assertNotNull(koin.getOrNull<Person>(q2))
    }
}
