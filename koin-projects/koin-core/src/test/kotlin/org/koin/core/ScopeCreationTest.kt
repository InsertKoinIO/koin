package org.koin.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.fail
import org.junit.Test
import org.koin.core.error.ScopeNotAlreadyExistsException
import org.koin.dsl.koinApplication

class ScopeCreationTest {

    @Test
    fun `create a scope`() {
        val koin = koinApplication { }.koin

        val scopeId = "myScope"
        val scope1 = koin.createScope(scopeId)
        val scope2 = koin.getScope(scopeId)

        assertEquals(scope1, scope2)
    }

    @Test
    fun `can't find a non registered scope`() {
        val koin = koinApplication { }.koin

        val scopeId = "myScope"
        try {
            koin.getScope(scopeId)
            fail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun `create different scopes`() {
        val koin = koinApplication { }.koin

        val scope1 = koin.createScope("myScope1")
        val scope2 = koin.createScope("myScope2")

        assertNotEquals(scope1, scope2)
    }

    @Test
    fun `can't create a new scope if not closed`() {
        val koin = koinApplication { }.koin

        koin.createScope("myScope1")
        try {
            koin.createScope("myScope1")
            fail()
        } catch (e: ScopeNotAlreadyExistsException) {
            e.printStackTrace()
        }
    }

    @Test
    fun `can find a scope by id`() {
        val koin = koinApplication { }.koin

        val scopeId = "myScope"
        koin.createScope(scopeId)

        assertNotNull(koin.getScope(scopeId))
    }

    @Test
    fun `find a scope by id`() {
        val koin = koinApplication { }.koin

        val scopeId = "myScope"
        val scope1 = koin.createScope(scopeId)
        assertEquals(scope1, koin.getScope(scope1.id))
    }
}