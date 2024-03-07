package org.koin.sample.sandbox.components.mvvm

import org.koin.androidx.scope.ScopeViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.sample.sandbox.components.scope.Session

@OptIn(KoinExperimentalAPI::class)
class MyScopeViewModel2 : ScopeViewModel() {

    val session by scope.inject<Session>()

}