package org.koin.samples.multi_module.module_a

import org.koin.samples.multi_module.common.IService

class ModuleAService : IService {
    override fun sayHello() = "Hello! I am ${javaClass.name}"
}