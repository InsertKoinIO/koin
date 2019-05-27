package org.koin.sample.android.components.mvvm

import android.arch.lifecycle.ViewModel
import org.koin.sample.android.components.scope.Session

class ExtSimpleViewModel(val session: Session) : ViewModel()