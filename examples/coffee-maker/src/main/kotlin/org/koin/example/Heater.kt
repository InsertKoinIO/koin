package org.koin.example

interface Heater {
    fun on()
    fun off()
    fun isHot(): Boolean
}