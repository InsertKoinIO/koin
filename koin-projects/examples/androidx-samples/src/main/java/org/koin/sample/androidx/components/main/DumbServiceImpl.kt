package org.koin.sample.androidx.components.main

class DumbServiceImpl(override val id: String = DUMB_SERVICE) : SimpleService
const val DUMB_SERVICE = "Dumb SimpleService"