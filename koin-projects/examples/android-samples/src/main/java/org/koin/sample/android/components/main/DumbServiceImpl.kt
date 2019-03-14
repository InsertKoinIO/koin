package org.koin.sample.android.components.main

class DumbServiceImpl(override val id: String = DUMB_SERVICE) : Service
const val DUMB_SERVICE = "Dumb Service"