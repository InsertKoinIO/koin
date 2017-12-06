package org.koin.sample

import org.koin.dsl.module.applicationContext
import org.koin.sample.Property.WHO


// Koin module
val HelloModule = applicationContext {
    provide { HelloModel(getProperty(WHO)) }
    provide { HelloServiceImpl(get()) } bind HelloService::class
}

// properties
object Property {
    val WHO = "WHO"
}