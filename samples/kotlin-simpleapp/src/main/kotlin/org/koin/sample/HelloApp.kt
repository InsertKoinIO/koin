package org.koin.sample

import org.koin.dsl.module.Module
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import org.koin.standalone.startKoin


// model class to backup "who" property
class HelloModel(val who: String)

// service class to say hello
class HelloService(val helloModel: HelloModel) {
    fun seyHello() = println("Hello ${helloModel.who}")
}

// Koin module
class HelloModule : Module() {
    override fun context() = applicationContext {
        provide { HelloModel(getProperty("WHO")) }
        provide { HelloService(get()) }
    }
}

// HelloApp main class
class HelloApp : KoinComponent {

    // Inject depndency
    val helloService by inject<HelloService>()

    // start Koin and add property "WHO"
    init {
        startKoin(listOf(HelloModule()), properties = mapOf("WHO" to "Koin :)"))
    }

    fun seyHello() {
        helloService.seyHello()
    }
}

fun main(vararg args: String) {
    HelloApp().seyHello()
}