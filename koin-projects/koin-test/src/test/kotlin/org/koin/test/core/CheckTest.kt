package org.koin.test.core

import org.junit.Assert.fail
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.check
import org.koin.test.error.BrokenDefinitionException
import org.koin.test.ext.junit.assertDefinitions
import org.koin.test.ext.junit.assertRemainingInstanceHolders

class CheckTest : AutoCloseKoinTest() {

    class ComponentA()
    class ComponentB(val componentA: ComponentA)

    interface Component
    class ComponentC() : Component
    class ComponentD(val component: Component)

    class ComponentE(val c: ComponentC) : Component

    @Test
    fun `successful check`() {
        check(listOf(module {
            single { ComponentA() }
            single { ComponentB(get()) }
        }))

        assertDefinitions(2)
        assertRemainingInstanceHolders(2)
    }

    @Test
    fun `successful check with injection params`() {
        check(listOf(module {
            single { (a : ComponentA) -> ComponentB(a) }
        }))

        assertDefinitions(1)
        assertRemainingInstanceHolders(1)
    }

    @Test
    fun `successful check with interface`() {
        check(listOf(module {
            single { ComponentC() }
            single { ComponentE(get()) as Component }
        }))

        assertDefinitions(2)
        assertRemainingInstanceHolders(2)

    }

    @Test
    fun `unsuccessful check`() {
        try {
            check(listOf(module {
                single { ComponentB(get()) }
            }))
            fail()
        } catch (e: BrokenDefinitionException) {
            System.err.println(e)
        }

        assertDefinitions(1)
    }

    @Test
    fun `interface definition check`() {
        check(listOf(module {
            single { ComponentC() as Component }
            single { ComponentD(get()) }
        }))

        assertDefinitions(2)
        assertRemainingInstanceHolders(2)
    }

    @Test
    fun `multiple interface & module definition check`() {
        check(listOf(module {
            single { ComponentC() as Component }
            module("otherModule") {
                single { ComponentC() as Component }
            }
            single { ComponentD(get()) }
        }))

        assertDefinitions(3)
        assertRemainingInstanceHolders(3)


    }

    @Test
    fun `mutiple module defs - check`() {
        check(listOf(
            module {
                module("otherModule") {
                    single { ComponentC() as Component }
                }
                single { ComponentD(get()) }
            },
            module {
                single { ComponentC() as Component }
            }
        ))

        assertDefinitions(3)
        assertRemainingInstanceHolders(3)

    }

    @Test
    fun `multiple interface definition check`() {
        check(listOf(module {
            single("default") { ComponentC() as Component }
            single("other") { ComponentC() as Component }
            single { ComponentD(get("default")) }
        }))

        assertDefinitions(3)
        assertRemainingInstanceHolders(3)

    }
}