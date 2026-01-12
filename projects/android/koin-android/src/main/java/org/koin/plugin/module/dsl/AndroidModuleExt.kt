@file:OptIn(KoinInternalApi::class)

package org.koin.plugin.module.dsl

import org.koin.androidx.scope.ActivityRetainedScopeArchetype
import org.koin.androidx.scope.ActivityScopeArchetype
import org.koin.androidx.scope.FragmentScopeArchetype
import org.koin.core.annotation.KoinInternalApi
import org.koin.dsl.ScopeDSL
import org.koin.core.module.Module

/**
 * Activity scope archetype.
 * Non-inline version for compiler plugin use.
 */
public fun Module.activityScope(scopeSet: ScopeDSL.() -> Unit) {
    ScopeDSL(ActivityScopeArchetype, this).apply(scopeSet)
}

/**
 * Activity retained scope archetype.
 * Non-inline version for compiler plugin use.
 */
public fun Module.activityRetainedScope(scopeSet: ScopeDSL.() -> Unit) {
    ScopeDSL(ActivityRetainedScopeArchetype, this).apply(scopeSet)
}

/**
 * Fragment scope archetype.
 * Non-inline version for compiler plugin use.
 */
public fun Module.fragmentScope(scopeSet: ScopeDSL.() -> Unit) {
    ScopeDSL(FragmentScopeArchetype, this).apply(scopeSet)
}
