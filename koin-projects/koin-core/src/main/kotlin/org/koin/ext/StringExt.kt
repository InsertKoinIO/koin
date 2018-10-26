package org.koin.ext

fun Any.checkedStringValue() =
    (this as? String)?.castValue() ?: this

fun String.castValue(): Any {
    return when {
        isInt() -> toInt()
        isFloat() -> toFloat()
        else -> this
    }
}

fun String.isFloat() = this.toFloatOrNull() != null

fun String.isInt() = this.toIntOrNull() != null