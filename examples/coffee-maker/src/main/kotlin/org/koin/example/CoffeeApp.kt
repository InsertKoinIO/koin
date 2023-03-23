package org.koin.example

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.time.measureDuration

class CoffeeApp : KoinComponent {
    val maker: CoffeeMaker by inject()
}

fun main() {
    startKoin {
        printLogger(Level.INFO)
        modules(coffeeAppModule)
    }

    val coffeeShop = CoffeeApp()
    val duration = measureDuration {
        coffeeShop.maker.brew()
    }
    println("Got Coffee in $duration ms")
}