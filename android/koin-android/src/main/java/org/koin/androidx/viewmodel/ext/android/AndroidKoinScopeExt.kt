package org.koin.androidx.viewmodel.ext.android

import org.koin.android.scope.AndroidScopeComponent
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.component.KoinComponent
import org.koin.core.component.KoinScopeComponent
import org.koin.core.context.GlobalContext
import org.koin.core.scope.Scope


@KoinInternalApi
@PublishedApi
internal fun getKoinScope(any : Any): Scope {
    return when (any) {
        is AndroidScopeComponent -> any.scope
        is KoinScopeComponent -> any.scope
        is KoinComponent -> any.getKoin().scopeRegistry.rootScope
        else -> GlobalContext.get().scopeRegistry.rootScope
    }
}