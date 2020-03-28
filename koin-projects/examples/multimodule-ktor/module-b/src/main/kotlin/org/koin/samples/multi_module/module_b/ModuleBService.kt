package org.koin.samples.multi_module.module_b

import org.koin.samples.multi_module.common.IService

class ModuleBService : IService {
    override fun sayHello() = "Hello! I am ${javaClass.name}"
}