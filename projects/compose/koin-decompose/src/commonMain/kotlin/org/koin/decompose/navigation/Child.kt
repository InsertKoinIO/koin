package org.koin.decompose.navigation

import androidx.compose.runtime.Composable

/**
 * Encapsulates a composable to be able to propagate through the navigation stack
 *
 * @author Juan Luis Berenquel
 */
interface Child {
    @Composable
    fun render()
}

/**
 * Default implementation of [Child] that renders a composable
 *
 * @param composable the composable to render
 *
 * @author Juan Luis Berenguel
 */
open class DefaultChild(
    private val composable: @Composable () -> Unit
) : Child {

    @Composable
    override fun render() {
        composable()
    }
}
