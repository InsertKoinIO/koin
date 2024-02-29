package org.koin.test.androidx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.CreationExtras
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.koin.androidx.viewmodel.resolveViewModel
import org.koin.core.annotation.KoinInternalApi
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope

@KoinInternalApi
class GetViewModelTest {

    private val viewModelStore: ViewModelStore = mockk(relaxed = true)
    private val extras: CreationExtras = mockk()
    private val qualifier: Qualifier = mockk()
    private val scope: Scope = mockk()

    @Test
    fun `should return a ViewModel with a default Android key`() {
        val classToTest = ViewModel::class
        every { scope.isRoot } returns true

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
        val scopeId = "scopeId"

        every { scope.isRoot } returns false
        every { scope.id } returns scopeId
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

        val koinVmKey = "$qualifierName$key$scopeId"

        assertNotNull(viewModel)
        verify { viewModelStore[koinVmKey] }
    }

    @Test
    fun `should return a ViewModel considering only a non root scope identifier`() {
        val classToTest = ViewModel::class
        val scopeId = "scopeId"

        every { scope.isRoot } returns false
        every { scope.id } returns scopeId

        val viewModel = resolveViewModel(
            classToTest,
            viewModelStore,
            null,
            extras,
            null,
            scope,
            null
        )

        assertNotNull(viewModel)
        verify { viewModelStore[scopeId] }
    }

    @Test
    fun `should return a ViewModel considering a key and a root scope identifier`() {
        val classToTest = ViewModel::class
        val key = "key"

        every { scope.isRoot } returns true

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

        every { scope.isRoot } returns true
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
        verify { viewModelStore[qualifierValue] }
    }

    companion object {
        const val DEFAULT_ANDROID_VIEW_MODEL_KEY =
            "androidx.lifecycle.ViewModelProvider.DefaultKey"
    }
}