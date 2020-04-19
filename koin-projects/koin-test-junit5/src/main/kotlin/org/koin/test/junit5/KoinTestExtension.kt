package org.koin.test.junit5

import org.junit.jupiter.api.extension.*
import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * [TestExtension] which will automatically start and stop Koin.
 * @author Marko Pukari
 */
class KoinTestExtension private constructor(private val appDeclaration: KoinAppDeclaration):
        BeforeEachCallback, AfterEachCallback, ParameterResolver {

    override fun beforeEach(context: ExtensionContext) {
        val koin = startKoin(appDeclaration = appDeclaration).koin
        context.store().put(Koin::class.simpleName, koin)
    }

    override fun afterEach(context: ExtensionContext) {
        context.store().remove(Koin::class.simpleName)
        stopKoin()
    }

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean {
        return parameterContext.parameter.type == Koin::class.java
    }

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Any {
        return extensionContext.store().get(Koin::class.simpleName)
    }

    private fun ExtensionContext.store(): ExtensionContext.Store {
        return this.getStore(KOIN_STORE)
    }

    companion object {
        fun create(appDeclaration: KoinAppDeclaration): KoinTestExtension {
            return KoinTestExtension(appDeclaration)
        }

        private val KOIN_STORE: ExtensionContext.Namespace  = ExtensionContext.Namespace.create("KOIN_STORE")
    }

}
