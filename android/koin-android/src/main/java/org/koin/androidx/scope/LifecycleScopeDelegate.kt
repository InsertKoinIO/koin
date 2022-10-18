package org.koin.androidx.scope

import androidx.lifecycle.LifecycleOwner
import org.koin.core.Koin
import org.koin.core.component.getScopeId
import org.koin.core.component.getScopeName
import org.koin.core.scope.Scope
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

//TODO Deprecate
class LifecycleScopeDelegate<T>(
    private val lifecycleOwner: LifecycleOwner,
    private val koin: Koin,
    @Suppress("UNCHECKED_CAST") private val createScope: (Koin) -> Scope = { k: Koin ->
        k.createScope(
            lifecycleOwner.getScopeId(),
            lifecycleOwner.getScopeName(),
            lifecycleOwner as T
        )
    },
) : ReadOnlyProperty<LifecycleOwner, Scope> {

    private var _scope: Scope? = null

    init {
        _scope = createScope(koin)
        lifecycleOwner.registerScopeForLifecycle(_scope!!)
    }

    override fun getValue(thisRef: LifecycleOwner, property: KProperty<*>): Scope {
        return if (_scope == null) {
            _scope = createScope(koin)
            return _scope ?: error("can't get Scope for $lifecycleOwner")
        } else {
            _scope ?: error("can't get Scope for $lifecycleOwner")
        }
    }
}
