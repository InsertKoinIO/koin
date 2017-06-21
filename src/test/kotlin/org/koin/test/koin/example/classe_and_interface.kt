package org.koin.test.koin.example

import org.junit.Assert
import org.junit.Test
import org.koin.Koin

/**
 * Created by arnaud on 01/06/2017.
 */

interface MyInterface {
    fun doSomething()
}

class MyService : org.koin.test.koin.example.MyInterface {
    override fun doSomething() {
        println("do nothing")
    }
}