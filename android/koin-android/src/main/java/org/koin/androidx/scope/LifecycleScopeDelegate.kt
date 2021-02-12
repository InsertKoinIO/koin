package org.koin.androidx.scope

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import org.koin.core.Koin
import org.koin.core.component.getScopeId
import org.koin.core.component.getScopeName
import org.koin.core.context.GlobalContext
import org.koin.core.context.KoinContext
import org.koin.core.scope.Scope
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class LifecycleScopeDelegate(
        val lifecycleOwner: LifecycleOwner,
        koinContext: KoinContext = GlobalContext,
        createScope: (Koin) -> Scope = { koin: Koin -> koin.createScope(lifecycleOwner.getScopeId(), lifecycleOwner.getScopeName()) },
) : ReadOnlyProperty<LifecycleOwner, Scope> {

    private var _scope: Scope? = null

    init {
        val koin = koinContext.get()
        val logger = koin.logger

        logger.debug("setup scope: $_scope for $lifecycleOwner")
        val scopeId = lifecycleOwner.getScopeId()
        _scope = koin.getScopeOrNull(scopeId) ?: createScope(koin)
        logger.debug("got scope: $_scope for $lifecycleOwner")

        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy(owner: LifecycleOwner) {
                logger.debug("Closing scope: $_scope for $lifecycleOwner")
                if (_scope?.closed == false) {
                    _scope?.close()
                }
                _scope = null
            }
        })
    }

    override fun getValue(thisRef: LifecycleOwner, property: KProperty<*>): Scope {
        return _scope ?: error("can't get Scope for $lifecycleOwner")
    }
}