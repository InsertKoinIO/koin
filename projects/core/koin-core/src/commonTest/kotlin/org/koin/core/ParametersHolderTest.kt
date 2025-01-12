package org.koin.core

import org.koin.Simple
import org.koin.core.parameter.ParametersHolder
import org.koin.core.parameter.parametersOf
import kotlin.test.*

class ParametersHolderTest {

    @Test
    fun `create a parameters holder - nullable params`() {
        val (i: Int, s: String?) = parametersOf(42, null)
        assertTrue(i == 42)
        assertTrue(s == null)
    }

    @Test
    fun `create a parameters holder - nullable resolution`() {
        val p = parametersOf(42)
        assertTrue(p.get<Int>() == 42)
        assertTrue(p.getOrNull<String>() == null)
    }

    @Test
    fun `create a parameters holder - 1 param`() {
        val myString = "empty"
        val myInt = 42
        val params = parametersOf(myString)
        val newParams = params.insert(0, myInt)

        assertEquals(2, newParams.size())
        assertEquals(newParams.get<Int>(0), myInt)
        assertEquals(newParams.get<String>(1), myString)
    }

    @Test
    fun `create a parameters holder - 2 params`() {
        val myString = "empty"
        val myInt = 42
        val params = parametersOf(myString, myInt)
        val newParams = params.insert(0, myInt)

        assertEquals(3, newParams.size())
        assertEquals(newParams.get<Int>(0), myInt)
        assertEquals(newParams.get<String>(1), myString)
    }

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
        assertNotNull(parametersOf(1, 2, 3, 4, 5, 6))
    }

    @Test
    fun `can insert param`() {
        val p = parametersOf(1, 2, 3, 4)
        assertTrue(p.insert(4, 5).get<Int>(4) == 5)
        assertTrue(p.insert(0, 0).get<Int>(0) == 0)
    }

    @Test
    fun `can add param`() {
        val p = parametersOf(1, 2, 3, 4)
        assertTrue(p.add(5).get<Int>(4) == 5)
    }

    @Test
    fun `can add into empty param`() {
        val p = parametersOf()
        assertTrue(p.add(5).get<Int>(0) == 5)
    }

    @Test
    fun `can insert at 0`() {
        val p = parametersOf(1, 2, 3, 4, 5)
        assertTrue(p.insert(0, 0).get<Int>(0) == 0)
    }

    @Test
    fun `get class value`() {
        val p = parametersOf("42")
        assertTrue(p.getOrNull<String>(String::class) == "42")
        assertTrue(p.getOrNull<String>(String::class) != "43")
        assertTrue(p.getOrNull<Int>(Int::class) == null)
    }

    @Test
    fun `ambiguous values`() {
        val p = parametersOf("42", "43")
        assertTrue(p.getOrNull<String>(String::class) == "42")
    }

    @Test
    fun `assignable type values`() {
        val p = parametersOf(Simple.Component1())
        assertNotNull(p.get<Simple.ComponentInterface1>())
    }

    @Test
    fun `equality check`() {
        val p1 = parametersOf(1, 2, 3, 4)
        val p2 = parametersOf(1, 2, 3, 4)
        val p3 = parametersOf(1, 2, 3)

        assertEquals(p1, p2)

        assertNotEquals(p1,p3)
    }

    @Test
    fun `equality mutable check`() {
        val p1 = parametersOf(1, 2, 3, 4)
        val p2 = parametersOf(1, 2, 3, 4)

        assertEquals(p1, p2)

        p2.add(5)

        assertNotEquals(p1, p2)
    }

    class DumBParam(v : ArrayList<Any?>) : ParametersHolder(v)

    @Test
    fun `equality check 2`() {
        val p1 = DumBParam(arrayListOf(1, 2, 3, 4))
        val p2 = DumBParam(arrayListOf(1, 2, 3, 4))
        val p3 = DumBParam(arrayListOf(1, 2, 3))

        assertEquals(p1, p2)

        assertNotEquals(p1,p3)
    }

    @Test
    fun `test equals considers useIndexedValues`() {
        val holderWithIndexed = ParametersHolder(mutableListOf(1, 2, 3), useIndexedValues = true)
        val holderWithoutIndexed = ParametersHolder(mutableListOf(1, 2, 3), useIndexedValues = false)

        // Assert they are not equal due to differing `useIndexedValues`
        assertNotEquals(holderWithIndexed, holderWithoutIndexed, "ParametersHolder instances with different useIndexedValues should not be equal.")
    }

    @Test
    fun `test mutability affects hashCode and equality`() {
        val holder = ParametersHolder(mutableListOf(1, 2, 3))
        val originalHashCode = holder.hashCode()

        // Modify the values list
        holder.add(4)
        // Assert hashCode changes after modification
        assertNotEquals(originalHashCode, holder.hashCode(), "hashCode should reflect changes in the values list.")
        // Assert equality is impacted
        val holderUnmodified = ParametersHolder(mutableListOf(1, 2, 3))
        assertNotEquals(holder, holderUnmodified, "ParametersHolder should not be equal after its content is modified.")
    }
}
