package org.koin.sample

import org.koin.dsl.module.Module
import org.koin.sample.Property.WHO


// Koin module
class HelloModule : Module() {
    override fun context() = applicationContext {
        provide { HelloModel(getProperty(WHO)) }
        provide { HelloServiceImpl(get()) } bind HelloService::class
    }
}

// properties
object Property {
    val WHO = "WHO"
}