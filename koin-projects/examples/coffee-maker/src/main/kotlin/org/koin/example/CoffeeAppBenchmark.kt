package org.koin.example

import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.time.measureDuration

fun main() {
    run()
    run()
}

/*
    start - 46.904948 ms
    ~ ~ ~ heating ~ ~ ~
    => => pumping => =>
    [_]P coffee! [_]P
    Make coffee - 1.8768 ms
    ~ ~ ~ heating ~ ~ ~
    => => pumping => =>
    [_]P coffee! [_]P
    Make coffee - 0.066816 ms
    start - 0.08282 ms
    ~ ~ ~ heating ~ ~ ~
    => => pumping => =>
    [_]P coffee! [_]P
    Make coffee - 0.082464 ms
    ~ ~ ~ heating ~ ~ ~
    => => pumping => =>
    [_]P coffee! [_]P
    Make coffee - 0.045618 ms
 */

private fun run() {
    measureDuration("start") {
        startKoin {
            modules(listOf(coffeeAppModule))
        }
    }
    val coffeeShop = CoffeeApp()
    measureDuration("Make coffee") {
        coffeeShop.maker.brew()
    }
    measureDuration("Make coffee") {
        coffeeShop.maker.brew()
    }
    stopKoin()
}