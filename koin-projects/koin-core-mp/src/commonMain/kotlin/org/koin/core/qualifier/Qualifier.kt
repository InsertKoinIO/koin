package org.koin.core.qualifier

/**
 * Help qualify a component
 */
interface Qualifier {
    val value: QualifierValue
}

typealias QualifierValue = String

/**
 * Give a String qualifier
 */
fun named(name: String) = StringQualifier(name)

fun qualifier(name: String) = StringQualifier(name)
fun _q(name: String) = StringQualifier(name)

/**
 * Give a Type based qualifier
 */
inline fun <reified T> named() = TypeQualifier(T::class)

/**
 * Give a Type based qualifier
 */
inline fun <reified T> qualifier() = TypeQualifier(T::class)

/**
 * Give a Type based qualifier
 */
inline fun <reified T> _q() = TypeQualifier(T::class)