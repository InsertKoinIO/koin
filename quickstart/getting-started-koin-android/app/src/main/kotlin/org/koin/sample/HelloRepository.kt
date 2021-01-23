package org.koin.sample

/**
 * Repository to provide a "Hello" data
 */

interface HelloRepository {
    fun giveHello(): String
}

class HelloRepositoryImpl() : HelloRepository {
    override fun giveHello() = "Hello Koin"
}