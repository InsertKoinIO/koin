package org.koin.android.ext.android

import android.content.ComponentCallbacks
import org.koin.android.scope.AndroidScopeComponent
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.KoinScopeComponent
import org.koin.core.context.GlobalContext
import org.koin.core.scope.Scope


@KoinInternalApi
fun ComponentCallbacks.getKoinScope(): Scope {
    return when (this) {
        is AndroidScopeComponent -> scope
        is KoinScopeComponent -> scope
        is KoinComponent -> getKoin().scopeRegistry.rootScope
        else -> GlobalContext.get().scopeRegistry.rootScope
    }
}