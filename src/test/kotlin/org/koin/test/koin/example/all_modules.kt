package org.koin.test.koin.example

import org.koin.module.Module


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

class SampleModuleC : Module() {
    override fun onLoad() {
        declareContext {
            provide(ServiceA::class)

            provide { ServiceC(get(), get()) }
        }
    }
}

class SampleModuleC_ImportB : Module() {
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