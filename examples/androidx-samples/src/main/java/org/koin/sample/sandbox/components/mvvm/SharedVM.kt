package org.koin.sample.sandbox.components.mvvm

import androidx.lifecycle.ViewModel
import org.koin.sample.sandbox.components.scope.Session

class SharedVM(val session: Session) : ViewModel()