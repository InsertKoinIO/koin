package org.koin.example

import org.koin.core.module.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.single

//val coffeeAppModule = module {
//    single { CoffeeMaker(get(), get()) }
//    single<Pump> { Thermosiphon(get()) }
//    single<Heater> { ElectricHeater() }
//}

 val coffeeAppModule = module {
     singleOf(::CoffeeMaker)
     singleOf(::Thermosiphon) bind Pump::class
     singleOf(::ElectricHeater) bind Heater::class
 }