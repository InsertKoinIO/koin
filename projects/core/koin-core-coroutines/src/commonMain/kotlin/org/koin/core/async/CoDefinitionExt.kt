package org.koin.core.async

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import org.koin.core.Koin
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.coroutine.KoinCoroutinesEngine
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.ext.getFullName

// Sandbox: suspend definitions ride on a regular definition of AsyncInstance, keyed by target type + qualifier
@PublishedApi
internal inline fun <reified T> asyncQualifier(qualifier: Qualifier?): Qualifier =
    named("async:${T::class.getFullName()}:${qualifier?.value.orEmpty()}")

inline fun <reified T> Module.coSingle(
    qualifier: Qualifier? = null,
    noinline definition: SuspendDefinition<T>,
) {
    single<AsyncInstance<T>>(asyncQualifier<T>(qualifier)) { AsyncSingle(this, definition) }
}

inline fun <reified T> Module.coFactory(
    qualifier: Qualifier? = null,
    noinline definition: SuspendDefinition<T>,
) {
    factory<AsyncInstance<T>>(asyncQualifier<T>(qualifier)) { AsyncFactory(this, definition) }
}

/** Resolves a suspend definition from the current coroutine, running it in the caller's context. */
suspend inline fun <reified T> Scope.await(qualifier: Qualifier? = null): T =
    get<AsyncInstance<T>>(asyncQualifier<T>(qualifier)).await()

suspend inline fun <reified T> Koin.await(qualifier: Qualifier? = null): T =
    get<AsyncInstance<T>>(asyncQualifier<T>(qualifier)).await()

/** Resolves a suspend definition from non-coroutine code: runs in the Koin coroutines engine. */
inline fun <reified T> Koin.getAsync(qualifier: Qualifier? = null): Deferred<T> =
    coroutinesEngineOrCreate().async { await<T>(qualifier) }

@OptIn(KoinInternalApi::class)
@PublishedApi
internal fun Koin.coroutinesEngineOrCreate(): CoroutineScope =
    extensionManager.getExtensionOrNull<KoinCoroutinesEngine>(KoinCoroutinesEngine.EXTENSION_NAME)
        ?: KoinCoroutinesEngine().also { extensionManager.registerExtension(KoinCoroutinesEngine.EXTENSION_NAME, it) }
