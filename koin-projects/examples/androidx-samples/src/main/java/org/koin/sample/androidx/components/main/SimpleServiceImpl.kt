package org.koin.sample.androidx.components.main

class SimpleServiceImpl(override val id: String = SERVICE_IMPL) : SimpleService
const val SERVICE_IMPL = "DefaultService"