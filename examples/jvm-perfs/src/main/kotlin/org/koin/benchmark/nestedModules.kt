package org.koin.benchmark

import org.koin.core.annotation.KoinInternalApi
import org.koin.core.module.Module
import org.koin.dsl.module

internal fun buildNestedModule(
    depth: Int,
    width: Int,
): List<Module> {
    val matrix = List(depth) { List(width) { module {} } }

    val level0Index = matrix.indices.random()
    val level0 = matrix[level0Index]

    matrix.forEachIndexed { r, modules ->
        modules.forEachIndexed { c, module ->
            // Let each module include the one above it.
            matrix
                .getOrNull(r + 1)
                ?.get(c)
                ?.let { module.includes(it) }

            if (r != level0Index) {
                module.includes(level0)
            }
        }
    }

    return matrix.first()
}

@OptIn(KoinInternalApi::class)
internal fun flattenIterative(modules: List<Module>): Set<Module> {
    // This is essentially a DFS traversal of the module graph,
    // but we're using a stack instead of recursion to avoid stack overflows and performance overhead.

    val flatten = HashSet<Module>()
    val stack = ArrayDeque(modules)

    while (stack.isNotEmpty()) {
        val current = stack.removeLast()

        // If the module is already in the set, that means we've already visited it, so we can skip it.
        if (!flatten.add(current)) {
            continue
        }

        // Add all the included modules to the stack if they haven't been visited yet.
        for (module in current.includedModules) {
            if (module !in flatten) {
                stack += module
            }
        }
    }

    return flatten
}