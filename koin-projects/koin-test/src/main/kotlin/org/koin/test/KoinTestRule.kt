package org.koin.test

import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * [TestRule] which will automatically start and stop Koin.
 *
 * @author Nick Cipollo
 * @author Jan Mottl
 */
class KoinTestRule private constructor(private val appDeclaration: KoinAppDeclaration) : TestWatcher() {

    private var _koin: Koin? = null
    val koin: Koin
        get() = _koin ?: error("No Koin application found")

    override fun starting(description: Description?) {
        _koin = startKoin(appDeclaration = appDeclaration).koin
    }

    override fun finished(description: Description?) {
        stopKoin()
        _koin = null
    }

    companion object {
        fun create(appDeclaration: KoinAppDeclaration = {}): KoinTestRule {
            return KoinTestRule(appDeclaration)
        }
    }
}
