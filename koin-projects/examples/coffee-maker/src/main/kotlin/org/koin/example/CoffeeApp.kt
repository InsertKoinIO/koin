package org.koin.example

import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.inject
import org.koin.core.logger.Level
import kotlin.system.measureTimeMillis

class CoffeeApp : KoinComponent {

    val maker: CoffeeMaker by inject()
}

fun main() {

    val appDuration = measureTimeMillis {
        startKoin {
            modules(listOf(coffeeAppModule))
        }

        val coffeeShop = CoffeeApp()
        coffeeShop.maker.brew()
    }

    println("executed in $appDuration ms")
}
