package org.koin.core.async

import org.koin.core.Koin
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.ext.getFullName

// Sandbox: async singles ride on a regular single of AsyncSingle, keyed by target type + qualifier
@PublishedApi
internal inline fun <reified T> asyncQualifier(qualifier: Qualifier?): Qualifier =
    named("async:${T::class.getFullName()}:${qualifier?.value.orEmpty()}")

inline fun <reified T> Module.singleAsync(
    qualifier: Qualifier? = null,
    noinline definition: SuspendDefinition<T>,
) {
    single(asyncQualifier<T>(qualifier)) { AsyncSingle(this, definition) }
}

suspend inline fun <reified T> Scope.getAsync(qualifier: Qualifier? = null): T =
    get<AsyncSingle<T>>(asyncQualifier<T>(qualifier)).await()

suspend inline fun <reified T> Koin.getAsync(qualifier: Qualifier? = null): T =
    get<AsyncSingle<T>>(asyncQualifier<T>(qualifier)).await()
