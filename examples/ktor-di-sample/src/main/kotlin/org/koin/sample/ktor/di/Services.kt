package org.koin.sample.ktor.di

interface HelloService {
    fun sayHello(): String
}

class HelloServiceImpl : HelloService {
    override fun sayHello(): String = "Hello from Koin!"
}

interface KtorSpecificService {
    fun process(): String
}

class KtorSpecificServiceImpl : KtorSpecificService {
    override fun process(): String = "Processed by Ktor DI"
}