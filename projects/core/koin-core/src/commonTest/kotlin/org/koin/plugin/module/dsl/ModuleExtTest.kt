package org.koin.plugin.module.dsl

import org.koin.KoinCoreTest
import org.koin.Simple
import org.koin.core.definition.KoinDefinition
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals

class ModuleExtTest : KoinCoreTest() {

    @Test
    fun bindSupportsInfixSyntaxInCompilerDsl() {
        val mod = module { }
        val definition = mod.buildSingle(Simple.Component1::class, definition = { Simple.Component1() })

        val bound: KoinDefinition<*> = definition bind Simple.ComponentInterface1::class

        assertEquals(
            listOf(Simple.ComponentInterface1::class),
            bound.factory.beanDefinition.secondaryTypes,
        )
    }

    @Test
    fun bindCallSyntaxStillWorksInCompilerDsl() {
        val mod = module { }
        val definition = mod.buildSingle(Simple.Component1::class, definition = { Simple.Component1() })

        val bound = definition.bind(Simple.ComponentInterface2::class)

        assertEquals(
            listOf(Simple.ComponentInterface2::class),
            bound.factory.beanDefinition.secondaryTypes,
        )
    }

    @Test
    fun infixBindResolvesSecondaryType() {
        val app = koinApplication {
            modules(
                module {
                    buildSingle(Simple.Component1::class, definition = { Simple.Component1() }) bind Simple.ComponentInterface1::class
                },
            )
        }

        val koin = app.koin
        val component = koin.get<Simple.Component1>()
        val asInterface = koin.get<Simple.ComponentInterface1>()

        assertEquals(component, asInterface)
    }
}
