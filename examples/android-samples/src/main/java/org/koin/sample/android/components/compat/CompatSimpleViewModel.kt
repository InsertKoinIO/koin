package org.koin.sample.android.components.compat

import android.arch.lifecycle.ViewModel
import org.koin.sample.android.components.scope.Session

class CompatSimpleViewModel(val session: Session) : ViewModel()