package org.koin.sample

import io.ktor.server.application.ApplicationCall
import org.koin.sample.Counter.init
import java.util.UUID

class HelloRepository {
    fun getHello(): String = "Ktor & Koin"
}

interface HelloService {
    fun sayHello(): String
}

object Counter {
    var init = 0
}

class HelloServiceImpl(val helloRepository: HelloRepository) : HelloService {
    init {
        println("created at start")
        init++
    }
    override fun sayHello() = "Hello ${helloRepository.getHello()}!"
}

class HelloService2() : HelloService{
    override fun sayHello() = "Hello Again!"
}

class ScopeComponent(val call : ApplicationCall) {
    val id = UUID.randomUUID().toString()
}