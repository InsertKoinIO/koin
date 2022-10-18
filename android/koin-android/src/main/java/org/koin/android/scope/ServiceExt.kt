package org.koin.android.scope

import android.app.Service
import org.koin.android.ext.android.getKoin
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.component.getScopeId
import org.koin.core.component.getScopeName
import org.koin.core.scope.Scope

fun Service.createServiceScope(): Scope {
    if (this !is AndroidScopeComponent) {
        error("Service should implement AndroidScopeComponent")
    }
    val koin = getKoin()
    val scope =
        koin.getScopeOrNull(getScopeId()) ?: koin.createScope(getScopeId(), getScopeName(), this)
    return scope
}

fun Service.destroyServiceScope() {
    if (this !is AndroidScopeComponent) {
        error("Service should implement AndroidScopeComponent")
    }
    scope.close()
}

fun Service.serviceScope() = lazy { createServiceScope() }

/**
 * Create new scope
 */
@KoinInternalApi
fun Service.createScope(source: Any? = null): Scope = getKoin().createScope(getScopeId(), getScopeName(), source)
@KoinInternalApi
fun Service.getScopeOrNull(): Scope? = getKoin().getScopeOrNull(getScopeId())