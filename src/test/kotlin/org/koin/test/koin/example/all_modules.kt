package org.koin.test.koin.example

import org.koin.module.Module


/**
 * Created by arnaud on 09/06/2017.
 */
class SampleModuleA_C : Module() {
    override fun onLoad() {
        declareContext {
            provide { ServiceA(get()) }
            provide { ServiceC(get(), get()) }
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