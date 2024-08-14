package org.koin.sample.androidx.compose.data

import java.util.UUID

class MyFactory(_id: String){
    val id = "${UUID.randomUUID()}_$_id"

    init {
        println("$this created '$id'")
    }
}

class MyInnerFactory(_id: String){
    val id = "${UUID.randomUUID()}_$_id"

    init {
        println("$this created '$id'")
    }
}