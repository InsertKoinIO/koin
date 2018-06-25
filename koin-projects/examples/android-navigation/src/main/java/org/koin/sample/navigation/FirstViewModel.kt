package org.koin.sample.navigation

import android.arch.lifecycle.ViewModel

class FirstViewModel : ViewModel() {
    fun sayHello() = println("Hello 1st - $this")
}