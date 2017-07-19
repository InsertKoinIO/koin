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
        serviceB.process()
    }
}

class OtherServiceA(val processor: Processor) {
    init {
        println("$this ctor OtherServiceA")
    }

    fun doSomethingWithB() {
        println("$this do something in OtherServiceA")
        processor.process()
    }
}

interface Processor {
    fun process()
}

class ServiceB() : Processor {

    init {
        println("$this ctor B")
    }

    override fun process() {
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
        serviceB.process()
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

//Circular defs
class ServiceTwo(val serviceOne: ServiceOne)

class ServiceOne(val serviceTwo: ServiceTwo)