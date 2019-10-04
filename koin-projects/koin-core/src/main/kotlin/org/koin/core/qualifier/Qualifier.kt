package org.koin.core.qualifier

/**
 * Help qualify a component
 */
interface Qualifier

/**
 * Give a String qualifier
 */
fun named(name: String) = StringQualifier(name)

/**
 * Give a Type based qualifier
 */
inline fun <reified T> named() = TypeQualifier(T::class)

/**
 * Give a enum based qualifier
 */
fun <E : Enum<E>> qualifier(qual: E) = EnumQualifier(qual)
