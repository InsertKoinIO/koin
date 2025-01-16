package org.koin.dsl

import org.koin.KoinCoreTest
import org.koin.Simple
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.module.Module
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

val myValModule = module { single { Simple.ComponentA() } }

val myGetModule : Module get() = module { single { Simple.ComponentA() }  }

fun myFunModule() = module { single { Simple.ComponentA() }  }

@OptIn(KoinInternalApi::class)
class ModuleFactoryIsolationTest : KoinCoreTest() {

    @Test
    fun testVariableIsolationAndInstanceFactories(){
        val a = myGetModule
        val b = myGetModule

        val aA = myValModule
        val aB = myValModule

        val aF = myFunModule()
        val bF = myFunModule()

        assertTrue(a.mappings != b.mappings)
        assertEquals(a.mappings.values.first().beanDefinition, b.mappings.values.first().beanDefinition)

        assertEquals(aA.mappings, aB.mappings)

        assertTrue(aF.mappings != bF.mappings)
        assertEquals(aF.mappings.values.first().beanDefinition, bF.mappings.values.first().beanDefinition)
    }

    @Test
    fun testVariableIsolationAndInstanceFactoriesLocal(){
        assertTrue(a.mappings != b.mappings)
        assertEquals(a.mappings.values.first().beanDefinition, b.mappings.values.first().beanDefinition)
    }

}

val ModuleFactoryIsolationTest.a : Module get() = myGetModule
val ModuleFactoryIsolationTest.b : Module get() = myGetModule
