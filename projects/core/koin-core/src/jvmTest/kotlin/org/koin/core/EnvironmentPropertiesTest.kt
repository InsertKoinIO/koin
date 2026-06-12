package org.koin.core

import org.koin.core.annotation.KoinInternalApi
import org.koin.core.registry.saveProperties
import org.koin.dsl.koinApplication
import java.util.Properties
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

/**
 * Regression test for #2348. java.util.Properties can legally hold non-String keys/values
 * (e.g. after System.setProperties(...) with arbitrary objects). Koin hard-cast every entry to
 * String, crashing with ClassCastException on startup. The registry is String-keyed but holds
 * Any values, so: non-String *keys* are dropped (unaddressable), non-String *values* are kept.
 */
@OptIn(KoinInternalApi::class)
class EnvironmentPropertiesTest {

    @Test
    fun saveProperties_tolerates_non_string_entries_issue_2348() {
        val koin = koinApplication { }.koin

        val objectValue = Any()
        val props = Properties().apply {
            setProperty("valid.key", "valid.value")
            // legacy Properties permits arbitrary objects:
            put("object.value", objectValue) // String key, Any value -> kept (values are Any)
            put(Any(), Any())                 // non-string key -> dropped
            put(42, "string.value")           // non-string key -> dropped
        }

        // must NOT throw ClassCastException
        koin.propertyRegistry.saveProperties(props)

        // valid string property is saved
        assertEquals("valid.value", koin.getProperty<String>("valid.key"))
        // a non-String value under a String key is preserved as-is (registry stores Any)
        assertSame(objectValue, koin.getProperty<Any>("object.value"))
    }
}
