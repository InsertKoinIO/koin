package org.koin.test.core

import org.junit.Assert
import org.junit.Test
import org.koin.core.parameter.ParameterDefinition
import org.koin.core.parameter.ParameterList
import org.koin.core.parameter.parametersOf
import org.koin.test.AutoCloseKoinTest

class ParameterAPITest : AutoCloseKoinTest() {

    class Component1
    class Component2

    @Test
    fun `destructured declaration`() {
        val params = ParameterList("1", "2")
        val (s1: String, s2: String) = params

        Assert.assertEquals("1", s1)
        Assert.assertEquals("2", s2)
    }

    @Test
    fun `get parameter declaration`() {
        val c1 = Component1()
        val c2 = Component2()
        val params = ParameterList(c1, c2)
        Assert.assertEquals(c1, params.get<Component1>())
        Assert.assertEquals(c2, params.get<Component2>())
    }

    @Test
    fun `get from definition`() {
        val c1 = Component1()
        val c2 = Component2()
        val def: ParameterDefinition = { parametersOf(c1, c2) }
        val params = def()
        Assert.assertEquals(c1, params.get<Component1>())
        Assert.assertEquals(c2, params.get<Component2>())
    }
}