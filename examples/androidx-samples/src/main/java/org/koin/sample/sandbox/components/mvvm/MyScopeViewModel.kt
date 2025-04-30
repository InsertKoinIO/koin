package org.koin.sample.sandbox.components.mvvm

import androidx.lifecycle.ViewModel
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.inject
import org.koin.core.scope.Scope
import org.koin.sample.sandbox.components.scope.Session
import org.koin.viewmodel.scope.viewModelScope

// use ViewModelScopeFactory
//class MyScopeViewModel(val session: Session) : ViewModel()

// not using Archetype here
class MyScopeViewModel : ViewModel(), KoinScopeComponent {

    override val scope: Scope = viewModelScope()
    val session by inject<Session>()
}