package org.koin.dsl

import org.koin.core.module.dsl.singleOf
import kotlin.test.Test
import kotlin.test.assertNotNull

class ModuleIncludeTest {

    @Test
    fun test_include(){
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
    fun test_include_higher(){
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
    fun test_include_higher2(){
        val m3 = module {
            singleOf(::ClassB)
        }
        val m2 = module {
            singleOf(::ClassA)
        }
        val m1 = module {
            includes(m2,m3)
        }

        val koin = koinApplication { modules(m1) }.koin
        assertNotNull(koin.getOrNull<ClassA>())
    }
}