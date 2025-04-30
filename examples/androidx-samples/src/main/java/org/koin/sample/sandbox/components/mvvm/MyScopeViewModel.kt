package org.koin.sample.sandbox.components.mvvm

import androidx.lifecycle.ViewModel
import org.koin.sample.sandbox.components.scope.Session

// use ViewModelScopeFactory
class MyScopeViewModel(val session: Session) : ViewModel()

// not using Archetype here
//class MyScopeViewModel(val session: Session) : ViewModel(), KoinScopeComponent {
//
//    override val scope: Scope = createScope(this)
//    val session by inject<Session>()
//
//    override fun onCleared() {
//        super.onCleared()
//        scope.close()
//    }
//}