package org.koin.test.mock

import kotlin.reflect.KClass

object MockProvider {
    private var _provider: Provider<*>? = null
    val provider: Provider<*>
        get() {
            return _provider
                ?: error("Missing MockProvider. Please use MockProvider.register() to register a new mock provider")
        }

    fun register(provider: Provider<*>) {
        _provider = provider
    }

    inline fun <reified T : Any> makeMock(): T {
        return provider.invoke(T::class) as T
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> makeMock(kClass: KClass<T>): T {
        return provider.invoke(kClass) as T
    }
}

typealias Provider<T> = (KClass<T>) -> T
