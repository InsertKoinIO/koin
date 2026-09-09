package org.koin.core

import org.koin.Simple
import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.core.qualifier.QualifierValue
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * KTZ-4829: debug messages must not be built when the logger is above DEBUG.
 *
 * Koin's default logger is EmptyLogger at Level.NONE, yet registration and resolution used to
 * interpolate BeanDefinition.toString() and ResolutionContext.debugTag on every definition and
 * every get(). Logger.debug(String) is a non-inline overload, so the string was built at the
 * call site before the level was ever checked.
 *
 * The probe is a Qualifier that counts its own toString(). It is reached only through
 * BeanDefinition.toString() and ResolutionContext.debugTag — indexKey() reads `value`, not
 * toString() — so a non-zero count means an eager debug string was built.
 */
class LazyLoggingTest {

    private class CountingQualifier(override val value: QualifierValue) : Qualifier {
        var toStringCount = 0

        override fun toString(): String {
            toStringCount++
            return value
        }
    }

    private class CollectingLogger(level: Level) : Logger(level) {
        val messages = mutableListOf<String>()

        override fun display(level: Level, msg: MESSAGE) {
            messages += msg
        }
    }

    // Scope source objects are interpolated by ScopeRegistry ("|- Scope source set ... -> $source"),
    // so a counting source detects eagerness on that path the way the qualifier does on the
    // definition path.
    private class CountingSource {
        var toStringCount = 0

        override fun toString(): String {
            toStringCount++
            return "source"
        }
    }

    // Reaches every rewritten site that interpolates an object: saveMapping and
    // InstanceFactory.create (BeanDefinition.toString), ResolutionContext.debugTag,
    // Koin.createEagerInstances (createdAtStart), PropertyRegistry (properties), ScopeRegistry
    // (createScope with a source) and the Scope.getOrNull catch paths (a miss).
    private fun runScenario(logger: Logger, qualifier: CountingQualifier, source: CountingSource) {
        val koin = koinApplication {
            logger(logger)
            properties(mapOf("key" to "value"))
            modules(
                module {
                    single(qualifier, createdAtStart = true) { Simple.ComponentA() }
                    scope(named("scoped")) { scoped { Simple.ComponentB(get(qualifier)) } }
                },
            )
        }.koin
        koin.get<Simple.ComponentA>(qualifier)
        val scope = koin.createScope("id", named("scoped"), source)
        scope.get<Simple.ComponentB>()
        scope.getOrNull<Simple.ComponentC>()
        scope.close()
        koin.close()
    }

    @Test
    fun no_debug_string_is_built_when_logger_is_at_info() {
        val qualifier = CountingQualifier("counted")
        val source = CountingSource()

        runScenario(CollectingLogger(Level.INFO), qualifier, source)

        assertEquals(
            0,
            qualifier.toStringCount,
            "A debug string was built at INFO level: registration or resolution is still eager",
        )
        assertEquals(0, source.toStringCount, "ScopeRegistry built its scope-source debug string at INFO level")
    }

    @Test
    fun no_debug_string_is_built_with_the_default_logger() {
        val qualifier = CountingQualifier("counted")

        val koin = koinApplication {
            modules(module { single(qualifier) { Simple.ComponentA() } })
        }.koin
        koin.get<Simple.ComponentA>(qualifier)
        koin.close()

        assertEquals(
            0,
            qualifier.toStringCount,
            "A debug string was built with the default EmptyLogger (Level.NONE)",
        )
    }

    // Falsification: the messages must still be produced at DEBUG, so the test above cannot be
    // satisfied by deleting the logging instead of deferring it.
    @Test
    fun debug_strings_are_still_built_when_logger_is_at_debug() {
        val qualifier = CountingQualifier("counted")
        val source = CountingSource()
        val logger = CollectingLogger(Level.DEBUG)

        runScenario(logger, qualifier, source)

        assertTrue(
            qualifier.toStringCount > 0,
            "No debug string was built at DEBUG level: the logging was removed, not deferred",
        )
        assertTrue(source.toStringCount > 0, "The scope-source debug message is missing at DEBUG level")
        assertTrue(
            logger.messages.any { it.contains("(+) index") },
            "The registration debug message is missing at DEBUG level",
        )
        assertTrue(
            logger.messages.any { it.contains("(+) '[Singleton:") },
            "The instance-creation debug message is missing at DEBUG level",
        )
    }
}
