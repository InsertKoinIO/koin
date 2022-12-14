package org.koin.sample

class HelloRepository {
    fun getHello(): String = "Ktor & Koin"
}

interface HelloService {
    fun sayHello(): String
}

class HelloServiceImpl(val helloRepository: HelloRepository) : HelloService {
    override fun sayHello() = "Hello ${helloRepository.getHello()}!"
}

class HelloService2() : HelloService{
    override fun sayHello() = "Hello Again!"
}