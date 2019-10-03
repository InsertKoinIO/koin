package org.koin.test.android

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import org.koin.android.scope.HasParentScope
import org.koin.android.scope.currentScope
import org.koin.core.context.startKoin
import org.koin.core.error.ParentScopeMismatchException
import org.koin.core.qualifier.named
import org.koin.core.scope.ScopeID
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.spy

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

            childObjectScope<Child> {
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

    @Test
    fun `fragment can have root scope as parent scope`() {
        class TestFragment: Fragment()

        startKoin {
            modules(module {
                single { "A" }
                objectScope<TestFragment>()
            })
        }.koin

        val fragment = spy(TestFragment())
        doReturn(Fragment()).`when`(fragment).parentFragment
        doReturn(FragmentActivity()).`when`(fragment).activity
        assertEquals("A", fragment.currentScope.get<String>())
    }

    @Test
    fun `fragment can have custom scope as parent scope`() {
        class TestFragment: Fragment(), HasParentScope {
            override val parentScopeId: ScopeID
                get() = "123"
        }

        val koin = startKoin {
            modules(module {
                single { "A" }
                typedScope<String> {
                    scoped { "B" }
                    childObjectScope<TestFragment>()
                }
            })
        }.koin

        koin.createScope("123", named<String>())
        val fragment = spy(TestFragment())
        doReturn(Fragment()).`when`(fragment).parentFragment
        doReturn(FragmentActivity()).`when`(fragment).activity

        assertEquals("B", fragment.currentScope.get<String>())
    }
}
