package org.koin.example

class Thermosiphon(private val heater: Heater) : Pump {

    val a = "a"

    override fun pump() {
        if (heater.isHot()) {
            println("=> => pumping => =>")
        }
    }
}