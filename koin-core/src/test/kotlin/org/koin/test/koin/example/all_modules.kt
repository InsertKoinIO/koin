package org.koin.test.koin.example

import org.koin.dsl.context.Context
import org.koin.dsl.module.Module


/**
 * Created by arnaud on 09/06/2017.
 */
class SampleModuleA_C : Module() {
    override fun context() =
            applicationContext {
                provide { ServiceA(get()) }
                provide { ServiceC(get(), get()) }
            }
}

class SampleModuleB : Module() {
    override fun context() =
            applicationContext {
                provide { ServiceB() }
            }
}

class SampleModuleD : Module() {
    override fun context() =
            applicationContext {
                provide { ServiceD(getProperty("myVal")) }
            }
}

class ScopedModuleB : Module() {
    override fun context() =
            applicationContext {
                provide { ServiceB() }
            }
}

class MultiDeclareA : Module() {
    override fun context() =
            applicationContext {
                provide { ServiceB() }
                provide("A1") { ServiceA(get()) }
                provide("A2") { ServiceA(get()) }
            }
}

class ConflictingModule : Module() {
    override fun context() =
            applicationContext {
                provide { ServiceB() }
                provide { ServiceB() }
            }
}

class ConflictingDependency : Module() {
    override fun context() =
            applicationContext {
                provide("B1") { ServiceB() }
                provide("B2") { ServiceB() }
                provide { ServiceA(get()) }
            }
}

class CleanMultiDependency : Module() {
    override fun context() =
            applicationContext {
                provide("B1") { ServiceB() }
                provide("B2") { ServiceB() }
                provide { ServiceA(get("B1")) }
            }
}


class ScopedModuleA : Module() {
    override fun context() =
            applicationContext {
                provide { ServiceA(get()) }
            }
}

class ScopedModuleA_B : Module() {
    override fun context() =
            applicationContext {
                provide { ServiceB() }
            }
}

class SampleModuleC : Module() {
    override fun context(): Context = applicationContext {
        provide { ServiceC(get(), get()) }
    }
}

class SampleModuleOA : Module() {
    override fun context() = applicationContext {
        provide { OtherServiceA(get()) }
    }
}

class BindModuleB : Module() {
    override fun context() = applicationContext {
        provide { ServiceB() } bind (Processor::class)
    }
}

class GenericProducerModule : Module() {
    override fun context() = applicationContext {
        provide { ProducerImpl() } bind (GenericProducer::class)
    }
}