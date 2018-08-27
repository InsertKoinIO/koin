package org.koin.test.core

import org.junit.Assert
import org.junit.Test
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext
import org.koin.standalone.get
import org.koin.test.AutoCloseKoinTest

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

    class A
    class B(val a: A)
    class C(val a: A)
}