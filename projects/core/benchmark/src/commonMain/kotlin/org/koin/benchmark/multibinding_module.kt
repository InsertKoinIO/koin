package org.koin.benchmark

import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

// Metro-shaped multibinding workload (KTZ-4829).
//
// The Metro benchmark maps every contribution to `@Singleton(binds = [Iface::class])` and
// consumes them with `koin.getAll<Plugin>()`. That is the shape reproduced here: a concrete
// primary type plus a marker secondary type, realized through getAll.
//
// Qualifiers keep every definition distinct on purpose. Loading the same module N times (as
// HeavyStartupBenchmark does) collides on identical index keys, so _instances stays flat and
// the O(N) registry scan in InstanceRegistry.getAll is never actually exercised. Scaling
// `count` is the only way to measure whether getAll is O(k) or O(total definitions).
//
// Per `count`: 3 index entries (contributor primary + marker secondary + filler) and
// 1 getAll match. count = 1800 lands at 5400 entries / 1800 matches, close to the Metro
// fingerprint of ~5500 entries and ~1800 realized contributions.

interface ScaleMarker

class ScaleContributor(val index: Int) : ScaleMarker

// Non-matching definitions, so the getAll scan walks misses as well as hits.
class ScaleFiller(val index: Int)

// One unqualified definition, so the warm-resolution timer exercises the full fall-through
// (injected params -> stacked params -> registry) rather than the registry-only path a
// qualified lookup takes.
class ScaleRoot

fun multibindingModule(count: Int) = module {
    single { ScaleRoot() }
    repeat(count) { i ->
        single(named("c$i")) { ScaleContributor(i) } bind ScaleMarker::class
        single(named("f$i")) { ScaleFiller(i) }
    }
}
