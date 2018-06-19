package org.koin.example

import org.koin.standalone.KoinComponent
import org.koin.standalone.StandAloneContext.startKoin
import org.koin.standalone.inject

class CoffeeApp : KoinComponent, Runnable {

    val coffeeMaker: CoffeeMaker by inject()

    override fun run() {
        coffeeMaker.brew()
    }
}

fun main(vararg args: String) {

    startKoin(listOf(coffeeMakerModule))

    CoffeeApp().run()
}
