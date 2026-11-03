package com.example.app

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import org.koin.core.context.stopKoin

class HolderTest {
    @Test
    fun theHolderOwnsTheLifecycleOfTheViewModel() {
        startForSwift()
        try {
            val first = ProfileViewModelHolder()
            val second = ProfileViewModelHolder()
            assertNotSame(first.viewModel, second.viewModel)
            val viewModel = first.viewModel
            assertEquals(0, viewModel.cleared)
            first.clear()
            assertEquals(1, viewModel.cleared)
            assertEquals(0, second.viewModel.cleared)
        } finally {
            stopKoin()
        }
    }
}
