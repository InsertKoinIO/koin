package org.koin.sample.sandbox.components.mvvm

import androidx.lifecycle.ViewModel
import org.koin.sample.sandbox.components.scope.Session

// use ViewModelScopeFactory
class MyScopeViewModel(val session: Session) : ViewModel()

// not using Archetype here
//class MyScopeViewModel : ViewModel(), KoinScopeComponent {
//
//    override val scope: Scope = viewModelScope()
//    val session by inject<Session>()
//}