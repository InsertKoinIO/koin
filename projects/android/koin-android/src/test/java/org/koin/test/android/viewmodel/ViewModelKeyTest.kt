package org.koin.test.android.viewmodel

import org.junit.Test
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.qualifier.StringQualifier
import org.koin.viewmodel.getViewModelKey
import kotlin.test.assertEquals

class ViewModelKeyTest {

    @OptIn(KoinInternalApi::class)
    @Test
    fun generate_right_key() {
        val q = StringQualifier("_qualifier_")
        val key = "_KEY_"
        val className = "this.is.just.a.class"

        assertEquals(
            null, getViewModelKey(qualifier = null, key = null, className = null)
        )
        assertEquals(
            q.value, getViewModelKey(qualifier = q, key = null, className = null)
        )
        assertEquals(
            q.value+"_"+className, getViewModelKey(qualifier = q, key = null, className = className)
        )
        assertEquals(
            key, getViewModelKey(
                qualifier = q,
                key = key,
                className = null
            )
        )
        assertEquals(
            key, getViewModelKey(qualifier = null, key = key, className = null)
        )

        assertEquals(
            key, getViewModelKey(qualifier = q, key = key, className = className)
        )
    }
}