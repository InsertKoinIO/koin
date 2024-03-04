package org.koin.androidx.scope

import androidx.lifecycle.ViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.scope.Scope

@KoinExperimentalAPI
abstract class ScopeViewModel : ViewModel(), KoinScopeComponent {

    override val scope: Scope = createScope(this)

    override fun onCleared() {
        super.onCleared()
        scope.close()
    }
}