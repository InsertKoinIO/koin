package org.koin.test.android.viewmodel.ext.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.androidx.viewmodel.ext.android.lazyViewModelForClass
import org.koin.core.context.startKoin
import org.koin.dsl.module

class ViewModelLazyTest {
    @Test
    fun `lazyViewModelForClass shouldn't resolve ViewModelStoreOwner immediately`() {
        // Given
        startKoin {
            modules(
                module {
                    viewModelOf(::FooViewModel)
                }
            )
        }

        var resolved = false
        val viewModelStoreOwnerResolver: () -> ViewModelStoreOwner = {
            resolved = true
            mockk(relaxed = true)
        }

        // When
        val lazy: Lazy<FooViewModel> = lazyViewModelForClass(viewModelStoreOwnerLazy = viewModelStoreOwnerResolver)

        // Then
        assertFalse(resolved)
        lazy.value
        assertTrue(resolved)
    }

    private class FooViewModel : ViewModel()
}