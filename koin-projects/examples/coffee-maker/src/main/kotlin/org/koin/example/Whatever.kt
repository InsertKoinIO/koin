package org.koin.example

import org.koin.ksp.KoinInject
import java.lang.ref.WeakReference

/**
 * @author Fabio de Matos
 * @since 21/04/2021.
 */
@KoinInject
class Whatever {

    fun what(): String {
        return "this"
    }

    fun <T> who() {

    }

    val propp = "ertiy"

    lateinit var teest : WeakReference<List<Int >>
}