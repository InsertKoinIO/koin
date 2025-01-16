package org.koin.core

import org.koin.core.parameter.ParametersHolder
import kotlin.test.Test
import kotlin.test.assertEquals

class ParametersTest {

    @Test
    fun `test thread-safety in concurrent access`() {
        val holder = ParametersHolder(mutableListOf(1, 2, 3))
        val threads = mutableListOf<Thread>()

        for (i in 1..10) {
            threads.add(Thread {
                holder.add(i)
            })
        }

        threads.forEach { it.start() }
        threads.forEach { it.join() }
        assertEquals(13, holder.size(), "ParametersHolder should contain all elements added concurrently.")
    }

}