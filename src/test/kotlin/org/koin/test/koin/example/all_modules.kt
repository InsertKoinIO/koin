package org.koin.test.koin.example

import org.koin.dsl.module.Module


/**
 * Created by arnaud on 09/06/2017.
 */
class SampleModuleA_C : Module() {
    override fun context() =
            declareContext {
                provide { ServiceA(get()) }
                provide { ServiceC(get(), get()) }
            }
}

class SampleModuleB : Module() {
    override fun context() =
            declareContext {
                provide { ServiceB() }
            }
}

class SampleModuleD : Module() {
    override fun context() =
            declareContext {
                provide { ServiceD(getProperty<String>("myVal")) }
            }
}