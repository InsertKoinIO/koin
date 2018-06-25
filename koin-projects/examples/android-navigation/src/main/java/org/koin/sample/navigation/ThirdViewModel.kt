package org.koin.sample.navigation

import android.arch.lifecycle.ViewModel

class ThirdViewModel : ViewModel() {
    fun sayHello() = println("Hello 3rd - $this")
}