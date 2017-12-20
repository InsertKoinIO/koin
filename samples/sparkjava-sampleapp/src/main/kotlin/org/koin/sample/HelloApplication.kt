package org.koin.sample

import org.koin.dsl.module.applicationContext
import org.koin.sample.util.start
import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject
import spark.kotlin.get

val module = applicationContext {
    bean { HelloServiceImpl(get()) as HelloService }
    bean { HelloRepository() }
}

class HelloRepository {
    fun getHello(): String = "Spark & Koin"
}

interface HelloService {
    fun sayHello(): String
}

class HelloServiceImpl(val helloRepository: HelloRepository) : HelloService {
    override fun sayHello() = "Hello ${helloRepository.getHello()} !"
}

class HelloController : KoinComponent {

    val service: HelloService by inject()

    init {
        get("/hello") {
            service.sayHello()
        }
    }
}

fun main(vararg args: String) {
    start {
        startKoin(listOf(module))
        HelloController()
    }
}