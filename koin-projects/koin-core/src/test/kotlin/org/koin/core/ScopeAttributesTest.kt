package org.koin.core

import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.Simple
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class ScopeAttributesTest {

    val scopeName = named("MY_SCOPE")

    @Test
    fun `can store & get an attribute value from scope instance`() {
        val koin = koinApplication {
            modules(
                    module {
                        scope(scopeName) {
                            scoped { Simple.MySingle(currentScope().properties["id"]) }
                        }
                    }
            )
        }.koin

        val scope = koin.createScope("myScope", scopeName)
        val id = 42
        scope.properties["id"] = id
        Assert.assertEquals(id, scope.get<Simple.MySingle>().id)
    }

    @Test
    fun `attribute value overwrite`() {
        val koin = koinApplication {
            modules(
                    module {
                        scope(scopeName) {}
                    }
            )
        }.koin

        val scope1 = koin.createScope("scope1", scopeName)
        scope1.properties["id"] = "42"
        val scope2 = koin.createScope("scope2", scopeName)
        scope2.properties["id"] = "24"

        assertEquals("42", koin.getScope("scope1").properties["id"])
        assertEquals("24", koin.getScope("scope2").properties["id"])
    }
}