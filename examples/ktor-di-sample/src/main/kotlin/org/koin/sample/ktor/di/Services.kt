package org.koin.sample.ktor.di

interface HelloKoinService {
    fun sayHello(): String
}

class HelloKoinServiceImpl : HelloKoinService {
    override fun sayHello(): String = "Hello from Koin!"
}

interface KtorSpecificService {
    fun process(): String
}

class KtorSpecificServiceImpl : KtorSpecificService {
    override fun process(): String = "Processed by Ktor DI"
}