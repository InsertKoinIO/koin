package org.koin.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.fail
import org.junit.Test
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
    fun `create a new scope is different`() {
        val koin = koinApplication { }.koin

        val scopeId = "myScope"
        val scope1 = koin.createScope(scopeId)
        val scope2 = koin.createScope(scopeId)

        assertNotEquals(scope1, scope2)
    }

    @Test
    fun `can't find a scope by id`() {
        val koin = koinApplication { }.koin

        val scopeId = "myScope"
        koin.createScope(scopeId)
        koin.createScope(scopeId)

        try {
            koin.getScope(scopeId)
            fail()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Test
    fun `find a scope by uuid`() {
        val koin = koinApplication { }.koin

        val scopeId = "myScope"
        val scope1 = koin.createScope(scopeId)
        val scope2 = koin.createScope(scopeId)

        assertEquals(scope1,koin.getScopeByUUID(scope1.uuid))
        assertEquals(scope2,koin.getScopeByUUID(scope2.uuid))
    }
}