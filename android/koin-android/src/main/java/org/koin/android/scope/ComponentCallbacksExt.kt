package org.koin.android.scope

import android.content.ComponentCallbacks
import org.koin.android.ext.android.getKoin
import org.koin.core.component.getScopeId
import org.koin.core.component.getScopeName
import org.koin.core.scope.Scope


fun <T : ComponentCallbacks> T.createScope(source: Any? = null): Scope {
    return getKoin().createScope(getScopeId(), getScopeName(), source)
}

fun <T : ComponentCallbacks> T.getScopeOrNull(): Scope? {
    return getKoin().getScopeOrNull(getScopeId())
}

fun <T : ComponentCallbacks> T.newScope() = lazy { createScope() }
fun <T : ComponentCallbacks> T.getOrCreateScope() = lazy { getScopeOrNull() ?: createScope() }