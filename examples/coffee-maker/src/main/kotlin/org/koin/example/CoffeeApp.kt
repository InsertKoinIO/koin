package org.koin.example

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import kotlin.time.DurationUnit
import kotlin.time.measureTime

class CoffeeApp : KoinComponent {
    val maker: CoffeeMaker by inject()
}

fun main() {
    startKoin {
        printLogger(Level.INFO)
        modules(coffeeAppModule)
    }

    val coffeeShop = CoffeeApp()
    measureDuration("Got Coffee") {
        coffeeShop.maker.brew()
    }
}

fun measureDuration(msg : String, code: () -> Unit) {
    val duration = measureTime(code)
    val durationInMillis = duration.toDouble(DurationUnit.MILLISECONDS)

    println("$msg in $durationInMillis ms")
}