package org.koin.core

import org.koin.Simple
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.StringQualifier
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Regression test for #2386. Mutating a definition's qualifier/secondaryTypes via withOptions
 * added new index entries but never removed the originals, leaving stale dual-index entries:
 * the definition stayed reachable under its OLD qualifier, and secondary types were indexed
 * only under the new qualifier (asymmetry). After the fix, the old coordinates must be dropped.
 */
class WithOptionsStaleIndexTest {

    @Test
    fun qualifier_change_drops_stale_primary_index_2386() {
        val koin = koinApplication {
            modules(
                module {
                    single(named("a")) { Simple.ComponentA() } withOptions { qualifier = StringQualifier("b") }
                },
            )
        }.koin

        // reachable under the new qualifier
        assertNotNull(koin.getOrNull<Simple.ComponentA>(named("b")))
        // NOT reachable under the old qualifier (stale index removed)
        assertNull(koin.getOrNull<Simple.ComponentA>(named("a")))
    }

    @Test
    fun qualifier_change_keeps_primary_and_secondary_reachability_consistent_2386() {
        val koin = koinApplication {
            modules(
                module {
                    single(named("a")) { Simple.Component1() } withOptions {
                        qualifier = StringQualifier("b")
                        secondaryTypes = listOf(Simple.ComponentInterface1::class)
                    }
                },
            )
        }.koin

        // primary + secondary both reachable under the NEW qualifier
        assertNotNull(koin.getOrNull<Simple.Component1>(named("b")))
        assertNotNull(koin.getOrNull<Simple.ComponentInterface1>(named("b")))
        // neither reachable under the OLD qualifier — consistent (no asymmetry)
        assertNull(koin.getOrNull<Simple.Component1>(named("a")))
        assertNull(koin.getOrNull<Simple.ComponentInterface1>(named("a")))
    }
}
