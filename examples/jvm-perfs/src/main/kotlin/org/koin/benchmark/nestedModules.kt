package org.koin.benchmark

import org.koin.core.annotation.KoinInternalApi
import org.koin.core.module.Module
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

internal fun buildNestedModule(
    depth: Int,
    width: Int,
): List<Module> {
    val matrix = List(depth) { r ->
        List(width) { c ->
            val q = qualifier("q_${r}_$c")
            module {
                single(q) { A1() }
                single(q) { A2() }
                single(q) { A3() }
                single(q) { A4() }
                single(q) { A5() }
                single(q) { A6() }
                single(q) { A7() }
                single(q) { A8() }
                single(q) { A9() }
                single(q) { A10() }
                single(q) { A11() }
                single(q) { A12() }
                single(q) { A13() }
                single(q) { A14() }
                single(q) { A15() }
                single(q) { A16() }
                single(q) { A17() }
                single(q) { A18() }
                single(q) { A19() }
                single(q) { A20() }
                single(q) { A21() }
                single(q) { A22() }
                single(q) { A23() }
                single(q) { A24() }
                single(q) { A25() }
                single(q) { A26() }
                single(q) { A27() }
                single(q) { A28() }
                single(q) { A29() }
                single(q) { A30() }
                single(q) { A31() }
                single(q) { A32() }
                single(q) { A33() }
                single(q) { A34() }
                single(q) { A35() }
                single(q) { A36() }
                single(q) { A37() }
                single(q) { A38() }
                single(q) { A39() }
                single(q) { A40() }
                single(q) { A41() }
                single(q) { A42() }
                single(q) { A43() }
                single(q) { A44() }
                single(q) { A45() }
                single(q) { A46() }
                single(q) { A47() }
                single(q) { A48() }
                single(q) { A49() }
                single(q) { A50() }
            }
        }
    }

    // Random a row that should be included in all modules.
    val level0Index = matrix.indices.random()
    val level0 = matrix[level0Index]

    matrix.forEachIndexed { r, modules ->
        modules.forEachIndexed { c, module ->
            // Let each module include the one above it.
            matrix
                .getOrNull(r + 1)
                ?.get(c)
                ?.let { module.includes(it) }

            // Don't include itself.
            if (r != level0Index) {
                module.includes(level0)
            }
        }
    }

    return matrix.last()
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