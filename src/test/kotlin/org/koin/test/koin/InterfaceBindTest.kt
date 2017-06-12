package org.koin.test.koin

import org.junit.Assert
import org.junit.Test
import org.koin.Koin

/**
 * Created by arnaud on 01/06/2017.
 */

interface MyInterface {
    fun doSomething()
}

class MyService : MyInterface {
    override fun doSomething() {
        println("do nothing")
    }
}

class InterfaceBindTest {

    @Test
    fun just_bind_interface() {
        val ctx = Koin().build()

        ctx.provide { MyService() }

        val instance = ctx.get<MyInterface>()

        Assert.assertNotNull(instance)
    }

    @Test
    fun just_bind_interface_class() {
        val ctx = Koin().build()

        ctx.provide(MyService::class)

        val instance = ctx.get<MyInterface>()

        Assert.assertNotNull(instance)
    }

}