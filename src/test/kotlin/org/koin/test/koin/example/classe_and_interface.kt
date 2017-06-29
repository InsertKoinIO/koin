package org.koin.test.koin.example

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