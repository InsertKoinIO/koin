@file:Suppress("CONTEXT_RECEIVERS_DEPRECATED")

package org.koin.viewmodel.scope

import androidx.lifecycle.ViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.scope.Scope

/**
 * Class to help support Koin Scope in a ViewModel
 * create directly a scope instance for current ViewModel
 *
 * allow to intercept before scope closing with `onCloseScope`, to be overriden
 *
 * Destroy linked scope with `onCleared`
 *
 * @author Arnaud Giuliani
 */
@KoinExperimentalAPI
abstract class ScopeViewModel : ViewModel(), KoinScopeComponent {

    override val scope: Scope = viewModelScope()

    /**
     * To override to add behavior before closing Scope
     */
    open fun onCloseScope(){}

    override fun onCleared() {
        onCloseScope()
        scope.close()
        super.onCleared()
    }
}

/**
 * Create a ViewModel Scope as ViewModelScope archetype for given ViewModel
 */
@KoinExperimentalAPI
fun KoinScopeComponent.viewModelScope() : Scope {
    if (this !is ViewModel) {
        error("$this should implement ViewModel() class")
    }
    return createScope(source = this, scopeArchetype = ViewModelScopeArchetype)
}