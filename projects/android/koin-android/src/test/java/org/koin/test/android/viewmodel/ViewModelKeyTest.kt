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
        val koin = koinApplication().koin
        val root = koin.scopeRegistry.rootScope

        val q = StringQualifier("_qualifier_")
        val scope = Scope(StringQualifier("_q_"), id = "_id_", _koin = koin, isRoot = false)
        val key = "_KEY_"

        assertEquals(
            null, getViewModelKey(qualifier = null, scope = root, key = null)
        )
        assertEquals(
            q.value, getViewModelKey(qualifier = q, scope = root, key = null)
        )
        assertEquals(
            key, getViewModelKey(qualifier = null, scope = root, key = key)
        )
        assertEquals(
            scope.id, getViewModelKey(qualifier = null, scope = scope, key = null)
        )

        assertEquals(
            key + scope.id, getViewModelKey(qualifier = null, scope = scope, key = key)
        )

        assertEquals(
            q.value + key + scope.id, getViewModelKey(qualifier = q, scope = scope, key = key)
        )
    }
}