@file:OptIn(KoinInternalApi::class)

package org.koin.dsl

import org.koin.KoinCoreTest
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.instance.FactoryInstanceFactory
import org.koin.core.instance.ScopedInstanceFactory
import org.koin.core.instance.SingleInstanceFactory
import org.koin.core.module.dsl.*
import org.koin.core.qualifier.named
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ClassA : IClassA
class ClassA2 : IClassA
class ClassB(val a: IClassA)
interface IClassA

class NewDSLTest : KoinCoreTest() {

    @Test
    fun singleof_options() {
        val module = module {
            singleOf(::ClassA) {
                named("_name_")
                createdAtStart()
                bind<IClassA>()
            }
        }
        assertEquals(3, module.mappings.size)
        val factory = module.mappings.values.iterator().next()
        assertTrue {
            factory is SingleInstanceFactory<*> && factory.beanDefinition._createdAtStart
        }
        assertEquals(0, module.scopes.size)
        assertEquals(1, module.eagerInstances.size)
    }

    @Test
    fun factory_options() {
        val module = module {
            factoryOf(::ClassA) {
                named("_name_")
                bind<IClassA>()
            }
        }
        assertEquals(3, module.mappings.size)
        val factory = module.mappings.values.iterator().next()
        assertTrue {
            factory is FactoryInstanceFactory<*> && !factory.beanDefinition._createdAtStart
        }
        assertEquals(0, module.scopes.size)
        assertEquals(0, module.eagerInstances.size)
    }

    @Test
    fun scoped_options() {
        val module = module {
            scope(named("_scope_")) {
                scopedOf(::ClassA) { bind<IClassA>() }
            }
        }
        assertEquals(2, module.mappings.size)
        val factory = module.mappings.values.iterator().next()
        assertTrue("factory type") {
            factory is ScopedInstanceFactory<*> && !factory.beanDefinition._createdAtStart
        }
        assertEquals(1, module.scopes.size)
        assertEquals(0, module.eagerInstances.size)
    }

    @Test
    fun factory_options_run() {
        val module = module {
            factoryOf(::ClassA) { bind<IClassA>() }
        }
        val koin = koinApplication { modules(module) }.koin
        assertTrue { koin.get<IClassA>() != koin.get<IClassA>() }
    }

    @Test
    fun singleof_nodsl() {
        val module = module {
            singleOf(::ClassA) bind IClassA::class
            singleOf(::ClassB)
        }
        val koin = koinApplication { modules(module) }.koin
        assertNotNull(koin.getOrNull<ClassB>())
    }

    @Test
    fun singleof_deps() {
        val module = module {
            singleOf(::ClassA) { bind<IClassA>() }
            singleOf(::ClassB)
        }
        val koin = koinApplication { modules(module) }.koin
        assertNotNull(koin.getOrNull<ClassB>())
    }

    @Test
    fun singleof_get_options() {
        val module = module {
            singleOf(::ClassA) {
                named("_name_")
                bind<IClassA>()
            }
            single { ClassB(get(named("_name_"))) } withOptions {
                createdAtStart()
            }
        }

        assertEquals(1, module.eagerInstances.size)
        assertEquals(4, module.mappings.size)
    }
}
