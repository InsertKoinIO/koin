package org.koin.core.instance

private const val ERROR_SEPARATOR = "\n\t"

@Suppress("NOTHING_TO_INLINE")
internal actual inline fun Exception.getExceptionStack(): String {
    return toString() + ERROR_SEPARATOR + stackTrace.takeWhile { !it.className.contains("sun.reflect") }
        .joinToString(ERROR_SEPARATOR)
}
