package org.koin.example

import org.koin.standalone.KoinComponent
import org.koin.standalone.inject


class CoffeeMaker(val pump: Pump) : KoinComponent {

    // Don't want to create a possibly costly heater until we need it.
    val heater: Heater by inject()

    fun brew() {
        heater.on()
        pump.pump()
        println(" [_]P coffee! [_]P ")
        heater.off()
    }
}