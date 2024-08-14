package org.koin.sample.sandbox.components.mvvm

import androidx.lifecycle.ViewModel
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.scope.Scope
import org.koin.sample.sandbox.components.scope.Session

class MyScopeViewModel : ViewModel(), KoinScopeComponent {

    override val scope: Scope = createScope(this)

    val session by scope.inject<Session>()

    override fun onCleared() {
        super.onCleared()
        scope.close()
    }
}