package org.koin.test.android

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.android.scope.currentScope
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.core.scope.HasParentScope
import org.koin.core.scope.ScopeID
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.spy

class AndroidScopeHierarchyTest: AutoCloseKoinTest() {

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
