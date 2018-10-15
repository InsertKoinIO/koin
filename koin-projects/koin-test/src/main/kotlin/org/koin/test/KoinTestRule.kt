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
class KoinTestRule private constructor(private val appDeclaration: KoinAppDeclaration) : TestRule, KoinTest {
    private var app: KoinApplication? = null

    override fun apply(base: Statement, description: Description): Statement =
            object : Statement() {
                override fun evaluate() {
                    app = startKoin(appDeclaration)
                    base.evaluate()
                    stopKoin()
                    app = null
                }
            }

    fun koinApplication() = app ?: throw IllegalStateException("Koin application not available outside of test run")

    companion object {
        fun create(appDeclaration: KoinAppDeclaration): KoinTestRule {
            return KoinTestRule(appDeclaration)
        }
    }
}
