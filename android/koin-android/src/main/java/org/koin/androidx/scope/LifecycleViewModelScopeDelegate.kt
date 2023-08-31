package org.koin.androidx.scope

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import org.koin.core.Koin
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.component.getScopeName
import org.koin.core.scope.Scope
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

//TODO Deprecate
@OptIn(KoinInternalApi::class)
class LifecycleViewModelScopeDelegate(
        private val lifecycleOwner: ComponentActivity,
        private val koin : Koin,
        private val createScope: (Koin) -> Scope = { k: Koin -> k.createScope(lifecycleOwner.getScopeName().value, lifecycleOwner.getScopeName()) },
) : ReadOnlyProperty<LifecycleOwner, Scope> {

    private var _scope: Scope? = null

    init {
        val scopeViewModel = lifecycleOwner.viewModels<ScopeHandlerViewModel>().value

        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                if (scopeViewModel.scope == null) {
                    scopeViewModel.scope = createScope(koin)
                }
                _scope = scopeViewModel.scope
            }
        })
    }

    override fun getValue(thisRef: LifecycleOwner, property: KProperty<*>): Scope {
        return if (_scope != null) _scope!!
        else {
            if (!thisRef.isActive()) {
                error("can't get Scope for $lifecycleOwner - LifecycleOwner is not Active")
            } else {
                _scope = koin.getScopeOrNull(lifecycleOwner.getScopeName().value) ?: createScope(koin)
                koin.logger.debug("got scope: $_scope for $lifecycleOwner")
                _scope!!
            }
        }
    }

    private fun LifecycleOwner.isActive(): Boolean {
        val ownerState = lifecycle.currentState
        return ownerState.isAtLeast(Lifecycle.State.CREATED)
    }
}