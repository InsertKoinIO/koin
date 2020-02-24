package org.koin.test

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * [TestRule] which will automatically start and stop Koin.
 * @author Nick Cipollo
 */
class KoinTestRule private constructor(private val appDeclaration: KoinAppDeclaration) : TestRule {

    var _koin: Koin? = null
    val koin: Koin
        get() = _koin ?: error("No Koin application found")

    override fun apply(base: Statement, description: Description): Statement =
            object : Statement() {
                override fun evaluate() {
                    _koin = startKoin(appDeclaration = appDeclaration).koin
                    base.evaluate()
                    stopKoin()
                    _koin = null
                }
            }

    companion object {
        fun create(appDeclaration: KoinAppDeclaration): KoinTestRule {
            return KoinTestRule(appDeclaration)
        }
    }
}
