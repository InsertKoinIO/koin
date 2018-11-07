package org.koin.core

import org.junit.Assert.*
import org.junit.Test
import org.koin.core.parameter.ParametersHolder
import org.koin.core.parameter.ParametersHolder.Companion.MAX_PARAMS
import org.koin.core.parameter.parametersOf

class ParametersHolderTest {

    @Test
    fun `create a parameters holder`() {
        val myString = "empty"
        val myInt = 42
        val parameterHolder: ParametersHolder = parametersOf(myString, myInt)

        assertEquals(2, parameterHolder.size())
        assertTrue(parameterHolder.isNotEmpty())
    }

    @Test
    fun `create an empty parameters holder`() {
        val parameterHolder: ParametersHolder = parametersOf()

        assertEquals(0, parameterHolder.size())
        assertTrue(parameterHolder.isEmpty())
    }

    @Test
    fun `get parameters from a parameter holder`() {
        val myString = "empty"
        val myInt = 42
        val parameterHolder: ParametersHolder = parametersOf(myString, myInt)

        val (s: String, i: Int) = parameterHolder
        assertEquals(myString, s)
        assertEquals(myInt, i)
    }

    @Test
    fun `can't create parameters more than max params`() {
        try {
            parametersOf(1, 2, 3, 4, 5, 6)
            fail("Can't build more than $MAX_PARAMS")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}