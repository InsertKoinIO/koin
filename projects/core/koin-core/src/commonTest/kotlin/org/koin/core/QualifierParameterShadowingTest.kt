package org.koin.core

import org.koin.core.logger.Level
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Regression test for issue #2370 / #2408 / #2393.
 *
 * CoreResolverV2 consults the stacked-parameter stack BEFORE the registry, and the
 * stacked-parameter lookup matches by type only (ignoring the qualifier). As a result a
 * qualified dependency (`get(named(...))`) of the same type as a value passed via
 * `parametersOf(...)` is silently shadowed by the parameter.
 *
 * Worked in 4.0.0 / 3.5.6, broke in 4.1.1+ (resolver order change).
 */
class QualifierParameterShadowingTest {

    private val configQualifier = named("Config")
    private val activityScope = named("TestScope")

    private val globalConfig = "CONFIG_STRING"

    private class Service(val config: String)

    private data class UseCase(val parameter: String, val service: Service)

    private val testModule = module {
        single(configQualifier) { globalConfig }

        factory { Service(config = get(configQualifier)) }

        factory { (parameterString: String) ->
            UseCase(parameter = parameterString, service = get())
        }

        scope(activityScope) { }
    }

    @Test
    fun qualified_string_is_not_shadowed_by_parametersOf_value() {
        val koin = koinApplication {
            printLogger(Level.DEBUG)
            modules(testModule)
        }.koin

        val scope = koin.createScope("test", activityScope)

        val useCase: UseCase = scope.get { parametersOf("parameterString") }

        // The parameter must reach UseCase.parameter
        assertEquals("parameterString", useCase.parameter)

        // ...but Service.config must come from the qualified definition, NOT the parameter.
        // Bug: useCase.service.config == "parameterString"
        assertEquals(globalConfig, useCase.service.config)
    }
}
