package org.koin.example

import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.logger.Level
import org.koin.dsl.koinApplication
import kotlin.system.measureTimeMillis

class CoffeeApp : KoinComponent {

    val maker: CoffeeMaker by inject()
}

fun main(vararg args: String) {

    koinApplication {
        useLogger(Level.DEBUG)
        loadModules(coffeeAppModule)
    }.start()

    val coffeeShop = CoffeeApp()

    val appDuration = measureTimeMillis {
        coffeeShop.maker.brew()
    }

    println("executed in $appDuration ms")
}
