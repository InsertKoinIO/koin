package org.koin.sample

import org.koin.sample.Property.WHO
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject

// model class to backup "who" property
data class HelloModel(val who: String)

interface HelloService {
    fun seyHello()
}

// service class to say hello
class HelloServiceImpl(val helloModel: HelloModel) : HelloService {
    override fun seyHello() = println("Hello ${helloModel.who}")
}

// HelloApp component
class HelloApp : KoinComponent {

    // Inject dependency
    val helloService by inject<HelloService>()

    fun seyHello() {
        helloService.seyHello()
    }
}

fun main(vararg args: String) {
    startKoin(listOf(HelloModule()), properties = mapOf(WHO to "Koin :)"))
    HelloApp().seyHello()
}