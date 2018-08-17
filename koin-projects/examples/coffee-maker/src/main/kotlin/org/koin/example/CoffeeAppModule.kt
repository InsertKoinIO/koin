package org.koin.example

import org.koin.dsl.module.module
import org.koin.reflect.single
import org.koin.reflect.singleOf

val coffeeMakerModule = module {
    single<CoffeeMaker>()
    singleOf<Thermosiphon, Pump>()
    singleOf<ElectricHeater, Heater>()
//        single { CoffeeMaker(get()) }
//    single { Thermosiphon(get()) as Pump }
//    single { ElectricHeater() as Heater }
}