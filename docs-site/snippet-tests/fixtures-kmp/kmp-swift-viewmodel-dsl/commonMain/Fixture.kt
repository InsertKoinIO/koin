package com.example.app

import androidx.lifecycle.ViewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

// What the page leaves out: the ViewModel. It counts its clears, for the test.
class ProfileViewModel : ViewModel() {
    val title = "Profile"
    var cleared = 0
    override fun onCleared() { cleared++ }
}

val swiftModule = module {
    viewModel<ProfileViewModel>()
}

fun startForSwift() = org.koin.core.context.startKoin { modules(swiftModule) }
