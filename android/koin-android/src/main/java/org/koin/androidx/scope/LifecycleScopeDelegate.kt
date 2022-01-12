package org.koin.androidx.scope

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import org.koin.core.Koin
import org.koin.core.component.getScopeId
import org.koin.core.component.getScopeName
import org.koin.core.scope.Scope
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class LifecycleScopeDelegate<T>(
    val lifecycleOwner: LifecycleOwner,
    private val koin: Koin,
    private val createScope: (Koin) -> Scope = { koin: Koin ->
        koin.createScope(
            lifecycleOwner.getScopeId(),
            lifecycleOwner.getScopeName(),
            lifecycleOwner as T
        )
    },
) : ReadOnlyProperty<LifecycleOwner, Scope> {

    private var _scope: Scope? = null

    init {
        val logger = koin.logger

        logger.debug("setup scope: $_scope for $lifecycleOwner")
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                createScope()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                logger.debug("Closing scope: $_scope for $lifecycleOwner")
                if (_scope?.closed == false) {
                    _scope?.close()
                }
                _scope = null
            }
        })
    }

    private fun createScope() {
        if (_scope == null) {
            koin.logger.debug("Create scope: $_scope for $lifecycleOwner")
            val scopeId = lifecycleOwner.getScopeId()
            _scope = koin.getScopeOrNull(scopeId) ?: createScope(koin)
        }
    }

    override fun getValue(thisRef: LifecycleOwner, property: KProperty<*>): Scope {
        return if (_scope == null) {
            createScope()
            return _scope ?: error("can't get Scope for $lifecycleOwner")
        }
        else {
            _scope ?: error("can't get Scope for $lifecycleOwner")
        }
    }
}

internal fun LifecycleOwner.isActive(): Boolean {
    val ownerState = lifecycle.currentState
    return ownerState.isAtLeast(Lifecycle.State.CREATED)
}
