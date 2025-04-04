package org.koin.sample.sandbox.components.mvvm

import org.koin.viewmodel.scope.ScopeViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.component.inject
import org.koin.sample.sandbox.components.scope.Session

@OptIn(KoinExperimentalAPI::class)
class MyScopeViewModel2 : ScopeViewModel() {

    val session by inject<Session>()

}