package org.koin.android.scope

import android.app.Service
import org.koin.android.ext.android.getKoin
import org.koin.core.component.getScopeId
import org.koin.core.component.getScopeName

fun Service.createServiceScope() {
    if (this !is AndroidScopeComponent) {
        error("Service should implement AndroidScopeComponent")
    }
    val koin = getKoin()
    val scope =
        koin.getScopeOrNull(getScopeId()) ?: koin.createScope(getScopeId(), getScopeName(), this)
    this.scope = scope
}

fun Service.destroyServiceScope() {
    if (this !is AndroidScopeComponent) {
        error("Service should implement AndroidScopeComponent")
    }
    this.scope?.close()
    this.scope = null
}