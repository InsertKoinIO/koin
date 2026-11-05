package org.koin.sample

import androidx.lifecycle.ViewModel
import org.koin.mp.KoinPlatform

// LoginViewModel is internal, so the test in :app can only reach it through this function.
fun resolveLoginViewModel(): ViewModel = KoinPlatform.getKoin().get<LoginViewModel>()
