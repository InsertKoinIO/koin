package org.koin.example

import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.inject
import org.koin.core.time.measureDuration

class CoffeeApp : KoinComponent {

    val maker: CoffeeMaker by inject()
}

fun main() {
    startKoin {
        modules(listOf(coffeeAppModule))
    }
    val coffeeShop = CoffeeApp()
    measureDuration("Make coffee") {
        coffeeShop.maker.brew()
    }
    stopKoin()
}