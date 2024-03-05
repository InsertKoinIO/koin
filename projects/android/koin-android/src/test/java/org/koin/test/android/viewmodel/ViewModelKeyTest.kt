package org.koin.test.android.viewmodel

import org.junit.Test
import org.koin.androidx.viewmodel.getViewModelKey
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.qualifier.StringQualifier
import org.koin.core.scope.Scope
import org.koin.dsl.koinApplication
import kotlin.test.assertEquals

class ViewModelKeyTest {

    @OptIn(KoinInternalApi::class)
    @Test
    fun generate_right_key() {
        val q = StringQualifier("_qualifier_")
        val key = "_KEY_"

        assertEquals(
            null, getViewModelKey(qualifier = null,  key = null)
        )
        assertEquals(
            q.value, getViewModelKey(qualifier = q, key = null)
        )
        assertEquals(
            q.value + "_$key", getViewModelKey(qualifier = q, key = key)
        )
        assertEquals(
            key, getViewModelKey(qualifier = null, key = key)
        )
    }
}