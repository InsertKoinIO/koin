package org.koin.test.koin.example

/**
 * Created by arnaud on 09/06/2017.
 */

class ServiceA(val serviceB: ServiceB) {
    init {
        println("$this ctor A")
    }

    fun doSomethingWithB() {
        println("$this do something in A")
        serviceB.doSomething()
    }
}

class OtherServiceA(val doSomething: DoSomething) {
    init {
        println("$this ctor OtherServiceA")
    }

    fun doSomethingWithB() {
        println("$this do something in OtherServiceA")
        doSomething.doSomething()
    }
}

interface DoSomething {
    fun doSomething()
}

class ServiceB() : DoSomething {

    init {
        println("$this ctor B")
    }

    override fun doSomething() {
        println("$this do something in B")
    }
}

class ServiceC(val serviceA: ServiceA, val serviceB: ServiceB) {
    init {
        println("$this ctor C")
    }

    fun doSomethingWithAll() {
        println("$this do something in C")
        serviceA.doSomethingWithB()
        serviceB.doSomething()
    }
}

class ServiceD(val myVal: String) {

    init {
        println("$this ctor D")
    }

    fun doSomething() {
        println("$this do something in D with $myVal")
    }
}