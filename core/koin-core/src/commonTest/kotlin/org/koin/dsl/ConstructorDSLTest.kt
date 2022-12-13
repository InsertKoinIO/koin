package org.koin.dsl

import org.koin.Simple
import org.koin.core.logger.Level
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ConstructorDSLTest {

    @Test
    fun test_reified_type_cosntructor(){
        val koin = koinApplication {
            modules(module {
                singleOf<IClassA>(::ClassA)
            })
        }.koin
        assertNotNull(koin.getOrNull<IClassA>())
        assertNull(koin.getOrNull<ClassA>())
    }

    @Test
    fun test_allow_extra_binds(){

        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(module {
                singleOf(::ClassA) { bind<IClassA>()}
                singleOf(::ClassA2) { bind<IClassA>()}
            })
        }.koin

        assertNotNull(koin.getOrNull<IClassA>() is ClassA2)
        assertNotNull(koin.getOrNull<ClassA>())
        assertNotNull(koin.getOrNull<ClassA2>())
    }
}