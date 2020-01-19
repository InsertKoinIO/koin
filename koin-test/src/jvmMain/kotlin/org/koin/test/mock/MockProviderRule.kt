package org.koin.test.mock

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class MockProviderRule private constructor(private val mockProvider: Provider<*>) : TestRule {

    override fun apply(base: Statement, description: Description): Statement =
            object : Statement() {
                override fun evaluate() {
                    MockProvider.register(mockProvider)
                    base.evaluate()
                }
            }

    companion object {
        fun create(mockProvider: Provider<*>): MockProviderRule {
            return MockProviderRule(mockProvider)
        }
    }

}