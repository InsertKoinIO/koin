package org.koin.test.androidx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.CreationExtras
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.koin.androidx.viewmodel.getViewModelKey
import org.koin.androidx.viewmodel.resolveViewModel
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.dsl.koinApplication

@KoinInternalApi
class GetViewModelTest {

    private val viewModelStore: ViewModelStore = mockk(relaxed = true)
    private val extras: CreationExtras = mockk()
    private val qualifier: Qualifier = mockk()
    private val koin = koinApplication {  }.koin
    private val scope: Scope = koin.scopeRegistry.rootScope

    @Test
    fun `should return a ViewModel with a default Android key`() {
        val classToTest = ViewModel::class

        val firstViewModel = resolveViewModel(
            classToTest,
            viewModelStore,
            null,
            extras,
            null,
            scope,
            null
        )

        val expectedKey = "$DEFAULT_ANDROID_VIEW_MODEL_KEY:${classToTest.qualifiedName}"

        assertNotNull(firstViewModel)
        verify { viewModelStore[expectedKey] }
    }

    @Test
    fun `should return a ViewModel considering a specific key, qualifier and a non root scope`() {
        val classToTest = ViewModel::class
        val qualifierName = "qualifier"
        val key = "key"

        every { qualifier.value } returns qualifierName

        val viewModel = resolveViewModel(
            classToTest,
            viewModelStore,
            key,
            extras,
            qualifier,
            scope,
            null
        )

        val koinVmKey = "${qualifierName}_$key"

        assertNotNull(viewModel)
        verify { viewModelStore[key] }
    }

    @Test
    fun `should return a ViewModel considering a key and a root scope identifier`() {
        val classToTest = ViewModel::class
        val key = "key"

        val viewModel = resolveViewModel(
            classToTest,
            viewModelStore,
            key,
            extras,
            null,
            scope,
            null
        )

        assertNotNull(viewModel)
        verify { viewModelStore[key] }
    }

    @Test
    fun `should return a ViewModel considering a qualifier and a root scope identifier`() {
        val classToTest = ViewModel::class
        val qualifierValue = "qualifier"

        every { qualifier.value } returns qualifierValue

        val viewModel = resolveViewModel(
            classToTest,
            viewModelStore,
            null,
            extras,
            qualifier,
            scope,
            null
        )

        assertNotNull(viewModel)
        verify { viewModelStore[qualifierValue+"_"+classToTest.java.canonicalName] }
    }

    companion object {
        const val DEFAULT_ANDROID_VIEW_MODEL_KEY =
            "androidx.lifecycle.ViewModelProvider.DefaultKey"
    }
}