package org.koin.android.ext.koin

import org.koin.java.KoinJavaComponent

/**
 * Wrapped call to retrieve given dependency lazily, without explicit type specification.
 */
inline fun <reified T> inject(): Lazy<T> {
    return KoinJavaComponent.inject(T::class.java)
}

/**
 * Wrapped call to retrieve given dependency lazily if available, without explicit type specification.
 */
inline fun <reified T> injectOrNull(): Lazy<T?> {
    return KoinJavaComponent.injectOrNull(T::class.java)
}

/**
 * Wrapped call to retrieve given dependency, without explicit type specification.
 */
inline fun <reified T> get(): T {
    return KoinJavaComponent.get(T::class.java)
}

/**
 * Wrapped call to retrieve given dependency if available, without explicit type specification.
 */
inline fun <reified T> getOrNull(): T? {
    return KoinJavaComponent.getOrNull(T::class.java)
}

/**
 * Wrapped call to retrieve given dependency, without explicit type specification.
 * @param clazz - dependency class
 */
inline fun <reified T> get(clazz:Class<T>): T {
    return KoinJavaComponent.get(clazz)
}

/**
 * Wrapped call to retrieve given dependency if available, without explicit type specification.
 * @param clazz - dependency class
 */
inline fun <reified T> getOrNull(clazz:Class<T>): T? {
    return KoinJavaComponent.getOrNull(clazz)
}