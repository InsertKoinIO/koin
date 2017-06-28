package org.koin.test.koin.example

import org.koin.module.Module


/**
 * Created by arnaud on 09/06/2017.
 */
class SampleModuleA : Module() {
    override fun onLoad() {
        Context {
            provide { ServiceA(get()) }
        }
    }
}

class SampleModuleA_C : Module() {
    override fun onLoad() {
        Context {
            provide { ServiceA(get()) }
            provide { ServiceC(get(), get()) }
        }
    }
}

class SampleModuleB : Module() {
    override fun onLoad() {
        Context {
            provide { ServiceB() }
        }
    }
}

class SampleModuleD : Module() {
    override fun onLoad() {
        Context {
            provide { ServiceD(getProperty<String>("myVal")) }
        }
    }
}