package org.koin.core.qualifier

import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

enum class Names1 {
    ENUM_NAME,
}

enum class Names2 {
    ENUM_NAME,
}

class EnumQualifierCollisionTest {

    @Test
    fun enum_qualifiers_with_the_same_constant_name_are_not_equal() {
        val qualifier1 = named(Names1.ENUM_NAME)
        val qualifier2 = named(Names2.ENUM_NAME)

        assertNotEquals(qualifier1, qualifier2)
        assertEquals("names1.enum_name", qualifier1.value)
        assertEquals("names2.enum_name", qualifier2.value)
    }

    @Test
    fun resolves_the_instance_for_the_matching_enum_qualifier() {
        val koin = koinApplication {
            modules(
                module {
                    single(named(Names1.ENUM_NAME)) { "from-names1" }
                    single(named(Names2.ENUM_NAME)) { "from-names2" }
                },
            )
        }.koin

        assertEquals("from-names1", koin.get<String>(named(Names1.ENUM_NAME)))
        assertEquals("from-names2", koin.get<String>(named(Names2.ENUM_NAME)))
    }
}
