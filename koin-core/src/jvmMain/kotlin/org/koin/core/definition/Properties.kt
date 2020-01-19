package org.koin.core.definition

import org.koin.core.error.MissingPropertyException
import java.util.concurrent.ConcurrentHashMap

/**
 * Definitions Properties
 *
 * @author Arnaud Giuliani
 * @author Victor Alenkov
 */
actual class Properties actual constructor(internal actual val data: MutableMap<String, Any>) {
    actual constructor() : this(data = ConcurrentHashMap())

    @Suppress("UNCHECKED_CAST")
    actual operator fun <T> get(key: String): T {
        return data[key] as? T ?: throw MissingPropertyException("missing property for '$key'")
    }

    actual operator fun <T> set(key: String, value: T) {
        data[key] = value as Any
    }

    @Suppress("UNCHECKED_CAST")
    actual fun <T> getOrNull(key: String): T? {
        return data[key] as? T
    }

}
