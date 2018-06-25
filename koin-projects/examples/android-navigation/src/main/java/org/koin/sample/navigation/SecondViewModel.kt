package org.koin.sample.navigation

import android.arch.lifecycle.ViewModel

class SecondViewModel : ViewModel() {
    fun sayHello() = println("Hello 2nd - $this")
}