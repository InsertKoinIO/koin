package org.koin.test.core

import org.junit.Assert
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

class ScopingTest : AutoCloseKoinTest() {

    @Test
    fun `test with scope`() {
        val scopeName = "MyScope"
        StandAloneContext.loadKoinModules(
            module(scopeName) {
                single { "Example String we want!" }
            },
            module("SomeOtherScopeOverHere") {
                single { "DON'T GIVE ME THIS!" }
            }
        )

        val result = get<String>(module = scopeName)
        Assert.assertNotNull(result)
    }

    class A()
    class B(val a : A)
    class C(val a : A)

    @Test
    fun `Test weakRefs`(){
        val wr = WeakReference<A>(A(), ReferenceQueue<A>())

        var b : B? = B(wr.get() ?: error("can't find A"))
        var c : C? = C(wr.get() ?: error("can't find A"))

        println("b : $b & c : $c - a : ${wr.get()}")
        println("rq : ${wr.isEnqueued}")

        b = null
        System.gc()
        println("b : $b & c : $c - a : ${wr.get()}")
        println("rq : ${wr.isEnqueued}")
        c = null
        System.gc()
        println("b : $b & c : $c - a : ${wr.get()}")
        println("rq : ${wr.isEnqueued}")
    }
}