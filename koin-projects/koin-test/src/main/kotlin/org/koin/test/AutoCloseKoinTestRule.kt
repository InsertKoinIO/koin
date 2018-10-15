package org.koin.test

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.koin.core.context.stopKoin

/**
 * [TestRule] which will automatically close Koin after each test completes.
 * @author Nick Cipollo
 */
class AutoCloseKoinTestRule : TestRule, KoinTest {
    override fun apply(base: Statement, description: Description): Statement =
        object : Statement() {
            override fun evaluate() {
                base.evaluate()
                stopKoin()
            }
        }
}
