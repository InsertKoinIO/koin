package org.koin.core

import org.junit.Test
import org.koin.core.logger.GraphLogging

class GraphLoggingTest {

    @Test
    fun `use graph logging`() {
        val graph = GraphLogging()

        graph.start(" a").print()
        graph.cont(" a").print()
        graph.increaseLevel()
        graph.start(" b").print()
        graph.cont(" b").print()
        graph.end(" b").print()
        graph.decreaseLevel()
        graph.end(" a").print()
    }
}

fun String.print() = println(this)