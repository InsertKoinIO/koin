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

@Deprecated("Internal function not used anymore")
fun <T : ComponentCallbacks> T.newScope(): Lazy<Scope> = lazy { createScope() }

@Deprecated("Internal function not used anymore")
fun <T : ComponentCallbacks> T.getOrCreateScope(): Lazy<Scope> = lazy { getScopeOrNull() ?: createScope() }
