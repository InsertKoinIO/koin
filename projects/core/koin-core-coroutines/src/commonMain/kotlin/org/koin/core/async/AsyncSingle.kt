package org.koin.core.async

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.koin.core.scope.Scope
import kotlin.concurrent.Volatile
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

typealias SuspendDefinition<T> = suspend Scope.() -> T

private val UNINITIALIZED = Any()

/**
 * Suspend singleton holder: runs its definition in the caller's coroutine, once.
 * Failures and cancellations aren't cached; recursive requests from the same creation chain fail fast.
 */
class AsyncSingle<T>(
    private val scope: Scope,
    definition: SuspendDefinition<T>,
) {
    private var definition: SuspendDefinition<T>? = definition
    private val mutex = Mutex()

    @Volatile
    private var value: Any? = UNINITIALIZED

    fun isInitialized(): Boolean = value !== UNINITIALIZED

    suspend fun await(): T {
        val cached = value
        if (cached !== UNINITIALIZED) {
            @Suppress("UNCHECKED_CAST")
            return cached as T
        }

        // Mutex isn't reentrant: a self-request would wait on itself forever
        check(currentCoroutineContext()[AsyncCreation]?.contains(this) != true) {
            "Circular dependency: async single requested while it is still being created"
        }

        return mutex.withLock {
            val lockedValue = value
            if (lockedValue !== UNINITIALIZED) {
                @Suppress("UNCHECKED_CAST")
                lockedValue as T
            } else {
                val created = withAsyncCreation(this) { definition!!.invoke(scope) }
                value = created
                definition = null
                created
            }
        }
    }
}

private class AsyncCreation(
    private val owner: AsyncSingle<*>,
    private val parent: AsyncCreation?,
) : AbstractCoroutineContextElement(Key) {
    // Children launched during creation inherit this element and may outlive it
    @Volatile
    private var active = true

    companion object Key : CoroutineContext.Key<AsyncCreation>

    fun deactivate() {
        active = false
    }

    fun contains(owner: AsyncSingle<*>): Boolean {
        var current: AsyncCreation? = this
        while (current != null) {
            if (current.active && current.owner === owner) return true
            current = current.parent
        }
        return false
    }
}

private suspend fun <T> withAsyncCreation(owner: AsyncSingle<*>, block: suspend () -> T): T {
    val creation = AsyncCreation(owner, currentCoroutineContext()[AsyncCreation])
    return try {
        withContext(creation) { block() }
    } finally {
        creation.deactivate()
    }
}
