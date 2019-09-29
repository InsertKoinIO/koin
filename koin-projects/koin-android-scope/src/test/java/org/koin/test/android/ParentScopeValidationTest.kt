package org.koin.test.android

import org.junit.Assert.fail
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.error.ParentScopeMismatchException
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest

class ParentScopeValidationTest: AutoCloseKoinTest() {

    class Parent
    class Child

    val module = module {

        factory(named("B")) { "Root" }

        objectScope<Parent> {

            factory(named("A")) {
                val root = get<String>(named("B"))
                "$root Parent"
            }

            childObjectScope<Child>(validateParentScope = true) {
                scoped {
                    val parent = get<String>(named("A"))
                    "$parent Child"
                }
            }
        }
    }

    @Test
    fun `detects parent scope mismatch`() {
        val koin = startKoin {
            modules(module)
        }.koin

        try {
            val child = Child()
            val parentScopeId = "234"
            koin.createScope(parentScopeId, named<Child>())
            koin.createObjectScoped(child, parentScopeId = parentScopeId)
            fail("Parent scope mismatch not detected")
        } catch (e: ParentScopeMismatchException) {
            println(e.toString())
        } catch (e: Exception) {
            fail("Parent scope mismatch not detected, another exception was thrown instead: $e")
        }
    }

    @Test
    fun `detects valid parent scope configuration`() {
        val koin = startKoin {
            modules(module)
        }.koin

        try {
            val child = Child()
            val parentScopeId = "234"
            koin.createScope(parentScopeId, named<Parent>())
            koin.createObjectScoped(child, parentScopeId = parentScopeId)
        } catch (e: ParentScopeMismatchException) {
            fail("Parent scope mismatch detected, but configuration should be valid")
        } catch (e: Exception) {
            fail("Parent scope mismatch not detected, another exception was thrown instead: $e")
        }
    }

}
