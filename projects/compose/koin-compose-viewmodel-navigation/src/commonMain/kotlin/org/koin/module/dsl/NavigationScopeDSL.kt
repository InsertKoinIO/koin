package org.koin.module.dsl

import androidx.navigation.NavBackStackEntry
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.module.KoinDslMarker
import org.koin.core.module.Module
import org.koin.core.qualifier.TypeQualifier
import org.koin.dsl.ScopeDSL

/**
 * Defines a navigation scope within a Koin module for use with Jetpack Compose Navigation.
 *
 * This DSL function creates a scope qualified by [NavBackStackEntry], allowing you to define
 * dependencies that are scoped to individual navigation destinations. Dependencies defined
 * within this scope will be created when the navigation destination becomes active and will
 * be destroyed when navigating away.
 *
 * Example usage:
 * ```kotlin
 * module {
 *     navigationScope {
 *         scoped { MyRepository() }
 *     }
 * }
 * ```
 *
 * @param scopeSet The DSL block for defining scoped dependencies using [ScopeDSL].
 *
 * @see NavBackStackEntry
 * @see ScopeDSL
 * @see KoinNavigationScope
 */
@OptIn(KoinInternalApi::class)
@KoinDslMarker
inline fun Module.navigationScope(scopeSet: ScopeDSL.() -> Unit) {
    val qualifier = TypeQualifier(NavBackStackEntry::class)
    ScopeDSL(qualifier, this).apply(scopeSet)
    scopes.add(qualifier)
}
