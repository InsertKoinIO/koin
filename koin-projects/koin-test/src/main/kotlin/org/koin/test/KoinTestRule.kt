package org.koin.test

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * [TestRule] which will automatically start and stop Koin.
 * @author Nick Cipollo
 */
class KoinTestRule private constructor(val koinApplication: KoinApplication) : TestRule, KoinTest {
    override fun apply(base: Statement, description: Description): Statement =
        object : Statement() {
            override fun evaluate() {
                startKoin(koinApplication)
                base.evaluate()
                stopKoin()
            }
        }

    companion object {
        @JvmStatic
        fun create(koinApplication: KoinApplication) = KoinTestRule(koinApplication)

        fun create(appDeclaration: KoinAppDeclaration): KoinTestRule {
            val koinApplication = KoinApplication.create().apply(appDeclaration)
            return KoinTestRule.create(koinApplication)
        }
    }
}
