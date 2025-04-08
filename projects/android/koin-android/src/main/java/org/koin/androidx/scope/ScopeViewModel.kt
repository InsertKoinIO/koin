package org.koin.androidx.scope

import androidx.lifecycle.ViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.component.KoinScopeComponent
import org.koin.core.scope.Scope
import org.koin.viewmodel.scope.viewModelScope

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
@OptIn(KoinExperimentalAPI::class)
@Deprecated("ScopeViewModel has been moved to org.koin.viewmodel.scope.ScopeViewModel (koin-core-viewmodel)", ReplaceWith(expression = "ScopeViewModel()", imports = ["org.koin.viewmodel.scope"]))
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