package org.koin.androidx.scope

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import org.koin.core.Koin
import org.koin.core.component.getScopeName
import org.koin.core.scope.Scope
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class LifecycleViewModelScopeDelegate(
        val lifecycleOwner: LifecycleOwner,
        private val koin : Koin,
        private val createScope: (Koin) -> Scope = { koin: Koin -> koin.createScope(lifecycleOwner.getScopeName().value, lifecycleOwner.getScopeName()) },
) : ReadOnlyProperty<LifecycleOwner, Scope> {

    private var _scope: Scope? = null

    private val scopeId = lifecycleOwner.getScopeName().value

    init {
        assert(lifecycleOwner is ComponentActivity){ "$lifecycleOwner should be a ComponentActivity" }

        val logger = koin.logger

        logger.debug("setup scope: $_scope for $lifecycleOwner")
        _scope = koin.getScopeOrNull(scopeId) ?: createScope(koin)
        logger.debug("got scope: $_scope for $lifecycleOwner")

        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                logger.debug("Attach ViewModel scope: $_scope to $lifecycleOwner")
                val scopeViewModel : ScopeHandlerViewModel = (lifecycleOwner as ComponentActivity).viewModels<ScopeHandlerViewModel>().value
                if (scopeViewModel.scope == null) {
                    scopeViewModel.scope = _scope
                }
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
}
