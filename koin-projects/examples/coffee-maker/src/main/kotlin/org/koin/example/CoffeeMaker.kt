package org.koin.example


class CoffeeMaker(val pump: Pump, val lazyHeater: Lazy<Heater>) {

    // Don't want to create a possibly costly heater until we need it.
    val heater: Heater by lazy { lazyHeater.value }

    fun brew() {
        heater.on()
        pump.pump()
        println(" [_]P coffee! [_]P ")
        heater.off()
    }
}