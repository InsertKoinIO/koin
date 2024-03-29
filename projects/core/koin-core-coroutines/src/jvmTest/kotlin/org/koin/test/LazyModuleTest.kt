package org.koin.test

import kotlinx.coroutines.Dispatchers
import org.koin.core.lazyModules
import org.koin.core.logger.Level
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.includes
import org.koin.core.waitAllStartJobs
import org.koin.dsl.koinApplication
import org.koin.dsl.lazyModule
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ClassA : IClassA
class ClassB(val a: IClassA)
interface IClassA

class LazyModuleTest {

    @Test
    fun test_include() {
        var resolved: Boolean? = null
        val m2 = lazyModule {
            resolved = true
            singleOf(::ClassB)
        }
        val m1 = lazyModule {
            includes(m2)
            singleOf(::ClassA) { bind<IClassA>() }
        }
        assertTrue(resolved == null, "resolved should be null: $resolved")

        val koin = koinApplication {
            printLogger(Level.DEBUG)
            lazyModules(m1)
        }.koin
        koin.waitAllStartJobs()

        assertNotNull(koin.getOrNull<ClassB>())
    }
    
        @Test
    fun test_dispatchers() {
        var resolved: Boolean? = null
        val m2 = lazyModule {
            resolved = true
            singleOf(::ClassB)
        }
        val m1 = lazyModule {
            includes(m2)
            singleOf(::ClassA) { bind<IClassA>() }
        }
        assertTrue(resolved == null, "resolved should be null: $resolved")

        val koin = koinApplication {
            printLogger(Level.DEBUG)
            lazyModules(m1, dispatcher = Dispatchers.IO)
        }.koin
        koin.waitAllStartJobs()

        assertNotNull(koin.getOrNull<ClassB>())
    }

    @Test
    fun test_plus() {
        var m2Resolved: Boolean? = null
        val m2 = lazyModule {
            m2Resolved = true
            singleOf(::ClassB)
        }
        var m1Resolved: Boolean? = null
        val m1 = lazyModule {
            m1Resolved = true
            singleOf(::ClassA) { bind<IClassA>() }
        }

        assertTrue(m2Resolved == null, "m2Resolved should be null: $m2Resolved")
        assertTrue(m1Resolved == null, "m1Resolved should be null: $m1Resolved")

        val koin = koinApplication {
            printLogger(Level.DEBUG)
            lazyModules(m1 + m2)
        }.koin
        koin.waitAllStartJobs()

        assertNotNull(koin.getOrNull<ClassB>())
    }
}
