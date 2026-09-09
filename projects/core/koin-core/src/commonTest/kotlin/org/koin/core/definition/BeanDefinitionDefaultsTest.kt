package org.koin.core.definition

import org.koin.Simple
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.QualifierValue
import org.koin.core.qualifier.named
import org.koin.ext.getFullName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * KTZ-4829: pins two registration-path details that were changed for allocation, so that the
 * observable behavior stays exactly what it was.
 */
class BeanDefinitionDefaultsTest {

    private fun definition() = BeanDefinition(
        scopeQualifier = named("test-scope"),
        primaryType = Simple.ComponentA::class,
        definition = { Simple.ComponentA() },
        kind = Kind.Singleton,
    )

    // The default Callbacks instance is shared between definitions. It is immutable, so this is
    // only safe as long as replacing it on one definition never shows up on another.
    @Test
    fun default_callbacks_are_empty_and_independent_per_definition() {
        val first = definition()
        val second = definition()
        assertNull(first.callbacks.onClose)
        assertNull(second.callbacks.onClose)

        first.callbacks = Callbacks(onClose = { })

        assertNotNull(first.callbacks.onClose)
        assertNull(second.callbacks.onClose, "replacing callbacks on one definition leaked into another")
    }

    @Test
    fun index_key_format_is_unchanged() {
        val fullName = Simple.ComponentA::class.getFullName()

        assertEquals("$fullName:q:scope", indexKey(Simple.ComponentA::class, named("q"), named("scope")))
        assertEquals("$fullName::scope", indexKey(Simple.ComponentA::class, null, named("scope")))
    }

    // Pre-existing asymmetry, kept on purpose: the type qualifier contributes its value, the
    // scope qualifier contributes its toString(). A custom Qualifier where the two differ must
    // keep producing the same key it always did.
    @Test
    fun index_key_uses_value_for_type_qualifier_and_toString_for_scope_qualifier() {
        class Odd : Qualifier {
            override val value: QualifierValue = "value"
            override fun toString(): String = "string"
        }
        val fullName = Simple.ComponentA::class.getFullName()

        assertEquals("$fullName:value:string", indexKey(Simple.ComponentA::class, Odd(), Odd()))
    }
}
