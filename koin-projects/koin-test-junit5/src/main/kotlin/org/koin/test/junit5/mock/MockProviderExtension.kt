package org.koin.test.junit5.mock

import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.koin.test.mock.MockProvider
import org.koin.test.mock.Provider


class MockProviderExtension private constructor(private val mockProvider: Provider<*>) : BeforeEachCallback {

    companion object {
        fun create(mockProvider: Provider<*>): MockProviderExtension {
            return MockProviderExtension(mockProvider)
        }
    }

    override fun beforeEach(context: ExtensionContext?) {
        MockProvider.register(mockProvider)
    }
}