package org.koin.sample.sandbox.components.main

class SimpleServiceImpl() : SimpleService {
    override val id: String = SERVICE_IMPL
}
const val SERVICE_IMPL = "DefaultService"