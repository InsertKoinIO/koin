package org.koin.test

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
}