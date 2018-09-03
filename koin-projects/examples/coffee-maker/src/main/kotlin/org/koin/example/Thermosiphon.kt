package org.koin.example

class Thermosiphon(val heater: Heater) : Pump{
    override fun pump() {
        if (heater.isHot()){
            println("=> => pumping => =>")
        }
    }
}