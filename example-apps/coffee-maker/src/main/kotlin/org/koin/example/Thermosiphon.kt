package org.koin.example

class Thermosiphon(private val heater: Heater) : Pump {
    override fun pump() {
        if (heater.isHot()) {
            println("=> => pumping => =>")
        }
    }
}