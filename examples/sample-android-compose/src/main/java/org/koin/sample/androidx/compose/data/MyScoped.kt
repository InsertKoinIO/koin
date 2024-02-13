package org.koin.sample.androidx.compose.data

import java.util.UUID

class MyScoped() {
    val id = UUID.randomUUID().toString()

    init {
        println("$this created '$id'")
    }
}