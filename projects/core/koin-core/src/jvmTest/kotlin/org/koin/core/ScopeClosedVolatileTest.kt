package org.koin.core

import org.koin.core.scope.Scope
import java.lang.reflect.Modifier
import kotlin.test.Test
import kotlin.test.assertTrue

class ScopeClosedVolatileTest {

    @Test
    fun `_closed field should be marked volatile for cross-thread visibility`() {
        // Deterministic check: verify the _closed field has the volatile modifier.
        // Without @Volatile, writes in close() under synchronized may not be visible
        // to unsynchronized reads in checkScopeIsOpen() on other threads.
        val field = Scope::class.java.getDeclaredField("_closed")
        assertTrue(
            Modifier.isVolatile(field.modifiers),
            "Scope._closed field must be @Volatile to guarantee visibility across threads. " +
                "Without it, a scope closed on Thread A may appear open on Thread B.",
        )
    }
}
