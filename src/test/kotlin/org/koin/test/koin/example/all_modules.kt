package org.koin.test.koin.example

import org.koin.module.Module
import org.koin.test.ServiceA
import org.koin.test.ServiceB
import org.koin.test.ServiceC
import org.koin.test.ServiceD


/**
 * Created by arnaud on 09/06/2017.
 */
class SampleModuleA : Module() {
    override fun onLoad() {
        declareContext {
            provide(ServiceA::class)
        }
    }
}

class SampleModuleAC : Module() {
    override fun onLoad() {
        declareContext {
            import(SampleModuleB::class)

            provide(ServiceA::class)

            provide { ServiceC(get(), get()) }
            // or
            // provide(ServiceC::class)
        }
    }
}

class SampleModuleB : Module() {
    override fun onLoad() {
        declareContext {
            provide { ServiceB() }
        }
    }
}

class SampleModuleD : Module() {
    override fun onLoad() {
        declareContext {
            provide { ServiceD(getProperty<String>("myVal")) }
        }
    }
}